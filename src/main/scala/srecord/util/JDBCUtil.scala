package srecord.util

import srecord.Logger

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

object JDBCUtil extends Logger {
  
  /**
   * Close SQL connection, ignoring and logging any exception
   */
  def close(c: Connection) {
    if (c != null) {
      try {
        c.close
      } catch { 
        case e: Exception => LOG.warn("Exception while closing database connection:" + e.getMessage)
      }	
    }	
  }

  def closeAfter(c: Connection)(f: Connection => Unit) {
    try {
      f(c)
    } finally {
      close(c)
    }
  }

}
