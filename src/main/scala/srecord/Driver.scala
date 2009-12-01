package srecord

import java.sql.Connection
import java.sql.DriverManager

trait Driver {
  val driverClass: String
  
  def connect(url: String, user: String, password: String): Connection = {
    Class.forName(driverClass);
    DriverManager.getConnection(url, user, password);
  }
}

object H2Driver extends Driver {
  val driverClass = "org.h2.Driver"
}
