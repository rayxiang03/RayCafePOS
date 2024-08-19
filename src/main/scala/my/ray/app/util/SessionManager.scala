package my.ray.app.util

import my.ray.app.model.User
import scala.collection.mutable

object SessionManager {

  // A mutable map to store session IDs and associated user
  private val sessions = mutable.Map[String, User]()

  // The current session ID (single active session)
  private var currentSessionId: Option[String] = None

  // Generate a new session ID
  private def generateSessionId(): String = java.util.UUID.randomUUID().toString

  // Start a new session for a user
  def startSession(user: User): String = {
    // Invalidate any existing session
    endSession()

    // Generate a new session ID and store it
    val sessionId = generateSessionId()
    currentSessionId = Some(sessionId)
    sessions(sessionId) = user
    println(s"Current Session ID: $sessionId, User: ${user.userName}")
    sessionId
  }

  // End the current session
  def endSession(): Unit = {
    currentSessionId.foreach { id =>
      println("Terminated session: " + id)
      sessions.remove(id)
      currentSessionId = None
    }
  }

  def getCurrentUser: Option[User] = {
    currentSessionId.flatMap(sessions.get)
  }

}
