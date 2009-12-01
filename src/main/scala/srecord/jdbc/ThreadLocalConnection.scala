package srecord.jdbc

import java.sql.Connection
import srecord.util.JDBCUtil

object ThreadLocalConnection extends Function0[Connection]{
  private val localConnection = new ThreadLocal[Connection]
  
  @transient
  var connectionProvider: ConnectionProvider = _
  
  def get = {
    var c = localConnection.get
    if (c == null && connectionProvider != null) {
      c = connectionProvider.connection
    }
    if (c == null) throw new IllegalStateException("No available local connection")
    c
  }

  def close = {
    val c = localConnection.get
    if (c != null) JDBCUtil.close(c)
    localConnection.set(null)
  }
  
  def set(c: Connection) = localConnection.set(c)

  def apply = get
  def update(c: Connection) = set(c)

  def usingC(c: Connection)(f: Connection => Unit) {
    val before = localConnection.get
    try {
      f(c)
    } finally {
      update(before)
    }
  }

  def using(c: Connection)(f: => Unit) {
    val before = localConnection.get
    set(c)
    try {
      f
    } finally {
      update(before)
    }
  }
}