package my.ray.app.util
import scalikejdbc._


trait Database {
  val derbyDriverClassname = "org.apache.derby.jdbc.EmbeddedDriver"

  val dbURL = "jdbc:derby:myDB;create=true;";
  
  Class.forName(derbyDriverClassname)

  ConnectionPool.singleton(dbURL, "admin", "admin")

  // ad-hoc session provider on the REPL
  implicit val session = AutoSession


}
object Database extends Database{
  def setupDB() = {
    if (!hasDBInitialize)
      Person.initializeTable()
  }
  def hasDBInitialize : Boolean = {

    DB getTable "Record" match {
      case Some(x) => true
      case None => false
    }

  }
}