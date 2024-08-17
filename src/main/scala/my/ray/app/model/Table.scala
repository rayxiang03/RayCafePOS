package my.ray.app.model

import scalafx.beans.property.{BooleanProperty, IntegerProperty, StringProperty}
import scalikejdbc._

import scala.util.{Failure, Success, Try}
import my.ray.app.util.Database

import java.sql.SQLException

case class Table(val _tableId: String, val _pax: Int, val _availability: Boolean, val _details: String) extends Database {

  // Define properties
  var tableId = new StringProperty(_tableId)
  var pax = new IntegerProperty(this, "pax", _pax)
  var availability = new BooleanProperty(this, "availability", _availability)
  var details = new StringProperty(_details)

  // Update the table record
  def save(): Try[Int] = {
    Try(DB autoCommit { implicit session =>
      sql"""
        UPDATE tables
        SET pax = ${pax.value}, availability = ${availability.value}, details = ${details.value}
        WHERE table_id = ${tableId.value}
      """.update.apply()
    })
  }


  // Check if the table exists in the database
  def isExist: Boolean = {
    DB readOnly { implicit session =>
      sql"""
        SELECT * FROM tables WHERE table_id = ${tableId.value}
      """.map(rs => rs.string("table_id")).single.apply()
    } match {
      case Some(_) => true
      case None => false
    }
  }


  def release(): Try[Int] = {
    availability.value = true
    details.value = null
    save()
  }
}

object Table extends Database {

  // Create the table in the database
  def initializeTable(): Unit = {
    DB autoCommit { implicit session =>
      try {
        sql"""
        CREATE TABLE tables (
          table_id VARCHAR(50) NOT NULL,
          pax INT,
          availability BOOLEAN,
          details VARCHAR(255),
          PRIMARY KEY (table_id)
        )
      """.execute.apply()
      } catch {
        case e: SQLException if e.getSQLState == "X0Y32" =>
        //          println("Table already exists.")
        case e: SQLException =>
          println(s"SQL Error: ${e.getMessage}")
      }
    }
  }

  // Retrieve all tables from the database
  def getAllTables: List[Table] = {
    DB readOnly { implicit session =>
      sql"SELECT * FROM tables".map(rs => new Table(
        rs.string("table_id"),
        rs.int("pax"),
        rs.boolean("availability"),
        rs.string("details")
      )).list.apply()
    }
  }

  def getAvailableTables: List[Table] = {
    DB readOnly { implicit session =>
      sql"SELECT * FROM tables where availability = true".map(rs => new Table(
        rs.string("table_id"),
        rs.int("pax"),
        rs.boolean("availability"),
        rs.string("details")
      )).list.apply()
    }
  }

  // Retrieve a specific table by its ID
  def getTableById(tableId: String): Option[Table] = {
    DB readOnly { implicit session =>
      sql"SELECT * FROM tables WHERE table_id = ${tableId}".map(rs => new Table(
        rs.string("table_id"),
        rs.int("pax"),
        rs.boolean("availability"),
        rs.string("details")
      )).single.apply()
    }
  }
}
