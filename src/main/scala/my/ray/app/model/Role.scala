package my.ray.app.model

import scalafx.beans.property.StringProperty
import scalikejdbc._
import scala.util.{Try, Success, Failure}
import my.ray.app.util.Database


class Role(val _roleId: String, val _roleDesc: String) extends Database {
  def this() = this(null, null)

  var roleId = new StringProperty(_roleId)
  var roleDesc = new StringProperty(_roleDesc)

  def save(): Try[Int] = {
    if (!isExist) {
      Try(DB autoCommit { implicit session =>
        sql"""
          INSERT INTO role (role_id, role_desc) VALUES (${roleId.value}, ${roleDesc.value})
        """.update.apply()
      })
    } else {
      Try(DB autoCommit { implicit session =>
        sql"""
          UPDATE role SET role_desc = ${roleDesc.value} WHERE role_id = ${roleId.value}
        """.update.apply()
      })
    }
  }

  def delete(): Try[Int] = {
    if (isExist) {
      Try(DB autoCommit { implicit session =>
        sql"""
          DELETE FROM role WHERE role_id = ${roleId.value}
        """.update.apply()
      })
    } else
      throw new Exception("Role not exists in the database")
  }

  def isExist: Boolean = {
    DB readOnly { implicit session =>
      sql"""
        SELECT * FROM role WHERE role_id = ${roleId.value}
      """.map(rs => rs.string("role_id")).single.apply()
    } match {
      case Some(_) => true
      case None => false
    }
  }
}

object Role extends Database {
  def apply(_roleId: String, _roleDesc: String): Role = {
    new Role(_roleId, _roleDesc)
  }

  def initializeTable() = {
    DB autoCommit { implicit session =>
      sql"""
        CREATE TABLE role (
          role_id VARCHAR(50) NOT NULL,
          role_desc VARCHAR(30),
          PRIMARY KEY (role_id)
        )
      """.execute.apply()
    }
  }

  def getAllRoles: List[Role] = {
    DB readOnly { implicit session =>
      sql"SELECT * FROM role".map(rs => Role(rs.string("role_id"), rs.string("role_desc"))).list.apply()
    }
  }
}
