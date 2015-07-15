package domain.location

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

final case class Location(longitude: Double, latitude: Double)

object Location {

  // these fields are like this to be compatible with Google
  val JSON_KEY_LATITUDE = "lat"
  val JSON_KEY_LONGITUDE = "lng"

  implicit val locationWrites: OWrites[Location] = new OWrites[Location] {
    override def writes(location: Location) = Json.obj(
      JSON_KEY_LONGITUDE -> location.longitude,
      JSON_KEY_LATITUDE -> location.latitude
    )
  }

  implicit val locationReads: Reads[Location] = (
    (__ \ JSON_KEY_LONGITUDE).read[Double] and
      (__ \ JSON_KEY_LATITUDE).read[Double]
    )(Location.apply _)

}
