package domain.googleplace

import domain.location.Location
import play.api.libs.functional.syntax._
import play.api.libs.json._

final case class GooglePlace(name: String,
                             placeId: String,
                             address: String,
                             location: Location)

object GooglePlace {

  val JSON_KEY_FORMATTED_ADDRESS = "formatted_address"
  val JSON_KEY_GEOMETRY = "geometry"
  val JSON_KEY_LOCATION = "location"
  val JSON_KEY_PLACE_ID = "place_id"
  val JSON_KEY_NAME = "name"

  implicit val googlePlaceWrites: OWrites[GooglePlace] = (
    (__ \ JSON_KEY_NAME).write[String] and
      (__ \ JSON_KEY_PLACE_ID).write[String] and
      (__ \ JSON_KEY_FORMATTED_ADDRESS).write[String] and
      (__ \ JSON_KEY_LOCATION).write[Location]
    )(unlift(GooglePlace.unapply))

  implicit val googlePlaceReads: Reads[GooglePlace] = (
    (__ \ JSON_KEY_NAME).read[String] and
      (__ \ JSON_KEY_PLACE_ID).read[String] and
      (__ \ JSON_KEY_FORMATTED_ADDRESS).read[String] and
      (__ \ JSON_KEY_GEOMETRY \ JSON_KEY_LOCATION).read[Location]
    )(GooglePlace.apply _)

}
