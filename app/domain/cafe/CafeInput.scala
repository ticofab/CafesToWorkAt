package domain.cafe

import play.api.libs.functional.syntax._
import play.api.libs.json._

final case class CafeInput(name: String,
                           city: String,
                           country: String)

object CafeInput {

  val JSON_KEY_NAME = "name"
  val JSON_KEY_CITY = "city"
  val JSON_KEY_COUNTRY = "country"

  implicit val cafeInputWrites: OWrites[CafeInput] = new OWrites[CafeInput] {
    def writes(cafeInput: CafeInput) = Json.obj(
      JSON_KEY_NAME -> JsString(cafeInput.name),
      JSON_KEY_CITY -> JsString(cafeInput.city),
      JSON_KEY_COUNTRY -> JsString(cafeInput.country)
    )
  }

  def onlyFields(allowed: String*): Reads[JsObject] = Reads.verifying(_.keys.forall(allowed.contains))

  implicit val cafeInputReads: Reads[CafeInput] =
    onlyFields(JSON_KEY_NAME, JSON_KEY_CITY, JSON_KEY_COUNTRY) andThen (
      (__ \ JSON_KEY_NAME).read[String] and
        (__ \ JSON_KEY_CITY).read[String] and
        (__ \ JSON_KEY_COUNTRY).read[String]
      )(CafeInput.apply _)
}
