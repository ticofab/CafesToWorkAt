package domain.cafe

import java.util.UUID
import javax.inject.Inject

import domain.location.Location
import play.api.libs.json.Json
import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.json.collection.JSONCollection

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * This object provides access to the persistence layer of Cafe objects.
 */
class CafeRepository @Inject()(val reactiveMongoApi: ReactiveMongoApi) {

  lazy val db = reactiveMongoApi.db

  def cafes: JSONCollection = db.collection[JSONCollection]("cafes")

  /**
   * Inserts a Cafe object in the database.
   *
   * @param cafe The Cafe object to insert.
   *
   * @return A Future containing either the inserted object or an error message.
   */
  def insert(cafe: Cafe): Future[Either[String, Cafe]] = {
    val document = Json.toJson(cafe)
    cafes.insert(document).map(wr => if (wr.ok) Right(cafe) else Left(wr.getMessage()))
      .recover { case t => Left(t.getMessage) }
  }

  /**
   * Removes a cafe from the database.
   *
   * @param id The unique id of the Cafe to remove.
   *
   * @return A Future containing either the number of removed elements or a String with the error message.
   */
  def remove(id: UUID): Future[Either[String, Int]] = {
    val selector = Json.obj(Cafe.JSON_KEY_ID -> id.toString)
    cafes.remove(selector).map(wr => if (wr.ok) Right(wr.n) else Left(wr.getMessage()))
      .recover { case t => Left(t.getMessage) }
  }

  /**
   * Looks the unique object associated with the supplied id.
   *
   * @param id The unique id of the object to retrieve.
   *
   * @return a Future of an Option[Cafe] containing the café object if found or None
   */
  def get(id: UUID): Future[Either[String, Option[Cafe]]] = {
    val selector = Json.obj(Cafe.JSON_KEY_ID -> id.toString)
    cafes.find(selector).one[Cafe].map(x => Right(x))
      .recover { case t => Left(t.getMessage) }
  }

  /**
   * Lists all cafes within the specified range of Location.
   *
   * @param location A Location object.
   * @param range The range, expressed in meters.
   *
   * @return A List of Cafes
   */
  def get(location: Location, range: Int): Future[Either[String, List[Cafe]]] = {
    ???
  }

  /**
   * Updates a cafe entry in the database.
   *
   * @param cafe The new Cafe.
   *
   * @return
   */
  def update(cafe: Cafe): Future[Either[String, Cafe]] = {
    val selector = Json.obj(Cafe.JSON_KEY_ID -> cafe.id.toString)
    cafes.update(selector, Json.toJson(cafe)).map(wr => if (wr.ok) Right(cafe) else Left(wr.getMessage()))
      .recover { case t => Left(t.getMessage) }
  }

  /**
   * Checks if an item with the given id already exists.
   *
   * @param id A valid UUID
   *
   * @return a Future[Boolean], true or false if the object exists or not.
   */
  def exists(id: UUID): Future[Boolean] = {
    val selector = Json.obj(Cafe.JSON_KEY_ID -> id.toString)
    cafes.count(Some(selector), 1).map(_ != 0)
  }
}
