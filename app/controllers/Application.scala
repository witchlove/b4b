package controllers

import play.api.mvc.Action
import play.api.mvc.Controller
import views.html

object Application extends Controller {
  
  def index = Action {
    Ok(html.index());
  }
  
  
}