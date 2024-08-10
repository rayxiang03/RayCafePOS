package my.ray.app.util

import my.ray.app.MainApp

import scala.collection.mutable

object SessionManager {

  // A mutable map to store session IDs and associated user IDs
  private val sessions = mutable.Map[String, String]()

  // The current session ID (single active session)
  private var currentSessionId: Option[String] = None

  // Generate a new session ID (for simplicity, using a random UUID)
  private def generateSessionId(): String = java.util.UUID.randomUUID().toString

  // Start a new session for a user ID
  def startSession(userId: String): String = {
    // Invalidate any existing session
    endSession()

    // Generate a new session ID and store it
    val sessionId = generateSessionId()
    currentSessionId = Some(sessionId)
    sessions(sessionId) = userId
    println(sessions)
    sessionId
  }

  // Retrieve the user ID associated with the current session ID
  def getCurrentUserId: Option[String] = {
    currentSessionId.flatMap(sessions.get)
  }

  // End the current session
  def endSession(): Unit = {
    currentSessionId.foreach { id =>
      println("Terminated session: " + sessions)
      sessions.remove(id)
      currentSessionId = None
    }
  }

  def isValidSession: Boolean = currentSessionId.isDefined

}
