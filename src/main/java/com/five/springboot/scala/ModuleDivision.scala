package com.five.springboot.scala

import java.net.URI
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, FileUtil, Path}
import org.apache.spark.sql._
import org.apache.spark.sql.SparkSession

/**
 * 模块划分
 *
 */

class ModuleDivision {

  case class News(summary: String, comments: String, comments_num: String, content: String, date: String,heat: String,
                  module_id: String, new_url: String, picture_urls: String, read_num: String, source: String, title: String, url: String)

  def main(args: Array[String]): Unit = {
    moduleDivision("2020-7-20 10:55:00")
  }

  def moduleDivision(maxTime: String): Unit = {
    System.setProperty("HADOOP_USER_NAME", "root")//更改访问hdfs用户名，需要写在sparkContext建立之前
    val spark = SparkSession.builder().appName("ModuleDivision").master("local").getOrCreate()
    val sc = spark.sparkContext
    val sqlContext = new SQLContext(sc)
    sc.setLogLevel("WARN")
    val root = "hdfs://172.16.29.94:9000/shixun/csv/"
    val fileName = maxTime.replace(" ","-").replace(":","-")

    //1.读取爬取到的新闻数据
    val news: DataFrame = sqlContext.read.format("csv")
      .option("header","true").option("encoding","GBK").option("delimiter",",")
      .load(root + fileName + ".csv")
    import spark.implicits._//rdd 的泛型是User，转为DF 表头自动带出来

    //2.根据新闻内容进行模块划分
    val newsRDD = news.filter(row => !row.isNullAt(3))
      .map(row => {
      if(row.getString(3).contains("政务") || row.getString(3).contains("习近平") || row.getString(3).contains("疫情") ||
        row.getString(3).contains("新冠") || row.getString(3).contains("教育") || row.getString(3).contains("人大")) {
        News(row.getString(0), row.getString(1), row.getString(2), row.getString(3), row.getString(4), row.getString(5), "2", row.getString(7), row.getString(8), row.getString(9), row.getString(10), row.getString(11), row.getString(12))
      } else if(row.getString(3).contains("国际") || row.getString(3).contains("美国") || row.getString(3).contains("欧盟") ||
        row.getString(3).contains("外交") || row.getString(3).contains("总统") || row.getString(3).contains("联合国")) {
        News(row.getString(0), row.getString(1), row.getString(2), row.getString(3), row.getString(4), row.getString(5), "3", row.getString(7), row.getString(8), row.getString(9), row.getString(10), row.getString(11), row.getString(12))
      } else if(row.getString(3).contains("体育") || row.getString(3).contains("足球") || row.getString(3).contains("篮球") ||
        row.getString(3).contains("中超") || row.getString(3).contains("NBA") || row.getString(3).contains("CBA")) {
        News(row.getString(0), row.getString(1), row.getString(2), row.getString(3), row.getString(4), row.getString(5), "4", row.getString(7), row.getString(8), row.getString(9), row.getString(10), row.getString(11), row.getString(12))
      } else if(row.getString(3).contains("娱乐") || row.getString(3).contains("电影") || row.getString(3).contains("电视") ||
        row.getString(3).contains("拍剧") || row.getString(3).contains("新歌") || row.getString(3).contains("舞蹈")) {
        News(row.getString(0), row.getString(1), row.getString(2), row.getString(3), row.getString(4), row.getString(5), "5", row.getString(7), row.getString(8), row.getString(9), row.getString(10), row.getString(11), row.getString(12))
      } else if(row.getString(3).contains("科技") || row.getString(3).contains("手机") || row.getString(3).contains("电脑") ||
        row.getString(3).contains("智能") || row.getString(3).contains("华为") || row.getString(3).contains("5G")) {
        News(row.getString(0), row.getString(1), row.getString(2), row.getString(3), row.getString(4), row.getString(5), "6", row.getString(7), row.getString(8), row.getString(9), row.getString(10), row.getString(11), row.getString(12))
      } else if(row.getString(3).contains("财经") || row.getString(3).contains("楼市") || row.getString(3).contains("证券") ||
        row.getString(3).contains("股票") || row.getString(3).contains("基金") || row.getString(3).contains("金融")) {
        News(row.getString(0), row.getString(1), row.getString(2), row.getString(3), row.getString(4), row.getString(5), "7", row.getString(7), row.getString(8), row.getString(9), row.getString(10), row.getString(11), row.getString(12))
      } else {
        News(row.getString(0), row.getString(1), row.getString(2), row.getString(3), row.getString(4), row.getString(5), "8", row.getString(7), row.getString(8), row.getString(9), row.getString(10), row.getString(11), row.getString(12))
      }
    })

    //3.将划分好模块的新闻数据存到hdfs里
    newsRDD.coalesce(1).write.option("header","true").option("delimiter", ",").csv(root + fileName + "-preProcess/")

    //4.更改hdfs文件名，后续处理时容易找到
    val conf = new Configuration
    val uri = new URI("hdfs://172.16.29.94:9000")
    val fs = FileSystem.get(uri, conf, "root")
    val fsArr = fs.listStatus(new Path(root + fileName + "-preProcess/"))
    val filePaths = FileUtil.stat2Paths(fsArr)
    filePaths.map(path => {
      if(path.getName.contains("part"))
        fs.rename(path,new Path(root + fileName + "-preProcess/" + fileName + "-p1.csv"))
      else if(path.getName.contains("SUCCESS"))
        fs.delete(path,true)
      else path
    } )
    fs.close()

  }

}