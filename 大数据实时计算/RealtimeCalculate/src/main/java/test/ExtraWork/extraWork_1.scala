package test.ExtraWork

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala._
import scala.util.control.Breaks._
import scala.collection.mutable.ArrayBuffer


object extraWork_1 {
  val key = "anything"
  val ticker = 20
  var pointer = 0
  var time = new ArrayBuffer[Long]()
  val mem = new ArrayBuffer[String]()

  def refreshIndex(word:String): String ={
    time+=System.currentTimeMillis()
    mem+=word
    breakable(
      for(i<-pointer to time.length-1){
        if(ticker*1000+time(pointer)>System.currentTimeMillis()){
          break()
        }
        pointer=i
      }
    )
    var res="There are "+ (time.length-pointer) + key +" in the last "+ ticker.toString() +" Seconds"+"\n"
    for (i<-pointer to time.length-1){
      res+=mem(i)+" "
    }
    res
  }

  def main(args: Array[String]) {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val text = env.socketTextStream("localhost", 9999)
    val stream = text.flatMap {
      _.toLowerCase.split("\\W+") filter {
        _.contains(key)
      }
    }.map {
      (refreshIndex(_))
    }
    stream.print()
    env.execute("RealtimeCalculate Words Count")
  }
}
