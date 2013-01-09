package controllers

import java.util.Date
import scala.Option.option2Iterable
import anorm.NotAssigned
import anorm.Pk
import models.Order
import models.OrderItem
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms.ignored
import play.api.data.Forms.mapping
import play.api.data.Forms.nonEmptyText
import play.api.data.Forms.of
import play.api.data.Forms.optional
import play.api.data.Forms.seq
import play.api.data.Forms.text
import play.api.data.format.Formats.dateFormat
import play.api.data.format.Formats.longFormat
import play.api.mvc.Action
import play.api.mvc.Controller
import views.html


object OrderController extends Controller with Secured {

  val Home = Redirect(routes.OrderController.list(0, 2, ""))
  
  val orderForm: Form[Order] = Form(
    mapping(
      "id" -> ignored(NotAssigned: Pk[Long]),
      "orderCode" -> nonEmptyText,
      "salesPersonId" -> of[Long],
      "orderDate" -> of[Date],
      "orderStatus" -> nonEmptyText,
      "orderRemarks" -> optional(text),
      "orderitems" -> optional(seq(
        mapping(
          "id" -> ignored(NotAssigned: Pk[Long]),
          "productId" -> optional(of[Long]),
          "quantity" -> optional(of[Long]))(OrderItem.apply)(OrderItem.unapply))
        ))(Order.apply)(Order.unapply)
        verifying("orderCode to short", f => f.orderCode.length() > 5)
  )
  
  val orderItemForm : Form[OrderItem] = Form {  mapping(
          "id" -> ignored(NotAssigned: Pk[Long]),
          "productId" -> optional(of[Long]),
          "quantity" -> optional(of[Long]))(OrderItem.apply)(OrderItem.unapply)
  }

  def create = withUser { user  => implicit request =>
    Ok(html.orders.createForm(orderForm,user))
  }
  
  def createdemo = withUser { user  => implicit request =>
     Ok(html.orders.demoCreateOrder(orderForm,orderItemForm,user))
  }
  
  def orderitem = withUser { user  => implicit request =>
   
    val items = orderForm.get.orderItems.get
    items :+ OrderItem(null,Option[Long](1),Option[Long](1))
    Logger.debug("request =>" + request.toString() + request.body)
     orderItemForm.bindFromRequest.fold(
      formWithErrors => BadRequest,
      orderItem => {
        Logger.debug(orderItem.toString())
        OrderItem.insert(1,orderItem)
        Home
      })
  }
  
  def list(page: Int, orderBy: Int, filter: String) = withUser { user  => implicit request => {

    Ok(html.orders.ordersOverview(
      Order.list(page = page, orderBy = orderBy, filter = ("%" + filter + "%")),orderBy, filter,user))
  	}
  }
  
  def orderDetail(id: Long) = withUser { user  => implicit request =>
    Ok(html.orders.orderDetail(Order.findById(id),user))
  }
  
  def edit(id: Long) = withUser { user  => implicit request =>
      Ok(html.orders.editForm(id,orderForm.fill(Order.findById(id)),user))
  }

  def save = withUser { user  => implicit request =>
    orderForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.orders.createForm(formWithErrors,user)),
      order => {
        Logger.debug(order.orderItems mkString " + ")
        Order.insert(order)
        Home
      })

  }

  def update(id : Long) = withUser { user  => implicit request => orderForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.orders.editForm(id,formWithErrors,user)),
      order => {
        Order.update(id, order)
        Home.flashing("success" -> "Person %s has been updated".format(order.orderCode))
      })
  }
}