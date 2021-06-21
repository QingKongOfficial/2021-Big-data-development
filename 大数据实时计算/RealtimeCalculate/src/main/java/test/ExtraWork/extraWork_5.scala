package test.ExtraWork

import java.util
import java.util.{Map, Properties, Scanner, Timer, TimerTask, UUID, Vector}
import com.bingocloud.util.json.JSONObject
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.runtime.client.JobExecutionException
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonParseException
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010

object extraWork_5 {

  val inputTopics: util.ArrayList[String] = new util.ArrayList[String]() {
    {

      add("mn_buy_ticket_1")
      add("mn_hotel_stay_1")
      add("mn_monitoring_1")
    }
  }
  val bootstrapServers = "bigdata35.depts.bingosoft.net:29035,bigdata36.depts.bingosoft.net:29036,bigdata37.depts.bingosoft.net:29037"
  var change=false
  var saveall:Map[String, Vector[String]] = new util.HashMap[String, Vector[String]]()

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)
    val kafkaProperties = new Properties()
    kafkaProperties.put("bootstrap.servers", bootstrapServers)
    kafkaProperties.put("group.id", UUID.randomUUID().toString)
    kafkaProperties.put("auto.offset.reset", "earliest")
    kafkaProperties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    kafkaProperties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")

    val kafkaConsumer = new FlinkKafkaConsumer010[String](inputTopics, new SimpleStringSchema, kafkaProperties)
    kafkaConsumer.setCommitOffsetsOnCheckpoints(true)
    kafkaConsumer.setCommitOffsetsOnCheckpoints(true)
    val inputKafkaStream = env.addSource(kafkaConsumer)

    inputKafkaStream.map(x => {
      if (x != null) {
        try {
          var cnt_it = new JSONObject(x)
          var username = cnt_it.get("username").toString()
          if (username != null) {
            if (saveall.get(username) == null) {
              saveall.put(username, new Vector[String])
            }
            saveall.get(username).add(x)
          }
        } catch {
          case exception: Exception => {
          }
        }
      }
      change=true
    })
    env.execute()
  }
  val timer=new Timer()
  timer.schedule(new TimerTask {
    override def run(): Unit = {
      if(change){
        val input = new Scanner(System.in)
        while(true) {
          System.out.println("Starting")
          System.out.println("Name:")
          val val1 = input.next();
          var all = saveall.get(val1)
          if (all == null){println("No record")}
          else{
            for(i<-0 to all.size()-1){
              println(all.elementAt(i))
            }
          }
        }
      }
    }},10000,1000)
}