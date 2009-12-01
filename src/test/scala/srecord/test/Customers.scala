package srecord.test

import srecord.Database

trait Customer {
  val id : Long
  var name : String;
}

trait CustomersDatabase extends Database {
  val customers: Customers

  trait Customers extends Table with PrimaryKey1[Long] {
    type ROW <: CustomerRow

    val primaryKey = id    
    def tableName = "CUSTOMERS"
    
    val id = new PrimaryKey(JDBCTypes.LONG) {
      val columnName = "id"
    }

    val name = new ColumnDef(JDBCTypes.STRING) {
      val columnName = "name"
      val nullable = false
    }
    
    val phone = new ColumnDef(JDBCTypes.STRING) {
      val columnName = "phone"
      val nullable = true
    }

    trait CustomerRow extends Customer with Row {
      lazy val id = get(Customers.this.id)
      def id_=(value: Long) = set(Customers.this.id, value)
      def name = get(Customers.this.name)
      def name_=(value: String) = set(Customers.this.name, value)
      def phone = get(Customers.this.phone)
      def phone_=(value: String) = set(Customers.this.phone, value)
    }
  }
  
}
