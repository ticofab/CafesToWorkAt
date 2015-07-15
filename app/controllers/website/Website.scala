package controllers.website

import play.api.mvc._

class Website extends Controller {

  // TODO: http://www.ybrikman.com/writing/2015/06/30/ping-play-big-pipe-streaming-for-play-framework/
  // TODO: http://reactfordesigners.com/labs/reactjs-introduction-for-people-who-know-just-enough-jquery-to-get-by

  def homepage = Action {
    Ok(views.html.index("Homepage of CafesToWorkAt"))
  }

  def cafesAtLocation(location: String) = Action {
    Ok(views.html.cafesatlocation(location))
  }

  def add = Action {
    Ok(views.html.addcafe())
  }

}
