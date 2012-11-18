package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import models.Order


object Application extends Controller {
  
  val orderForm = Form(
  mapping(
    "id" -> of[Long],
    "orderDesc" -> text
  )
  (Order.apply)(Order.unapply)
)
  
  
  def index = Action {
    Redirect(routes.Application.orders)
  }
  
  def orders = Action {
    Ok(views.html.index(Order.all,orderForm))
  }
  
  def newOrder = Action { implicit request => 
    	orderForm.bindFromRequest.fold( 
    	    errors => BadRequest(views.html.index(Order.all(), errors)),
    	    order => {
    	    	Order.create(order.orderDesc)
    	    	Redirect(routes.Application.orders)
    	    })
  }
  
  def deleteOrder(id : Long) = Action {
    Order.delete(id)
    Redirect(routes.Application.orders)
  }
}