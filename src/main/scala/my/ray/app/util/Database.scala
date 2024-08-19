package my.ray.app.util

import scalikejdbc._
import my.ray.app.model.{OrderTransaction, Table, User}

trait Database {
  val derbyDriverClassname = "org.apache.derby.jdbc.EmbeddedDriver"

  val dbURL = "jdbc:derby:myDB;create=true;";

  Class.forName(derbyDriverClassname)

  ConnectionPool.singleton(dbURL, "admin", "admin")

  // ad-hoc session provider on the REPL
  implicit val session = AutoSession
}

// This object is used to setup the database and check if the database is already initialized
object Database extends Database {
  def setupDB(): Unit = {
    if (!hasDBInitialize) {
      // Only initialize if at least one table is missing
      if (!tableExists("User")) {
        User.initializeTable()
      }
      if (!tableExists("Tables")) {
        Table.initializeTable()
      }
      if (!tableExists("Txn_order_header") || !tableExists("Txn_order_detail")) {
        OrderTransaction.initializeTable()
      }
    }
  }

  // Checks if the database is initialized by confirming the presence of all tables
  def hasDBInitialize: Boolean = {
    tableExists("User") && tableExists("Tables") && tableExists("Txn_order_header") && tableExists("Txn_order_detail")
  }

  // Utility function to check if a specific table exists
  private def tableExists(tableName: String): Boolean = {
    DB.getTable(tableName).isDefined
  }
}