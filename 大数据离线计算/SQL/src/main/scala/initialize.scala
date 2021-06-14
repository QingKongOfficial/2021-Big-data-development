import java.util.Properties
import scala.util.control._
import java.sql.DriverManager

class initialize(hostString: String,
                 portString: String,
                 databaseString: String,
                 userString: String,
                 passwordString: String) {
  var host:String = hostString
  var port:String = portString
  var database:String = databaseString
  var user:String = userString
  var password:String = passwordString
  val loop = new Breaks;
  val address = "jdbc:hive2://"+ host +":"+ port +"/"+ database
  val properties = new Properties()
  properties.setProperty("driverClassName", "org.apache.hive.jdbc.HiveDriver")
  properties.setProperty("user", user)
  properties.setProperty("password", password)

  val connection = DriverManager.getConnection(address, properties)

  var statement = connection.createStatement

  def getTables(): Array[String] = {
    var tables = new Array[String](0)
    val queryTable = statement.executeQuery("SHOW tables")
    try {
      while (queryTable.next) {
        val tableName = queryTable.getString(1)
        tables=tables:+tableName
      }

    }catch {
      case e: Exception => e.printStackTrace()
    }
    tables
  }

  def Column(table:String): Array[String] = {
    var columns = new Array[String](0)
    val resultSet = statement.executeQuery("SHOW columns FROM " + table)
    try {
      while (resultSet.next) {
        val column = resultSet.getString(1)
        columns=columns:+column
      }
    }catch {
      case e: Exception => e.printStackTrace()
    }
    columns
  }

  def Connector(query:String): Array[Array[String]] = {
    var resultSet = statement.executeQuery(query)
    val data = resultSet.getMetaData();
    val colCount = data.getColumnCount()
    var rowCount = 0
    while(resultSet.next) {
      rowCount+=1;
    }
    resultSet.close()
    resultSet = statement.executeQuery(query)
    val res= Array.ofDim[String](rowCount+1,colCount)
    for( i <- 0 to colCount-1){
      res(0)(i) = data.getColumnName(i+1)
    }
    var tag=1
    while (resultSet.next) {
      for( i <- 0 to colCount-1){
        res(tag)(i) = resultSet.getString(i+1)
      }
      tag+=1
    }
    res
  }
}
