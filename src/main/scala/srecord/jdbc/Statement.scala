package srecord.jdbc

import java.util.Date;
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Timestamp
import java.sql.Types

import scala.collection.Map
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.HashMap

/**
 * Utility wrapper for PreparedStatements.
 * <p>
 * Note:  This class is not thread-safe.
 */
class Statement(val c: Connection, var sql: String) {
  
  /**
   * Prepared statement, from SQL string.
   */
  private var stmt: PreparedStatement = null
  
  /**
   * Current column index.
   */
  private var _col = 1
  
  /**
  * Column metadata
  */
  private var _columns: HashMap[String, Int] = null
  
  
  def this(c: Connection) {
    this(c, "")
  }
  
  /**
   * Execute the SQL statement
   * 
   * @return true if execution was successful
   */
  def execute(): Int = {
    try {
      prepareStatement
      if (stmt.execute) {
        // result is a result set
        -1
      } else {
        stmt.getUpdateCount
      }
    } finally {
      close
    }
  }
  
  /**
   * Execute the SQL statement
   * 
   * @return true if execution was successful
   */
  def execute(values: Any*): Int = {
    writeValues(values: _*)
    try {
      prepareStatement
      if (stmt.execute) {
        // result is a result set
        -1
      } else {
        stmt.getUpdateCount
      }
    } finally {
      close
    }
  }
  
  /**
  * Execute the SQL statement as an update.
  *
  * @return number of rows affected.
  */
  def executeUpdate(): Int = {
    try {
      prepareStatement
      stmt.executeUpdate
    } finally {
      close
    }
  }
  
  
  /**
  * Execute the SQL query with the given value bindings and convert
  * each row into some object
  */
  def query[T](values: Any*)(f: Statement#Row => T): Seq[T] = {
    writeValues(values: _*)
    query(f)
  }
  
  /**
  * Execute the SQL query and convert each row into some object
  */
  def query[T](f: Statement#Row => T): Seq[T] = {
    try {
      prepareStatement
      val rs = stmt.executeQuery()
      val row = new Row(rs)
      val buf = new ArrayBuffer[T]()
      while (row.next) {
        buf += f(row)
      }
      buf.toSeq
    } finally {
      close
    }
  }
  
  
  /**
  * Execute the SQL query with the given value bindings and convert
  * each row into some object
  */
  def queryFirst[T](values: Any*)(f: Statement#Row => T): Option[T] = {
    writeValues(values: _*)
    queryFirst(f)
  }
  
  /**
  * Execute the SQL query and convert each row into some object
  */
  def queryFirst[T](f: Statement#Row => T): Option[T] = {
    try {
      prepareStatement
      val rs = stmt.executeQuery()
      val row = new Row(rs)
      if (row.next) Some(f(row))
      else None
    } finally {
      close
    }
  }
  
  /**
  * Release all underlying resources of this EasyStatement.
  */
  def close() {
    _col = 1
    if (stmt != null) {
      stmt.close()
      stmt = null
    }
  }
  
  
  /**
  * Set the current CHAR column value to the given String value.
  *
  * @param value String value (optional).
  */
  def writeString(value: String) {
    prepareStatement
    if ( value == null || value.trim().length() == 0) {
      stmt.setNull(_col, Types.CHAR);
    } else {
      stmt.setString(_col, value.trim());
    }
    _col += 1
  }
  
  
  
  /**
  * Set the current BIT column value to the given Boolean value.
  *
  * @param value Boolean value (optional).
  */
  def writeBoolean(value: Boolean) {
    prepareStatement
    stmt.setBoolean(_col, value);
    _col += 1
  }
  
  
  /**
  * Set the current BIT column value to the given Boolean value.
  *
  * @param value Boolean value (optional).
  */
  def writeBoolean(value: Option[Boolean]) {
    prepareStatement
    if (value.isDefined) {
      stmt.setBoolean(_col, value.get);
    } else {
      stmt.setNull(_col, Types.BIT);
    }
    _col += 1
  }
  
  
  /**
  * Set the current NUMBER column value to the given Integer's value
  *
  * @param value Integer value (optional).
  */
  def writeInt(value: Int) {
    prepareStatement
    stmt.setInt(_col, value)
    _col += 1
  }
  
  
  /**
  * Set the current NUMBER column value to the given Integer's value
  *
  * @param value Integer value (optional).
  */
  def writeInt(value: Option[Int]) {
    prepareStatement
    if (value.isEmpty) stmt.setNull(_col, Types.INTEGER);
    else stmt.setInt(_col, value.get);
    _col += 1
  }
  
  
  /**
  * Set the current NUMBER column value to the given Long value
  *
  * @param value Long value (optional).
  */
  def writeLong(value: Long) {
    prepareStatement
    stmt.setLong(_col, value);
    _col += 1
  }
  
  /**
  * Set the current NUMBER column value to the given Long value
  *
  * @param value Long value (optional).
  */
  def writeLong(value: Option[Long]) {
    prepareStatement
    if (value.isEmpty) stmt.setNull(_col, Types.NUMERIC )
    else stmt.setLong(_col, value.get)
    _col += 1
  }
  
  def writeFloat(value: Float) {
    prepareStatement
    stmt.setFloat(_col, value);
    _col += 1
  }
  
  def writeFloat(value: Option[Float]) {
    prepareStatement
    if (value.isEmpty) stmt.setNull(_col, Types.NUMERIC )
    else stmt.setFloat(_col, value.get)
    _col += 1
  }
  
  def writeDouble(value: Double) {
    prepareStatement
    stmt.setDouble(_col, value);
    _col += 1
  }
  
  def writeDouble(value: Option[Double]) {
    prepareStatement
    if (value.isEmpty) stmt.setNull(_col, Types.NUMERIC )
    else stmt.setDouble(_col, value.get)
    _col += 1
  }
  
  /**
  * Set the current DATE column value to the given Date value
  *
  * @param value Date value (optional).
  */
  def writeDate(value: Date) {
    prepareStatement
    if (value == null) {
      stmt.setNull(_col, Types.TIMESTAMP);
    } else {
      val ts = new Timestamp(value.getTime);
      stmt.setTimestamp(_col, ts);
    }
    _col += 1
  }
  
  /**
  * Set the current CLOB column to the given data
  */
  def writeCLOB(data: String) {
    prepareStatement
    if (data == null) {
      stmt.setNull(_col, Types.CLOB);
    } else {
      stmt.setString(_col, data)
    }	
    _col += 1
  }
  
  def writeValues(values: Any*) {
    //values.foreach(v => println("writeValues: "+v))
    values.foreach { _ match {
      case b: Boolean => writeBoolean(b)
      case d: Date    => writeDate(d)
      case i: Int     => writeInt(i)
      case l: Long    => writeLong(l)
      case f: Float   => writeFloat(f)
      case d: Double  => writeDouble(d)
      case s: String  => writeString(s)
      }
    }
  }
  
  override def toString = "Statement: "+ sql;
  
  /**
  * Prepare the SQL statement.
  */
  def prepareStatement {
    if (sql == null) throw new IllegalStateException( "SQL statement string is null" );
    if (stmt == null) {
      stmt = c.prepareStatement(sql);
    }
  }
  
  class Row(rs: ResultSet) {
    
    private[Statement] val _rs = rs
    
    // current column
    private[this] var _col = 1
    
    /**
    * Move the result set cursor to the next row, if any.
    * @return true if there is a next row, false otherwise.
    */
    private[Statement] def next: Boolean = {
      _col = 1
      rs.next
    }		
    
    /**
    * Read the next column as a String.
    * @return String read after being trimmed, or null if value is SQL <code>NULL</code>.
    */
    def nextString = {
      var value = rs.getString(_col);
      _col += 1
      value
    }
    
    def readString(c: String): String = readString(columns(c))
    
    def readString(c: Int) = {
      var value = rs.getString(c);
      if (value != null) value = value.trim
      value
    }
    
    /**
    * Read the next column as a CLOB and then returns its String value.
    * @return String read after being trimmed, or null if value is SQL <code>NULL</code>.
    */
    def readClob: String = {
      error("todo")
    }	
    
    /**
    * Read the next column as an "int" java type.
    * @return The column value, or 0 if column value is SQL <code>NULL</code>.
    */
    def nextInt: Int = {
      val value = readInt(_col)
      _col += 1
      value
    }
    
    def readInt(c: Int): Int = {
      val value = rs.getInt(c)
      value
    }
    
    def readInt(c: String): Int = readInt(columns(c))
    
    /**
    * Read the next column as a Long.
    *
    * @return Value read converted to a Long, or 0 if value is SQL <code>NULL</code>.
    */	
    def nextLong: Long = {
      val value = readLong(_col)
      _col += 1
      value
    }
    
    def readLong(c: Int): Long = {
      val value = rs.getLong(c)
      value
    }
    
    def readLong(c: String): Long = readLong(columns(c))
    
    def nextFloat: Float = {
      val value = readFloat(_col)
      _col += 1
      value
    }
    
    def readFloat(c: Int): Float = {
      val value = rs.getFloat(c)
      value
    }
    
    def readFloat(c: String): Float = readFloat(columns(c))
    
    def nextDouble: Double = {
      val value = readDouble(_col)
      _col += 1
      value
    }
    
    def readDouble(c: Int): Double = {
      val value = rs.getDouble(c)
      value
    }
    
    def readDouble(c: String): Double = readDouble(columns(c))
    
    /**
    * Read the next column as a Date
    *
    * @return Value read converted to a Date, or null if value is SQL <code>NULL</code>.
    */
    def nextDate: Date = {
      val value = readDate(_col)
      _col += 1
      value
    }
    
    def readDate(c: Int): Date = {
      val value = rs.getTimestamp(c)
      if (value == null ) null else new Date(value.getTime)
      value
    }
    
    def readDate(c: String): Date = readDate(columns(c))
    
    /**
    * Read the next column as a boolean Java type.
    *
    * @return Value boolean value of the column, or false if value is SQL <code>NULL</code>.
    */
    def nextBoolean: Boolean = {
      val value = readBoolean(_col)
      _col += 1
      value
    }
    
    def readBoolean(c: Int): Boolean = {
      val value = rs.getBoolean(c)
      value
    }
    
    def readBoolean(c: String): Boolean = readBoolean(columns(c))
    
    def columns: Map[String, Int] = {
      if (_columns == null) {
        _columns = new HashMap[String, Int] {
          override def apply(key: String): Int =
          super.apply(key.toLowerCase)
          override def default(key: String): Int =
          throw new NoSuchElementException("Invalid column name: " + key)
        }
        val meta = rs.getMetaData
        var i = 1
        while (i <= meta.getColumnCount) {
          val colname = meta.getColumnName(i)
          _columns(colname.toLowerCase) = i
          i += 1
        }
      }
      _columns
    }
    
  } // class Row
  
} // class Statement
