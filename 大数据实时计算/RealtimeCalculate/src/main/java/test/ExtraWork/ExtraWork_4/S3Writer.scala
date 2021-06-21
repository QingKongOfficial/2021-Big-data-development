package test.ExtraWork.ExtraWork_4

import java.io.{File, FileWriter}
import java.sql.{DriverManager, Statement}
import java.util.{Properties, Timer, TimerTask}
import Main.{url}
import com.bingocloud.auth.BasicAWSCredentials
import com.bingocloud.{ClientConfiguration, Protocol}
import com.bingocloud.services.s3.AmazonS3Client
import org.apache.commons.lang3.StringUtils
import org.apache.flink.api.common.io.OutputFormat
import org.apache.flink.configuration.Configuration


class S3Writer(user:String, password:String,driverClassName:String,keyPrefix: String, period: Int) extends OutputFormat[String] {
  var timer: Timer = _
  var file: File = _
  var fileWriter: FileWriter = _
  var length = 0L
  var statement:Statement=_

  override def configure(configuration: Configuration): Unit = {
    val properties = new Properties()
    properties.setProperty("driverClassName", driverClassName)
    properties.setProperty("user", user)
    properties.setProperty("password", password)

    val connection = DriverManager.getConnection(url, properties)
    statement = connection.createStatement
  }

  override def open(taskNumber: Int, numTasks: Int): Unit = {
  }

  override def writeRecord(it: String): Unit = {
    this.synchronized {
        val str=it.replace("{","").replace("}","").replace("\"","").replace("\"","")
        println(str)
        val info = str.split(",");
      var colname=new Array[String](5)
      var cont=new Array[String](5)
        for( i <- 0 to info.length-1){

          if(i==0){
            val info_1=info(i).split(":")
            colname(0)=info_1(0);
            println(colname(0))
            cont(0)=info_1(1)
            for(j<-2 to info_1.length-1){
              cont(0)=cont(0)+info_1(j)
            }
            println(cont(0))
            }
          else{
            println(i)
            val info_1=info(i).split(":")
            colname(i)=info_1(0)
            println(colname(i))
            cont(i)=info_1(1)
            println(cont(i))
          }
          }
          val sql="INSERT INTO buyticket("+colname(0)+","+colname(1)+","+colname(2)+","+colname(3)+","+colname(4)+")values('"+cont(0)+"','"+cont(1)+"','"+cont(2)+"','"+cont(3)+"','"+cont(4)+"')"
          statement.addBatch(sql)
          statement.executeBatch()
          println("Success")
      }
    }

  override def close(): Unit = {
    fileWriter.flush()
    fileWriter.close()
    timer.cancel()
  }
}