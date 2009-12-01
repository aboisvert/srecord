import srecord._

trait Customer_ {
  var id: Long
}

trait Order_ {
  var id: Long
}

trait CustomersMixin extends Database {
  val customers: Customers

  trait Customers extends Table with PrimaryKey1[Long] {
    type ROW <: Customer
    val primaryKey = id
    val tableName = "CUSTOMERS"
    val id = new PrimaryKey(JDBCTypes.LONG) {
      val columnName = "id"
    }
    
    trait Customer extends Customer_ with Row {
      def id = get(Customers.this.id)
      def id_=(value: Long) = set(Customers.this.id, value)
    }
  }
  
}

trait OrdersMixin extends Database { self: CustomersMixin =>
  val orders: Orders

  trait Orders extends Table with PrimaryKey1[Long]{
    type ROW <: Order
    val primaryKey = id
    val tableName = "ORDERS"
    val id = new PrimaryKey(JDBCTypes.LONG) {
      val columnName = "id"
    }
    val customer = foreignKey(customers, customers.id)
    
    trait Order extends Order_ with Row {
      def id = get(Orders.this.id)
      def id_=(value: Long) = set(Orders.this.id, value)
      def customer = get(Orders.this.customer)
      def customer_=(value: Long) = set(Orders.this.customer, value)
    }

  }
}

trait MyDatabase extends CustomersMixin with OrdersMixin 

object MyDatabase extends MyDatabase {
  val orders = new Orders { 
    type ROW = Order
    def createRow: ROW = new Order {}
  }
  val customers = new Customers { 
    type ROW = Customer
    def createRow: ROW = new Customer {}
  }
  val databaseName = "MyDatabase"
  
}

object MyApplication {
  
  val db: MyDatabase = MyDatabase
  
  db.customers.find(1)
  
}
