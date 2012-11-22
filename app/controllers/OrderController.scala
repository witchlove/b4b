package controllers

import models.Order
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.data.Forms._
import play.api.data.Forms.text
import play.api.data.format.Formats._
import play.api.mvc.Action
import play.api.mvc.Controller
import views.html

object OrderController extends Controller {
  
  val orderForm : Form[Order] = Form (
    mapping("orderDesc" ->text,
        "salespersonId" ->of[Long]
    )
    (Order.apply)
    (Order.unapply)
  )
  
  /**
   * Display an empty form.
   */
  def form = Action {
    Ok(html.orders.form(orderForm));
  }
  
  /**
   * Display a form pre-filled with an existing Contact.
   */
  def editForm(orderId : Long) = Action {
    val existingOrder = Order("tuinkabouter",orderId)
    Ok(html.orders.form(orderForm.fill(existingOrder)))
  }
  
  /**
   * Handle form submission.
   */
  def submit = Action { implicit request =>
    orderForm.bindFromRequest.fold(
      errors => {
    	  println( errors.globalErrors)
    	  println(errors.errors mkString "\n")
    	  BadRequest(html.orders.form(errors))
      },
      order => {
    	  Order.create(order.orderDesc)
    	  Redirect(routes.Application.index)
      }
    )
  }
}