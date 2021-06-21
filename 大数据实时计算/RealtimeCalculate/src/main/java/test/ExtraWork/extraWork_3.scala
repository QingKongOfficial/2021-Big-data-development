package test.ExtraWork

import java.util.Properties

import com.bingocloud.{ClientConfiguration, Protocol}
import com.bingocloud.auth.BasicAWSCredentials
import com.bingocloud.services.s3.AmazonS3Client
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.nlpcn.commons.lang.util.IOUtil

object extraWork_3 {
  val accessKey = "89189D3CBF9AB0869CAF"
  val secretKey = "W0Y4RkVEOEE3M0FEMTc5RTNDMjI3OTY3MEM0RUJE"
  val endpoint = "http://scut.depts.bingosoft.net:29997"
  val bucket = "kongqing"
  val key = "demo.txt"

  val topic = "mn_buy_ticket_1_kongqing"
  val sever = "bigdata35.depts.bingosoft.net:29035,bigdata36.depts.bingosoft.net:29036,bigdata37.depts.bingosoft.net:29037"

  def main(args: Array[String]): Unit = {
    val s3Content = readFile()
    produceToKafka(s3Content)
  }

  def readFile(): String = {
    val credentials = new BasicAWSCredentials(accessKey, secretKey)
    val clientConfig = new ClientConfiguration()
    clientConfig.setProtocol(Protocol.HTTP)
    val amazonS3 = new AmazonS3Client(credentials, clientConfig)
    amazonS3.setEndpoint(endpoint)
    val s3Object = amazonS3.getObject(bucket, key)
    IOUtil.getContent(s3Object.getObjectContent, "UTF-8")
  }

  def produceToKafka(s3Content: String): Unit = {
    val props = new Properties
    props.put("bootstrap.servers", sever)
    props.put("acks", "all")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    val producer = new KafkaProducer[String, String](props)
    val dataArr = s3Content.split("\n")
    for (s <- dataArr) {
      if (!s.trim.isEmpty) {
        val record = new ProducerRecord[String, String](topic, null, s)
        println("Starting:" + s)
        producer.send(record)
      }
    }
    producer.flush()
    producer.close()
  }
}
