package srecord.jdbc

import java.io.File
import org.scalatest.FunSuite

import scala.List

import srecord.DriverSuite
import srecord.util.JDBCUtil._

class StatementSuite extends FunSuite {

  test("execute") {
    
    DriverSuite.withCleanDatabase {
      SQL.execute("create table mytable (id integer, name varchar(100))")
      SQL.execute("insert into mytable values (?,?)", 1, "foo")
      val r = SQL.query("select * from mytable") { row =>
        assert( row.nextInt == 1 )
        assert( row.nextString == "foo" )
      }
      assert(r.length == 1)
    }
  }
  
  
  test("query with column names") {
    
    DriverSuite.withCleanDatabase {
      SQL.execute("create table mytable (id integer, name varchar(100))")
      SQL.execute("insert into mytable values (?,?)", 1, "foo")
      SQL.execute("insert into mytable values (?,?)", 2, "bar")
      var n = 1
      val r = SQL.query("select * from mytable") { row =>
        if (n == 1) {
          assert( row.readInt("id") === 1 )
          assert( row.readInt("ID") === 1 ) // case insensitive
          assert( row.readString("name") == "foo" )
        } else if (n == 2) {
          assert( row.readInt("id") === 2 )
          assert( row.readString("name") == "bar" )
        } else error("Invalid number of rows")
        n += 1
        n
      }
      assert(r.length === 2)
      
      // try with invalid column name
      try {
        val r = SQL.query("select * from mytable") { row =>
          assert( row.readString("bad") == "bad")
        }
        fail("Expected NoSuchElementException but no exception raised")
      } catch {
        case e: NoSuchElementException => // pass
        case e: Exception => fail("Unexpected exception: "+e)
      }
      
    }
  }
  
  test("queryFirst") {
    
    DriverSuite.withCleanDatabase {
      SQL.execute("create table mytable (id integer, name varchar(100))")
      SQL.execute("insert into mytable values (?,?)", 1, "foo")
      SQL.execute("insert into mytable values (?,?)", 2, "bar")
      val count = SQL.queryFirst("select count(*) from mytable") { _.nextInt }
      assert(count.get === 2)
    }
  }

  // TODO:  Test all datatypes
  
}
