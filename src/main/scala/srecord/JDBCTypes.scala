package srecord

import java.util.Date

object JDBCTypes {
  class JDBCType[T](jdbcType: Int)
  case object BOOLEAN   extends JDBCType[Boolean](java.sql.Types.BIT)
  case object DATE      extends JDBCType[Date]   (java.sql.Types.DATE)
  case object DOUBLE    extends JDBCType[Double] (java.sql.Types.DOUBLE)
  case object FLOAT     extends JDBCType[Float]  (java.sql.Types.FLOAT)
  case object INTEGER   extends JDBCType[Integer](java.sql.Types.INTEGER)
  case object LONG      extends JDBCType[Long]   (java.sql.Types.BIGINT)
  case object STRING    extends JDBCType[String] (java.sql.Types.VARCHAR)
  case object TIMESTAMP extends JDBCType[Date]   (java.sql.Types.TIMESTAMP)
    
}
