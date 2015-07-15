package controllers.test

import java.util.UUID
import javax.inject.Inject

import domain.cafe.{Cafe, CafeRepository}
import domain.location.Location
import play.api.libs.json._
import play.api.mvc.{Action, Controller}
import services.GoogleMapsApiClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

/*
 * I will use this object to test stuff as I go.
 */
class TestController @Inject()(val cafeRepository: CafeRepository) extends Controller {

  // method to test the TO-DO built-in feature of play
  def todoTest = TODO

  // method to test parsing a Json body
  def parseJsonBody = Action(parse.json) {
    request =>
      val body: JsValue = request.body
      Ok(s"received: $body")
  }

  // serves a Json representation of a Cafe object using its implicit writes
  def sendBackCafeAsJson = Action {
    request =>
      val cafe = Cafe(
        UUID.randomUUID(),
        "fake_place_id_1111",
        "Drovers Dog",
        "Eerste Atjehstraat",
        Location(21.3, 44.5))

      Ok(Json.toJson(cafe))
  }

  // test a Cafe object sent as a json request body
  def parseJsonBodyAsCafe = Action(parse.json) {
    request =>
      val body: JsValue = request.body
      val cafe: JsResult[Cafe] = body.validate[Cafe]
      cafe match {
        case success: JsSuccess[Cafe] => Ok(s"Thanks for this Cafe object: ${success.get}")
        case error: JsError => UnprocessableEntity(s"Error parsing object: ${JsError.toJson(error)}")
      }
  }

  // insert a cafe into the db
  def insertCafeIntoDb() = Action.async(parse.json) {
    request =>
      request.body.validate[Cafe] match {

        case success: JsSuccess[Cafe] =>
          val futureInsert = cafeRepository.insert(success.get)
          futureInsert.map {
            case Right(cafe) => Ok(Json.toJson(success.get))
            case Left(message) => InternalServerError(message)
          }

        case error: JsError => Future {
          UnprocessableEntity(s"Error parsing object: ${JsError.toJson(error)}")

        }
      }
  }

  // get cafe from db
  def getCafeFromDb(id: String) = Action.async {
    request =>
      // try to parse the id as a UUID object
      Try(UUID.fromString(id)) match {

        case Failure(t) =>
          // we couldn't parse the UUID
          Future {
            BadRequest(t.getMessage)
          }

        case Success(uuid) =>
          // try to get the cafe from the database
          cafeRepository.get(uuid).map {
            case Right(cafe) => Ok(Json.toJson(cafe))
            case Left(message) => Ok(message)
          }

      }
  }

  def geocodingTest(address: String) = Action.async {
    request => {
      GoogleMapsApiClient.geocode(address)
        .map {
        case Right((lat, lon)) => Ok(lat.toString + "," + lon.toString)
        case Left(t) => NotFound
      } recover {
        case _ => InternalServerError
      }
    }
  }

  def searchTest(query: String) = Action.async {
    request => {
      GoogleMapsApiClient.searchPlace(query)
        .map {
        case Right(jsonResp) => Ok(Json.toJson(jsonResp))
        case Left(t) => NotFound(t)

      } recover {
        case _ => InternalServerError
      }
    }
  }

  def detailsTest(placeId: String) = Action.async {
    request => {
      GoogleMapsApiClient.detail(placeId)
        .map {
        case Right(result) => Ok(result)
        case Left(t) => NotFound
      } recover {
        case _ => InternalServerError
      }
    }
  }

}
