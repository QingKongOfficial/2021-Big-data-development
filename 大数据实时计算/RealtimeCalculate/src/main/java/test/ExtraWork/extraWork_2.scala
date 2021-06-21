package test.ExtraWork

import java.util.{Properties, UUID}
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010
import scala.collection.{breakOut, mutable}

object extraWork_2 {
  val topic = "kongqing_1"
  val sever = "bigdata35.depts.bingosoft.net:29035,bigdata36.depts.bingosoft.net:29036,bigdata37.depts.bingosoft.net:29037"

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val kafkaProperties = new Properties()
    kafkaProperties.put("bootstrap.servers", sever)
    kafkaProperties.put("group.id", UUID.randomUUID().toString)
    kafkaProperties.put("auto.offset.reset", "earliest")
    kafkaProperties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    kafkaProperties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    val kafkaConsumer = new FlinkKafkaConsumer010[String](topic,
      new SimpleStringSchema, kafkaProperties)
    kafkaConsumer.setCommitOffsetsOnCheckpoints(true)
    val inputKafkaStream = env.addSource(kafkaConsumer)
    var mapw:Map[String,Int]=Map()
    inputKafkaStream.map(x => {
      val cityt=x.split(",")(2)
      val city=cityt.substring(10,cityt.length-1)
      if(mapw.contains(city)){
        val t=mapw(city)+1
        mapw+=(city -> t)
      }
      else{
        mapw+=(city -> 1)
      }
      val xx=mutable.LinkedHashMap(mapw.toSeq.sortWith( _._2>_._2 ):_*)
      var length=xx.size
      if (length>5){
        length=5
      }
      print("The top 5 cities been visited:")
      xx.take(length).keys.foreach(str=>{
        print(str+" ")
      })
      print("\n")
    })
    env.execute()
    println("Success")
  }
}
