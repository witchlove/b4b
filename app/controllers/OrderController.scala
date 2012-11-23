package controllers

import anorm.NotAssigned
import anorm.Pk
import models.Order
import play.api.data.Form
import play.api.data.Forms.ignored
import play.api.data.Forms.mapping
import play.api.data.Forms.nonEmptyText
import play.api.data.Forms.of
import play.api.data.format.Formats.longFormat
import play.api.mvc.Action
import play.api.mvc.Controller
import views.html
import play.api.data._
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import models.OrderItem
import org.omg.CosNaming.NamingContextPackage.NotFound

object OrderController extends Controller {

  val Home = Redirect(routes.OrderController.list(0, 2, ""))
  
  val orderForm: Form[Order] = Form(
    mapping(
      "id" -> ignored(NotAssigned: Pk[Long]),
      "orderCode" -> nonEmptyText,
      "salesPersonId" -> of[Long],
      "orderItems" -> optional(seq(
        mapping(
          "id" -> ignored(NotAssigned: Pk[Long]),
          "productId" -> of[Long],
          "quantity" -> of[Long])(OrderItem.apply)(OrderItem.unapply))
        ))(Order.apply)(Order.unapply))

  def create = Action {
    Ok(html.orders.createForm(orderForm))
  }

  def list(page: Int, orderBy: Int, filter: String) = Action { implicit request =>
    Ok(html.orders.ordersOverview(
      Order.list(page = page, orderBy = orderBy, filter = ("%" + filter + "%")),
      orderBy, filter))
  }

  def edit(id: Long) = Action {
	  NotFound
  }

  def save = Action { implicit request =>
    orderForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.orders.createForm(formWithErrors)),
      order => {
        Order.insert(order)
        Home
      })

  }

  def update = TODO

}