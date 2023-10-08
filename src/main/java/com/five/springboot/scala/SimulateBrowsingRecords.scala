package com.five.springboot.scala

import java.net.URI
import java.sql.{Connection, DriverManager, PreparedStatement, SQLException}
import java.util.Properties

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, FileUtil, Path}
import org.apache.spark.sql.{SparkSession, _}

import scala.util.Random

/**
 * 模块划分
 *
 */

object SimulateBrowsingRecords {

  def main(args: Array[String]): Unit = {
    simulateBrowsingRecords("2016-03-04 22:14:00")
  }

  def simulateBrowsingRecords(maxTime: String): Unit = {
    System.setProperty("HADOOP_USER_NAME", "root") //更改访问hdfs用户名，需要写在sparkContext建立之前
    val spark: SparkSession = SparkSession.builder().appName("SimulateBrowsingRecords").master("local").getOrCreate()
    val sc = spark.sparkContext
    sc.setLogLevel("WARN")

    val properties = new Properties()
    properties.setProperty("user", "root")
    properties.put("password", "123456")
    val url = "jdbc:mysql://172.16.29.94:3306/news?serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8"

    //1.获取新闻数据
    //val selectSql = "(SELECT newsId FROM news.news WHERE date > " + maxTime + ") T"
    val news = spark.read.jdbc(url, "(SELECT newsId FROM news.news WHERE date > '" + maxTime + "') T", properties).toDF()
    news.cache() //将数据进行缓存，提高计算效率


    news.rdd.foreachPartition(iterator => {
      val driver: String = "com.mysql.jdbc.Driver"
      val username: String = "root"
      val password: String = "123456"
      var conn: Connection = null
      var ps: PreparedStatement = null
      try {
        Class.forName(driver) //classLoader,加载对应驱动
        conn = DriverManager.getConnection(url, username, password).asInstanceOf[Connection]
        conn.setAutoCommit(false)
        //val maxUserId = spark.read.jdbc(url, "(SELECT max(userId) FROM user) T", properties).toDF().rdd
        val sql = "INSERT INTO browsingHistory(userId,newsId,isComment) VALUES (?,?,?)"
        ps = conn.prepareStatement(sql) //批量导入ps一定要写在循环外面

        iterator.foreach(row => {
          val userId = (new Random).nextInt(38) + 6000 //随机获取[1,maxUserId]之间的userId
          val isComment = (new Random).nextInt(2) //随机获取0和1
          ps.setLong(1, userId)
          ps.setLong(2, row.getLong(0))
          ps.setInt(3, isComment)
          ps.addBatch()
        })
        ps.executeBatch()
        conn.commit()
        ps.clearBatch()

      } catch {
        case e: ClassNotFoundException =>
          System.err.println("need driver class !")
        case e: SQLException =>
          e.printStackTrace()
      } finally {
        if (ps != null) {
          ps.close()
        }
        if (conn != null) {
          conn.close()
        }
      }
    })

  }

}