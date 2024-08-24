package my.ray.app.model

import scalafx.scene.media.Media

trait Consumable {
    val eatSound: Media = new Media(getClass.getResource("/audios/consumable.mp3").toExternalForm)
}
