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

  implicit val googlePlaceWrites: OWrites[GooglePlace] = new OWrites[GooglePlace] {
    def writes(googlePlace: GooglePlace) = Json.obj(
      JSON_KEY_NAME -> JsString(googlePlace.name),
      JSON_KEY_FORMATTED_ADDRESS -> JsString(googlePlace.address),
      JSON_KEY_PLACE_ID -> JsString(googlePlace.placeId),
      JSON_KEY_LOCATION -> Json.toJson(googlePlace.location)
    )
  }

  implicit val googlePlaceReads: Reads[GooglePlace] = (
    (__ \ JSON_KEY_NAME).read[String] and
      (__ \ JSON_KEY_PLACE_ID).read[String] and
      (__ \ JSON_KEY_FORMATTED_ADDRESS).read[String] and
      (__ \ JSON_KEY_GEOMETRY \ JSON_KEY_LOCATION).read[Location]
    )(GooglePlace.apply _)

}
