package my.ray.app.util
import scalikejdbc._
import my.ray.app.model.Role

trait Database {
  val derbyDriverClassname = "org.apache.derby.jdbc.EmbeddedDriver"

  val dbURL = "jdbc:derby:myDB;create=true;";
  
  Class.forName(derbyDriverClassname)

  ConnectionPool.singleton(dbURL, "admin", "admin")

  // ad-hoc session provider on the REPL
  implicit val session = AutoSession
}

// This object is used to setup the database and check if the database is already initialized
object Database extends Database{
  def setupDB() = {
    if (!hasDBInitialize)
      Role.initializeTable()
  }
  def hasDBInitialize : Boolean = {

    DB getTable "Role" match {
      case Some(x) => true
      case None => false
    }

  }
}