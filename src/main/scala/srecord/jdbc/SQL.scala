package srecord.jdbc

import java.util.Date;
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Timestamp
import java.sql.Types

object SQL {
  def execute(stmt: String, values: Any*): Int = {
    val c = ThreadLocalConnection.get
    val s = new Statement(c, stmt)
    s.execute(values: _*)
  }
    
  def query[T](stmt: String, values: Any*)(f: Statement#Row => T): Seq[T] = {
    val c = ThreadLocalConnection.get
    val s = new Statement(c, stmt)
    s.query(values: _*)(f)
  }

  def query[T](stmt: String, f: Statement#Row => T): Seq[T] = {
    val c = ThreadLocalConnection.get
    val s = new Statement(c, stmt)
    s.query(f)
  }    

  def queryFirst[T](stmt: String, values: Any*)(f: Statement#Row => T): Option[T] = {
    val c = ThreadLocalConnection.get
    val s = new Statement(c, stmt)
    s.queryFirst(values: _*)(f)
  }

  def queryFirst[T](stmt: String, f: Statement#Row => T): Option[T] = {
    val c = ThreadLocalConnection.get
    val s = new Statement(c, stmt)
    s.queryFirst(f)
  }    
}
