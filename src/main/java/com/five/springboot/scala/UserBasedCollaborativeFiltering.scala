package com.five.springboot.scala

import java.sql.{Connection, DriverManager, PreparedStatement, SQLException}
import java.util.Properties
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SQLContext, SparkSession}

/**
 * 基于用户的协同过滤算法的scala实现
 */
object UserBasedCollaborativeFiltering {

  def main(args: Array[String]): Unit = {
    collaborativeFilteringRecommend(16)
  }

  def collaborativeFilteringRecommend(recommendNum: Int): Unit = {

    System.setProperty("HADOOP_USER_NAME", "root")//更改访问hdfs用户名，需要写在sparkContext建立之前
    val spark: SparkSession = SparkSession.builder().appName("userBasedCollaborativeFiltering").master("local").getOrCreate()
    val sc = spark.sparkContext
    val sqlContext = new SQLContext(sc)
    sc.setLogLevel("WARN")

    val properties = new Properties()
    properties.setProperty("user","root")
    properties.put("password","123456")
    val url = "jdbc:mysql://172.16.29.94:3306/news?serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8"

    //1.获取用户新闻rating数据
    val resource = spark.read.jdbc(url,"(SELECT userId,newsId,((isComment*0.5+0.5)*60 + (40/(DATEDIFF(NOW(),browsingTime)+1))) as rating FROM browsingHistory) T",properties).toDF()
    resource.cache()//将数据进行缓存，提高计算效率

    //2.计算用户相似度矩阵
    val item_sim: RDD[(String, (String, Double))] = getCosineSimilarity(spark, resource)
    item_sim.cache()//将数据进行缓存，提高计算效率

    //3.为每个用户提取最相近的40个用户
    val item_sim_rdd = item_sim.filter(f => f._2._2 > 0.05).groupByKey().map(
      f => {
        val item = f._1
        val items_score = f._2.toList
        val items_score_desc = items_score.sortWith((x, y) => x._2 > y._2)
        (item, items_score_desc.take(40))
      }).collectAsMap()

    //4.广播相似度矩阵，提高计算效率
    val item_sim_bd: Broadcast[scala.collection.Map[String, List[(String, Double)]]] = sc.broadcast(item_sim_rdd)

    //5.为用户生成推荐列表
    val recTopN: RDD[(String, List[(String, Double)])] = recommend(spark, resource, item_sim_bd, recommendNum)

    //6.将推荐结果存到数据库里
    recTopN.foreachPartition(iterator => iterator.foreach(item_sim=>
    {
      val driver: String = "com.mysql.jdbc.Driver"
      val username: String = "root"
      val password: String = "123456"
      var conn :Connection = null
      var ps1 :PreparedStatement = null
      var ps2 :PreparedStatement = null
      try {
        Class.forName(driver)//classLoader,加载对应驱动
        conn = DriverManager.getConnection(url, username, password).asInstanceOf[Connection]
        conn.setAutoCommit(false)

        val sql1 = "DELETE FROM personalRecommend WHERE userId=" + item_sim._1
        ps1 = conn.prepareStatement(sql1)
        val sql2 = "INSERT INTO personalRecommend(userId,newsId,scores) VALUES (?,?,?)"
        ps2 = conn.prepareStatement(sql2)//批量导入ps一定要写在循环外面
        item_sim._2.foreach(tuple => {
          ps2.setString(1,item_sim._1)
          ps2.setString(2,tuple._1)
          ps2.setDouble(3,tuple._2)
          ps2.addBatch()
        })
        ps1.execute()
        ps2.executeBatch()
        conn.commit()
        ps2.clearBatch()

      } catch {
        case e: ClassNotFoundException =>
          System.err.println("need driver class !")
        case e: SQLException =>
          e.printStackTrace()
      } finally {
        if (ps1 != null && ps2 != null) {
          ps1.close()
          ps2.close()
        }
        if (conn != null) {
          conn.close()
        }
      }
    }))

  }

  /* 获取原始数据源
   * @param spark SparkSession
   * @return 原始数据的dataFrame
   */
  def getResource(spark: SparkSession) = {
    /*val hiveContext = new HiveContext(sc)
    import hiveContext.sql
    val resource = sql("select "
      + "uid,"
      + "aid,"
      + "cnt"
      + " from " + table + " where dt ='" + day + "'")
    resource*/
  }

  /**
   * 分布式计算余弦相似度
   * --------------------------------
   * user1     user2
   * item1  score11   score21 (X)
   * item2  score12   score22 (Y)
   * --------------------------------
   * sim(item1,item2) = XY / math.sqrt(XX) * math.sqrt(YY)
   * XY= score11 * score12 + score21 * score22
   * XX = score11 * score11 + score21 * score21
   * YY = score12 * score12 + score22 * score22
   * @param spark SparkSession
   * @param resource DataFrame
   * @return RDD[(item1,item2,sim)]
   * */
  def getCosineSimilarity(spark: SparkSession, resource: DataFrame): RDD[(String, (String, Double))] = {
    import spark.implicits._
    val rating = resource.map {
      row => {
        val uid = row.getLong(0).toString
        val aid = row.getLong(1).toString
        val score = row.getDecimal(2).doubleValue()
        (uid, aid, score)
      }
    }.rdd
    val user_item_score = rating.map(f => (f._1, (f._2, f._3)))//RDD[(uid,(aid,score))]
     //1.提取每个用户有过行为的item键值对,即RDD[((aid1,aid2),(score11,score22))]
    val item_score_pair = user_item_score.join(user_item_score)
      .map(f => ((f._2._1._1, f._2._2._1), (f._2._1._2, f._2._2._2)))
     //2.提取同一对item，所有的用户评分向量的点积，即XY 及 XX 及 YY
     /**
     * RDD[((aid1,aid2),score11 * score12 + score21 * score22)]
     * 及 RDD[((aid1,aid1),score11 * score11 + score21 * score21)]
     * 及 RDD[((aid2,aid2),score12 * score12 + score22 * score22)]*/
    val item_pair_ALL = item_score_pair.map(f => (f._1, f._2._1 * f._2._2)).reduceByKey(_ + _)
     //3.提取每个item，所有用户的自向量的点积，即XX或YY
     /**
     * RDD[((aid1,aid1),score11 * score11 + score21 * score21)]
     * 或 RDD[((aid2,aid2),score12 * score12 + score22 * score22)]*/
    val item_pair_XX_YY = item_pair_ALL.filter(f => f._1._1 == f._1._2)
     //4.提取每个item，所有用户的非自向量的点积，即XY ,RDD[((aid1,aid2),score11 * score12 + score21 * score22)]
    val item_pair_XY = item_pair_ALL.filter(f => f._1._1 != f._1._2)
     //5.提取item_pair_XX_YY中的item及XX或YY
     /**
     * RDD[(aid1,score11 * score11 + score21 * score21)]
     * 或 RDD[(aid2,score12 * score12 + score22 * score22)]*/
    val item_XX_YY = item_pair_XX_YY.map(f => (f._1._1, f._2))
     //6.转化item_pair_XY为(aid1,((aid1,aid2,XY),XX)))
     /* *
     *  RDD[(aid1,((aid1,aid2,score11 * score12 + score21 * score22),score11 * score11 + score21 * score21)))]*/
    val item_XY_XX = item_pair_XY.map(f => (f._1._1, (f._1._1, f._1._2, f._2))).join(item_XX_YY)
     //7.转为item_XY_XX为(aid2,((aid1,aid2,XY,XX),YY))
     /**
     *  RDD[(aid2,((aid1,aid2,score11 * score12 + score21 * score22,score11 * score11 + score21 * score21),score12 * score12 + score22 * score22))]*/
    val item_XY_XX_YY = item_XY_XX.map(f => (f._2._1._2, (f._2._1._1, f._2._1._2, f._2._1._3, f._2._2))).join(item_XX_YY)
     //8.提取item_XY_XX_YY中的(aid1,aid2,XY,XX,YY))
     /**
     *  RDD[(aid1,aid2,score11 * score12 + score21 * score22,score11 * score11 + score21 * score21,score12 * score12 + score22 * score22)]*/
    val item_pair_XY_XX_YY = item_XY_XX_YY.map(f => (f._2._1._1, f._2._1._2, f._2._1._3, f._2._1._4, f._2._2))
     //9.转化item_pair_XY_XX_YY为(aid1,aid2,XY / math.sqrt(XX * YY))
     /**
     *  RDD[(aid1,aid2,score11 * score12 + score21 * score22 / math.sqrt((score11 * score11 + score21 * score21)*(score12 * score12 + score22 * score22))]*/
    val item_pair_sim = item_pair_XY_XX_YY.map(f => (f._1, (f._2, f._3 / math.sqrt(f._4 * f._5))))
    item_pair_sim
  }

  /**
   * 基于item相似度矩阵为user生成topN推荐列表
   * @param spark SparkSession
   * @param resource DataFrame
   * @param item_sim_bd Broadcast[scala.collection.Map[String, List[(String, Double)]]]
   * @param topN Int
   * @return RDD[(user,List[(item,score)])]*/
  def recommend(spark: SparkSession, resource: DataFrame, item_sim_bd: Broadcast[scala.collection.Map[String, List[(String, Double)]]], topN: Int = 16) = {
    import spark.implicits._
    val user_item_score = resource.map(
      row => {
        val uid = row.getLong(0).toString
        val aid = row.getLong(1).toString
        val score = row.getDecimal(2).doubleValue()
        ((uid, aid), score)
      }
    )
     /** 提取item_sim_user_score为((user,item2),sim * score)
     * RDD[(user,item2),sim * score]*/
    val user_item_simscore = user_item_score.flatMap(
      f => {
        val items_sim = item_sim_bd.value.getOrElse(f._1._2, List(("0", 0.0)))
        for (w <- items_sim) yield ((f._1._1, w._1), w._2 * f._2)
      }).filter(_._2 > 0.03).rdd

     /** 聚合user_item_simscore为 (user,（item2,sim1 * score1 + sim2 * score2）)
     * 假设user观看过两个item,评分分别为score1和score2，item2是与user观看过的两个item相似的item,相似度分别为sim1，sim2
     * RDD[(user,item2),sim1 * score1 + sim2 * score2）)]*/
    val user_item_rank = user_item_simscore.reduceByKey(_ + _, 1000)
     /** 过滤用户已看过的item,并对user_item_rank基于user聚合
     * RDD[(user,CompactBuffer((item2,rank2）,(item3,rank3)...))]*/
    val user_items_ranks = user_item_rank.subtractByKey(user_item_score.rdd).map(f => (f._1._1, (f._1._2, f._2))).groupByKey()
     /** 对user_items_ranks基于rank降序排序，并提取topN,其中包括用户已观看过的item
     * RDD[(user,ArrayBuffer((item2,rank2）,...,(itemN,rankN)))]*/
    val user_items_ranks_desc = user_items_ranks.map(f => {
      val item_rank_list = f._2.toList
      val item_rank_desc = item_rank_list.sortWith((x, y) => x._2 > y._2)
      (f._1, item_rank_desc.take(topN))
    })
    user_items_ranks_desc
  }

}

