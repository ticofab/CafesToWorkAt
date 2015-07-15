package controllers.api

import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import services.GoogleMapsApiClient

import scala.concurrent.ExecutionContext.Implicits.global

class PlaceResource extends Controller {
  /**
   * Endpoint to validate a cafe search query.
   *
   * @param query A query that should be composed ideally by
   *              <name>+<city>+<country>
   *
   * @return An HTTP result. Ok will contain a list (possibly empty) of found matches.
   */
  def validate(query: String) = Action.async {
    implicit request =>
      // validate the query string against the Google Search Api
      GoogleMapsApiClient.searchPlace(query).map {
        case Right(googlePlacesList) => Ok(Json.toJson(googlePlacesList))
        case Left(message) => InternalServerError(message)
      }.recover { case e: Exception => InternalServerError(e.getMessage) }
  }
}
