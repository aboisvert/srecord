package srecord.test

import org.scalatest.FunSuite

import srecord.DriverSuite
import srecord.jdbc.Statement
import srecord.jdbc.SQL

class CustomersTest extends FunSuite {

  object CustomersDatabase extends CustomersDatabase {
    val databaseName = "Customers"
    val customers = new Customers {
      type ROW = CustomerRow
      def createRow: ROW = new CustomerRow {}
    }
  }
  
  import CustomersDatabase._
  
  test("execute") {
    
    DriverSuite.withCleanDatabase {
      SQL.execute("create table customers (id integer, name varchar(100), phone varchar(20))")

      val c = customers.create(1)
      c.name = "foo"
      c.phone = "111-111-1111"
      
      assert(c.id === 1)
      assert(c.name === "foo")
      customers.insert(c)
      
      customers.find(1) match {
        case None => error("Customer not found")
        case Some(c) =>
          assert(c.id === 1)
          assert(c.name === "foo")
          assert(c.phone === "111-111-1111")
      }

      val c2 = customers.create(2)
      c2.name = "bar"
      c2.phone = "222-222-2222"
      customers.insert(c2)
      
      val q1 = customers.query(customers.name, "foo")
      assert( q1.size == 1 )
      q1.foreach { c =>
        assert( c.id === 1 )
        assert( c.name === "foo" )
        assert(c.phone === "111-111-1111")
      }

      val q2 = customers.query(customers.name, "bar")
      assert( q2.size == 1 )
      q2.foreach { c =>
        assert( c.id === 2 )
        assert( c.name === "bar" )
        assert( c.phone === "222-222-2222" )
      }

      customers.queryFirst(customers.name, "bar") match {
        case None => error("Customer 'bar' not found")
        case Some(c) => 
          assert( c.id === 2 )
          assert( c.name === "bar" )
          assert( c.phone === "222-222-2222" )
      }

      customers.queryFirst(
        customers.name -> "bar", 
        customers.phone -> "222-222-2222"
      ) match {
        case None => error("Customer 'bar' not found")
        case Some(c) => 
          assert( c.id === 2 )
          assert( c.name === "bar" )
          assert( c.phone === "222-222-2222" )
      }
    }
  }      
      
}
