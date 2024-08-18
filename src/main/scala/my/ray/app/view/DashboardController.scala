package my.ray.app.view

import scalafx.animation.{KeyFrame, Timeline}
import scalafx.application.Platform
import scalafx.scene.control.Label
import scalafx.util.Duration

import java.time.format.DateTimeFormatter
import java.time.{LocalDateTime, ZoneId}
import scalafx.animation.Animation
import scalafx.scene.media.{Media, MediaPlayer, MediaView}
import scalafxml.core.macros.sfxml

@sfxml
class DashboardController(
                           private val dateTimeLabel: Label,
                           private val advertisementView: MediaView
                         ) {

  private val dateFormatter = DateTimeFormatter.ofPattern("EEEE, yyyy-MM-dd HH:mm:ss", java.util.Locale.ENGLISH)

  def initialize(): Unit = {
    startDateTimeUpdate()
    setupadvertisementVideo()
  }

  private def startDateTimeUpdate(): Unit = {
    val timeline = new Timeline {
      keyFrames = Seq(
        KeyFrame(Duration(1000), onFinished = _ => updateDateTime())
      )
      cycleCount = Animation.Indefinite
    }
    timeline.play()
  }

  private def updateDateTime(): Unit = {
    Platform.runLater {
      val currentDateTime = LocalDateTime.now(ZoneId.systemDefault()).format(dateFormatter)
      dateTimeLabel.setText(currentDateTime)
    }
  }

  private def setupadvertisementVideo(): Unit = {
    val videoPath = getClass.getResource("/videos/coffee_brewing.mp4").toURI.toString
    val media = new Media(videoPath)
    val mediaPlayer = new MediaPlayer(media)
    mediaPlayer.autoPlay = true
    mediaPlayer.cycleCount = MediaPlayer.Indefinite
    advertisementView.setMediaPlayer(mediaPlayer)
  }

  initialize()
}
