import play.api.GlobalSettings
import play.api._
import play.api.mvc._
import play.api.mvc.Results.BadRequest

object Global extends GlobalSettings {
	
  override def onBadRequest(request: RequestHeader, error: String) = {
	Logger.debug("error => " + error)
    BadRequest("Bad Request: " + error)
  } 
}