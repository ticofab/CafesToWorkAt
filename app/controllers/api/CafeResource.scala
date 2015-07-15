package controllers.api

import java.util.UUID
import javax.inject.Inject

import domain.cafe.{Cafe, CafeRepository}
import domain.googleplace.GooglePlace
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.{Action, Controller, Result}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

class CafeResource @Inject()(cafeRepository: CafeRepository) extends Controller {

  /**
   * Endpoint to insert a new Cafe object in the database.
   *
   * @return An HTTP status describing the outcome of the request. Upon success, the newly
   *         inserted Cafe is returned in its Json representation.
   */
  def post = Action.async(parse.json) {
    implicit request =>
      request.body.validate[GooglePlace] match {

        case success: JsSuccess[GooglePlace] =>

          // we successfully parsed a google place object
          val googlePlace: GooglePlace = success.get

          // create our own cafe object
          val cafe: Cafe = Cafe(
            UUID.randomUUID(),
            googlePlace.placeId,
            googlePlace.name,
            googlePlace.address,
            googlePlace.location)

          // store object in repository
          cafeRepository.insert(cafe).map {
            case Right(insertedCafe) => okCafeStatus(insertedCafe)
            case Left(message) => InternalServerError(message)
          }.recover { case t => InternalServerError(t.getMessage) }

        case error: JsError =>

          // error parsing Json
          Future(unprocessableEntityStatus(error))

      }
  }

  /**
   * Retrieves a single Cafe object from the database.
   *
   * @param id The unique id of the Cafe object to retrieve.
   *
   * @return An HTTP status describing the outcome of the operation. If successful, the response
   *         includes the retrieved object in its Json representation.
   */
  def get(id: String) = Action.async {
    implicit request =>

      // operation to perform if the id is valid
      def getFromUUID(uuid: UUID) = cafeRepository.get(uuid).map {

        case Right(maybeCafe) => maybeCafe match {
          case Some(cafe) => okCafeStatus(cafe)
          case None => notFoundStatus(uuid)
        }

        case Left(message) => InternalServerError(message)
      }

      parseIdAndPerformOperation(id, getFromUUID)
  }

  /**
   * Removes a Cafe object from the database.
   *
   * @param id The unique id of the Cafe object to remove.
   *
   * @return An HTTP status describing the outcome of the operation.
   */
  def delete(id: String) = Action.async {
    implicit request =>

      // operation to perform if the id is valid
      def deleteFromUUID(uuid: UUID) = cafeRepository.remove(uuid).map {
        case Right(updated) => if (updated == 1) NoContent else notFoundStatus(uuid)
        case Left(error) => InternalServerError(error)
      }

      parseIdAndPerformOperation(id, deleteFromUUID)
  }

  /**
   * Updates an  existing Cafe entry. If the specified ID exists, the existing entry is completely replaced with
   * the Cafe object in the body of the request.
   *
   * @param id The unique id of the object to replace.
   *
   * @return A HTTP status containing the newly inserted object or a description of the error occurred.
   */
  def update(id: String) = Action.async(parse.json) {
    implicit request =>

      def updateFromUUID(uuid: UUID): Future[Result] = cafeRepository.exists(uuid).flatMap {
        isThere => {

          def update(cafe: Cafe): Future[Result] = {
            cafeRepository.update(cafe).map {
              case Right(updatedCafe) => okCafeStatus(updatedCafe)
              case Left(message) => InternalServerError(message)
            }
          }

          if (isThere) {
            request.body.validate[Cafe] match {
              case success: JsSuccess[Cafe] => update(success.get)
              case error: JsError => Future {
                unprocessableEntityStatus(error)
              }
            }
          } else Future {
            notFoundStatus(uuid)
          }
        }
      }

      parseIdAndPerformOperation(id, updateFromUUID)
  }

  /**
   * Higher-order function that tries to parse the provided id and then applies the
   * supplied function to the parsed id.
   *
   * @param id The id of the object on which to perform the operation.
   * @param operation The operation to apply to the supplied id
   *
   * @return A Future[Result] object
   */
  private def parseIdAndPerformOperation(id: String, operation: UUID => Future[Result]): Future[Result] = {
    // try to parse the id as a UUID object
    Try(UUID.fromString(id)) match {

      case Failure(e) =>
        // we couldn't parse the id
        Future(BadRequest(e.getMessage))

      case Success(uuid) => operation(uuid).recover {
        case e => InternalServerError(e.getMessage)
      }

    }

  }

  def okCafeStatus(cafe: Cafe) = Ok(Json.toJson(cafe))

  def notFoundStatus(uuid: UUID) = NotFound(s"Couldn't find object with id ${uuid.toString}")

  def unprocessableEntityStatus(jsError: JsError) = UnprocessableEntity(s"Error parsing object: ${JsError.toJson(jsError)}")

}
