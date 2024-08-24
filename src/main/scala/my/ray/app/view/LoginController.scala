package my.ray.app.view

import my.ray.app.MainApp
import my.ray.app.model.User
import my.ray.app.util.SessionManager
import scalafx.animation.{FadeTransition, PauseTransition, ScaleTransition, TranslateTransition}
import scalafx.scene.effect.GaussianBlur
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.media.{Media, MediaPlayer, MediaView}
import scalafx.util.Duration
import scalafxml.core.macros.sfxml
import scalafx.Includes._
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, ButtonType, Label, TextField}
import scalafx.scene.input.MouseEvent
import scalafx.scene.paint.Color
import scalafx.scene.text.{Font, Text, TextFlow}

import scala.util.Random


@sfxml
class LoginController(
                       private val mediaView: MediaView,
                       private val imageView: ImageView,
                       private val welcomeTextFlow: TextFlow,
                       private val emailField: TextField,
                       private val passwordField: TextField,
                       private val logoutMessageLabel: Label
                     ) {

  private var mediaPlayer: MediaPlayer = _
  private val random = new Random()


  // Initialize method to set up the video and image animation
  initialize()

  private def initialize(): Unit = {
    clearLogoutMessage()
    setupBackgroundVideo()
    setupLogoAnimation()
    setupHoverEffects()
    setupWelcomeTextAnimation()
  }

  private def setupBackgroundVideo(): Unit = {
    // Specify the path to your video file
    val videoPath = getClass.getResource("/videos/Login_Background.mp4").toURI.toString

    // Create a Media object for the video
    val media = new Media(videoPath)

    // Create a MediaPlayer to control playback
    val mediaPlayer = new MediaPlayer(media)

    // Link the MediaPlayer to the MediaView
    mediaView.mediaPlayer = mediaPlayer

    // Optionally start the video automatically
    mediaPlayer.setAutoPlay(true)

    // Add an onEndOfMedia event handler to replay the video
    mediaPlayer.onEndOfMedia = () => {
      mediaPlayer.seek(javafx.util.Duration.ZERO) // Rewind to the beginning
      mediaPlayer.play()
    }

    // Set the image for the ImageView
    setImage()

    // Setup the ImageView animation
    setupLogoAnimation()
  }

  private def setImage(): Unit = {
    // Load the image from the resources directory
    val imagePath = getClass.getResource("/images/main_logo2.png").toExternalForm
    val image = new Image(imagePath)
    imageView.image = image
  }


  private def setupLogoAnimation(): Unit = {
    // Create a GaussianBlur effect and set it to the ImageView
    val blurEffect = new GaussianBlur(20) // Start with high blur
    imageView.effect = blurEffect

    // Create a TranslateTransition for the ImageView
    val translateTransition = new TranslateTransition(Duration(2000), imageView) // 2 seconds duration
    translateTransition.fromY = imageView.translateY.value // Start position
    translateTransition.toY = imageView.translateY.value - 50 // Move up by 50 pixels

    // Create a FadeTransition to gradually change opacity
    val fadeTransition = new FadeTransition(Duration(3800), imageView)
    fadeTransition.fromValue = 0.0 // Start fully transparent
    fadeTransition.toValue = 1.0 // End fully visible

    // Combine the fade transition to clear the blur effect
    fadeTransition.onFinished = _ => {
      imageView.effect = new GaussianBlur(0) // Fully clear after fade-in
    }

    // Play the animations together
    translateTransition.play()
    fadeTransition.play()

  }

  private def setupHoverEffects(): Unit = {
    // Load the sound file
    val soundPath = getClass.getResource("/audios/moo_sound.mp3").toURI.toString
    val sound = new Media(soundPath)
    mediaPlayer = new MediaPlayer(sound)

    // Create a ScaleTransition for the ImageView
    val scaleTransition = new ScaleTransition(Duration(300), imageView)
    scaleTransition.toX = 1.1 // Scale up by 10%
    scaleTransition.toY = 1.1 // Scale up by 10%
    scaleTransition.interpolator = scalafx.animation.Interpolator.EaseIn // or use another available interpolator

    // Mouse hover effect: play sound and enlarge image
    imageView.onMouseEntered = (e: MouseEvent) => {
      mediaPlayer.stop() // Ensure the sound is stopped before playing again
      mediaPlayer.play() // Play the sound
      scaleTransition.playFromStart() // Enlarge the image
    }

    // Mouse exit effect: reset scale
    imageView.onMouseExited = (e: MouseEvent) => {
      // Create a ScaleTransition for reset effect
      val resetScaleTransition = new ScaleTransition(Duration(300), imageView)
      resetScaleTransition.toX = 1.0 // Reset scale to 100%
      resetScaleTransition.toY = 1.0 // Reset scale to 100%
      resetScaleTransition.interpolator = scalafx.animation.Interpolator.EaseOut

      resetScaleTransition.playFromStart() // Reset the image size
    }
  }

  private def setupWelcomeTextAnimation(): Unit = {
    val textToDisplay = "Welcome to RAY's Cafe Management System!"
    val underscore = "|"


    // Create the Text objects
    val mainText = new Text(textToDisplay) {
      font = Font.font("Consolas", 25)
      fill = Color.White
    }

    val blinkText = new Text(underscore) {
      font = Font.font("Arial", 28)
      fill = Color.Beige
      opacity = 0.0
    }

    // Add the Text objects to a TextFlow
    welcomeTextFlow.children = Seq(mainText, blinkText)

    // Randomized typing and deleting speeds
    def randomDelay(min: Int, max: Int): Duration =
      Duration(random.nextInt(max - min + 1) + min)

    // Function to type the text with an underscore
    def typeText(index: Int): Unit = {
      if (index <= textToDisplay.length) {
        mainText.text = textToDisplay.substring(0, index)
        blinkText.opacity = 1.0
        new PauseTransition(randomDelay(20, 150)) {
          onFinished = _ => typeText(index + 1)
        }.play()
      } else {
        // Remove the underscore and pause before starting to delete
        new PauseTransition(Duration(1000)) {
          onFinished = _ => deleteText(textToDisplay.length)
        }.play()
      }
    }

    // Function to delete the text
    def deleteText(index: Int): Unit = {
      if (index >= 0) {
        mainText.text = textToDisplay.substring(0, index)
        blinkText.opacity = 1.0 // Show underscore
        new PauseTransition(randomDelay(20, 50)) {
          onFinished = _ => deleteText(index - 1)
        }.play()
      } else {
        blinkText.opacity = 0.0 // Hide underscore
        new PauseTransition(Duration(1000)) {
          onFinished = _ => typeText(0)
        }.play()
      }
    }

    // Function to start blinking the underscore
    def startBlinkingUnderscore(): Unit = {
      def blink(): Unit = {
        val fadeOut = new FadeTransition(Duration(500), blinkText) {
          fromValue = 1.0
          toValue = 0.0
        }
        val fadeIn = new FadeTransition(Duration(500), blinkText) {
          fromValue = 0.0
          toValue = 1.0
        }
        fadeOut.onFinished = _ => {
          blinkText.opacity = 1.0
          fadeIn.play()
        }
        fadeIn.onFinished = _ => {
          blinkText.opacity = 1.0
          fadeOut.play()
        }
        fadeOut.play()
      }

      blink()
    }

    // Start the typing effect and the blinking underscore
    typeText(0)
    startBlinkingUnderscore()
  }


  def handleLogin(): Unit = {
    val email = emailField.text()
    val password = passwordField.text()

    clearLogoutMessage()

    if (email.isEmpty || password.isEmpty) {
      showAlert(AlertType.Error, "Username or password cannot be empty.")
    } else if (!isValidEmail(email)) {
      showAlert(AlertType.Error, "Invalid email format.")
    } else if (!isValidPassword(password)) {
      showAlert(AlertType.Error, "Password must be at least 8 characters long.")
    } else {
      validateCredentials(email, password) match {
        case Some(user) =>
          SessionManager.startSession(user)
          showSuccessAlert(user.userName)
        case None =>
          showAlert(AlertType.Error, "Invalid email or password.")
      }
    }
  }

  def showAlert(alertType: AlertType, content: String): Unit = {
    new Alert(alertType, content, ButtonType.OK) {
      initOwner(null)
      this.title.value = "Login Error"
      this.headerText.value = None.orNull
      showAndWait()
    }
  }

  private def isValidEmail(email: String): Boolean = {
    email.matches("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")
  }

  private def isValidPassword(password: String): Boolean = {
    password.length >= 8
  }

  private def validateCredentials(email: String, password: String): Option[User] = {
    User.findByEmail(email).filter(_.password == password)
  }

  private def showSuccessAlert(userName: String): Unit = {
    val alert = new Alert(AlertType.Information) {
      initOwner(null)
      title = "Login Successful"
      headerText = s"Hej, $userName!"
      contentText = "Login successful! Click OK to continue to the dashboard."
    }

    val result = alert.showAndWait()
    result match {
      case Some(ButtonType.OK) => MainApp.showDashboard()
      case _ => MainApp.showDashboard()
    }
  }

  def clearLogoutMessage(): Unit = {
    logoutMessageLabel.text = ""
  }

  def displayLogoutMessage(): Unit = {
    logoutMessageLabel.text = "Logout Successful !"
  }

}
