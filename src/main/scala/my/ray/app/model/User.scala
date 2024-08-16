package my.ray.app.model

import scalafx.beans.property.{ObjectProperty, StringProperty}
import scalikejdbc._
import java.sql.SQLException
import java.time.LocalDateTime
import java.util.UUID
import scala.util.{Failure, Success, Try}

case class User(
                 userId: String,
                 userName: String,
                 telNo: String,
                 icNo: String,
                 dob: LocalDateTime,
                 address: String,
                 email: String,
                 password: String,
                 status: String,
                 createdDate: LocalDateTime,
                 updatedDate: LocalDateTime,
                 role: String
               ) {
  val userIdProperty = new StringProperty(userId)
  var userNameProperty = new StringProperty(userName)
  var telNoProperty = new StringProperty(telNo)
  var icNoProperty = new StringProperty(icNo)
  var dobProperty = ObjectProperty[LocalDateTime](dob)
  var addressProperty = new StringProperty(address)
  var emailProperty = new StringProperty(email)
  var passwordProperty = new StringProperty(password)
  var statusProperty = new StringProperty("ACTIVE")
  val createdDateProperty = ObjectProperty[LocalDateTime](LocalDateTime.now())
  var updatedDateProperty = ObjectProperty[LocalDateTime](LocalDateTime.now())
  val roleProperty = new StringProperty(role)


  def save(): Try[Int] = {
    if (!exists()) {
      Try(DB autoCommit { implicit session =>
        sql"""
          insert into users (user_id, user_name, tel_no, ic_no, dob, address, email, password, status, created_date, updated_date, role) values
          (${userIdProperty.value}, ${userNameProperty.value}, ${telNoProperty.value}, ${icNoProperty.value}, ${dobProperty.value}, ${addressProperty.value}, ${emailProperty.value}, ${passwordProperty.value}, ${statusProperty.value}, ${createdDateProperty.value}, ${updatedDateProperty.value}, ${roleProperty.value})
        """.update.apply()
      })
    } else {
      update()
    }
  }

  def update(): Try[Int] = Try(DB autoCommit { implicit session =>
    sql"""
      update users set
      user_name = ${userNameProperty.value},
      tel_no = ${telNoProperty.value},
      ic_no = ${icNoProperty.value},
      dob = ${dobProperty.value},
      address = ${addressProperty.value},
      email = ${emailProperty.value},
      password = ${passwordProperty.value},
      status = ${statusProperty.value},
      updated_date = ${updatedDateProperty.value},
      role = ${roleProperty.value}
      where user_id = ${userIdProperty.value}
    """.update.apply()
  })

  def delete(): Try[Int] = {
    if (exists()) {
      Try(DB autoCommit { implicit session =>
        sql"""
          delete from users where user_id = ${userIdProperty.value}
        """.update.apply()
      })
    } else
      Failure(new Exception("User not Exists in Database"))
  }

  def exists(): Boolean = {
    DB readOnly { implicit session =>
      sql"""
        select 1 from users where user_id = ${userIdProperty.value}
      """.map(rs => rs.int(1)).single.apply().isDefined
    }
  }
}

object User {
  def apply(
             userId: String,
             userName: String,
             telNo: String,
             icNo: String,
             dob: LocalDateTime,
             address: String,
             email: String,
             password: String,
             status: String,
             createdDate: LocalDateTime,
             updatedDate: LocalDateTime,
             role: String
           ): User = new User(userId, userName, telNo, icNo, dob, address, email, password, status, createdDate, updatedDate, role)

  def initializeTable(): Unit = {
    DB autoCommit { implicit session =>
      try {
        sql"""
        create table users (
          user_id varchar(50) not null primary key,
          user_name varchar(20),
          tel_no varchar(20),
          ic_no varchar(20),
          dob timestamp,
          address varchar(50),
          email varchar(100),
          password varchar(20),
          status varchar(10),
          created_date timestamp,
          updated_date timestamp,
          role varchar(20)
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

  def findByEmail(userEmail: String): Option[User] = {
    DB readOnly { implicit session =>
      sql"""
        select * from users where email = $userEmail
      """.map(rs =>
        User(
          rs.string("user_id"),
          rs.string("user_name"),
          rs.string("tel_no"),
          rs.string("ic_no"),
          rs.timestamp("dob").toLocalDateTime,
          rs.string("address"),
          rs.string("email"),
          rs.string("password"),
          rs.string("status"),
          rs.timestamp("created_date").toLocalDateTime,
          rs.timestamp("updated_date").toLocalDateTime,
          rs.string("role")
        )
      ).single.apply()
    }
  }

  def getUserNameByEmail(userEmail: String): Option[User] = {
    DB readOnly { implicit session =>
      sql"""
        select distinct user_name from users where email = $userEmail
      """.map(rs =>
        User(
          rs.string("user_id"),
          rs.string("user_name"),
          rs.string("tel_no"),
          rs.string("ic_no"),
          rs.timestamp("dob").toLocalDateTime,
          rs.string("address"),
          rs.string("email"),
          rs.string("password"),
          rs.string("status"),
          rs.timestamp("created_date").toLocalDateTime,
          rs.timestamp("updated_date").toLocalDateTime,
          rs.string("role")
        )
      ).single.apply()
    }
  }

  def allUsers: List[User] = {
    DB readOnly { implicit session =>
      sql"select * from users".map(rs =>
        User(
          rs.string("user_id"),
          rs.string("user_name"),
          rs.string("tel_no"),
          rs.string("ic_no"),
          rs.timestamp("dob").toLocalDateTime,
          rs.string("address"),
          rs.string("email"),
          rs.string("password"),
          rs.string("status"),
          rs.timestamp("created_date").toLocalDateTime,
          rs.timestamp("updated_date").toLocalDateTime,
          rs.string("role")
        )
      ).list.apply()
    }
  }
}
