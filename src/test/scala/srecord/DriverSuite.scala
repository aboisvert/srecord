package srecord

import srecord.util.FileUtil._
import srecord.util.JDBCUtil._
import srecord.jdbc.SQL
import srecord.jdbc.ThreadLocalConnection

import java.io.File
import java.sql.Connection

import org.scalatest.FunSuite

class DriverSuite extends FunSuite {

  test("H2Driver") {
    DriverSuite.withCleanDatabase {
      closeAfter(DriverSuite.connection) { c =>
        assert(c != null)
      }
    }
  }
  
}

object DriverSuite {
  val temp = new File("temp")
  
  def connection = {
    temp.mkdir
    H2Driver.connect("jdbc:h2:temp/test", "test", "test")
  }
  
  def withCleanDatabase(f: => Unit) {
      deleteRecursively(temp)
      ThreadLocalConnection.using(DriverSuite.connection) { 
        f
        SQL.execute("shutdown")
      }
      
  }

}
