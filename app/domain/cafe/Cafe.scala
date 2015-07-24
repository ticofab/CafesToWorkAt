package domain.cafe

import java.util.UUID

import domain.location.Location
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

final case class Cafe(id: UUID,
                      placeId: String,
                      name: String,
                      address: String,
                      location: Location)

object Cafe {

  val JSON_KEY_ID = "_id"
  val JSON_KEY_NAME = "name"
  val JSON_KEY_FORMATTED_ADDRESS = "formatted_address"
  val JSON_KEY_LOCATION = "location"
  val JSON_KEY_PLACE_ID = "place_id"

  implicit val cafeWrites: OWrites[Cafe] = (
    (__ \ JSON_KEY_ID).write[UUID] and
      (__ \ JSON_KEY_PLACE_ID).write[String] and
      (__ \ JSON_KEY_NAME).write[String] and
      (__ \ JSON_KEY_FORMATTED_ADDRESS).write[String] and
      (__ \ JSON_KEY_LOCATION).write[Location]
    )(unlift(Cafe.unapply))

  implicit val cafeReads: Reads[Cafe] = (
    (__ \ JSON_KEY_ID).read[UUID] and
      (__ \ JSON_KEY_PLACE_ID).read[String] and
      (__ \ JSON_KEY_NAME).read[String] and
      (__ \ JSON_KEY_FORMATTED_ADDRESS).read[String] and
      (__ \ JSON_KEY_LOCATION).read[Location]
    )(Cafe.apply _)

  /*
   * TODO: this is an example of an "artisanal" Reads that I could use instead.
  implicit val cafeReads2: Reads[Cafe] = new Reads[Cafe] {

    override def reads(json: JsValue): JsResult[Cafe] = {

      val j1: JsValue = json \ JSON_KEY_NAME
      val j2: JsValue = json \ JSON_KEY_ID

      val idOpt: Option[UUID] = j2.asOpt[UUID]
      val nameOpt: Option[String] = j1.asOpt[String]

      val cafe = Cafe(idOpt, nameOpt)

      JsSuccess(cafe)
    }
  }

   */
}
