package com.atguigu.es

import java.util.Date

import org.apache.http.HttpHost
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.client.{RequestOptions, RestClient, RestHighLevelClient}
import org.elasticsearch.common.xcontent.XContentType

/**
  * SparkStreaming：是处理流式数据的框架
  *
  * @description
  * @author lihuaiqiang
  * @date 2022/7/4 16:22
  */
object SparkStreamingESTest2 {

  def main(args: Array[String]): Unit = {
    val eSTest = new SparkConf().setMaster("local[*]").setAppName("ESTest")
    //Seconds(3)：采集周期
    val ssc = new StreamingContext(eSTest, Seconds(3))

    //通过 socket 获取网络数据流
    val unit = ssc.socketTextStream("localhost", 9999)
    unit.foreachRDD(
      rdd => {
        println(new Date())
        rdd.foreach(
          data => {
            var client = new RestHighLevelClient(
              RestClient.builder(new HttpHost("localhost", 9200, "http"))
            )
            // 新增文档 - 请求对象
            val request = new IndexRequest()
            //把接收的数据分解
            val strings = data.split(" ")
            request.index("product").id(strings(0))
            var json =
              s"""
                 |{"data":"${strings(1)}"}
                        """.stripMargin
            request.source(json, XContentType.JSON)
            val response = client.index(request, RequestOptions.DEFAULT)
            val result = response.getResult
            println(result)

            client.close()
          }
        )
      }
    )
    //启动采集器
    ssc.start()
    //等待结束
    ssc.awaitTermination()
  }
}
