package services

import domain.googleplace.GooglePlace
import play.api.Play.current
import play.api.libs.json.Json
import play.api.libs.ws.WS

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object GoogleMapsApiClient {
  val TIMEOUT = 3000
  val KEY_API_KEY = "key"
  val API_KEY = "AIzaSyDhE9hLcFrClAf-zvBX8eXr5IWcMmjRJwI"

  // geocoder
  val GEOCODER_BASE_URL = "https://maps.googleapis.com/maps/api/geocode/json?"
  val KEY_ADDRESS = "address"

  // search
  val SEARCH_BASE_URL = "https://maps.googleapis.com/maps/api/place/textsearch/json?"
  val KEY_QUERY = "query"
  val MAX_SEARCH_RESULTS = 3

  // detail
  val DETAIL_BASE_URL = "https://maps.googleapis.com/maps/api/place/details/json?"
  val KEY_PLACE_ID = "placeid"

  def geocode(address: String): Future[Either[String, (Double, Double)]] = {
    WS.url(GEOCODER_BASE_URL)
      .withQueryString(KEY_ADDRESS -> address, KEY_API_KEY -> API_KEY)
      .withRequestTimeout(TIMEOUT)
      .get()
      .map {
      response =>
        // TODO: parse it better. Maybe a custom object and a Json Writes?
        val jsonLocation = response.json \\ "location"
        val latitude = (jsonLocation.head \\ "lat").head.toString().toDouble
        val longitude = (jsonLocation.head \\ "lng").head.toString().toDouble
        Right((latitude, longitude))
    }.recover {
      case t => Left(t.getMessage)
    }
  }

  def searchPlace(query: String): Future[Either[String, List[GooglePlace]]] = {
    val JSON_KEY_RESULTS = "results"

    WS.url(SEARCH_BASE_URL)
      .withQueryString(KEY_QUERY -> query, KEY_API_KEY -> API_KEY)
      .withRequestTimeout(TIMEOUT)
      .get()
      .map {
      response => {
        val results = (response.json \ JSON_KEY_RESULTS).as[List[GooglePlace]]
        Right(results.take(MAX_SEARCH_RESULTS))
      }
    }.recover {
      case t => Left(t.getMessage)
    }
  }

  def detail(placeId: String) = {
    WS.url(DETAIL_BASE_URL)
      .withQueryString(KEY_PLACE_ID -> placeId, KEY_API_KEY -> API_KEY)
      .withRequestTimeout(TIMEOUT)
      .get()
      .map {
      response => Right(Json.prettyPrint(response.json))
    }.recover {
      case t => Left(t.getMessage)
    }
  }

}
