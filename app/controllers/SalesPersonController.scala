package controllers

import anorm.NotAssigned
import anorm.Pk
import models.SalesPerson
import play.api.data.Form
import play.api.data.Forms.ignored
import play.api.data.Forms.mapping
import play.api.data.Forms.nonEmptyText
import play.api.data.Forms.optional
import play.api.data.Forms.text
import play.api.mvc.Action
import play.api.mvc.Controller
import views.html

object SalesPersonController extends Controller {
  
  val salespersonForm : Form[SalesPerson] = Form(
  mapping(
      "id" -> ignored(NotAssigned:Pk[Long]),
      "name" -> nonEmptyText,
      "firstName" -> nonEmptyText,
      "mobile" -> optional(text(6,16))
  )
  (SalesPerson.apply)
  (SalesPerson.unapply)
  )
  
  val Home = Redirect(routes.SalesPersonController.list(0, 2, ""))
  
  def create = Action {
    Ok(html.salespersons.createForm(salespersonForm))
  }
  
  def edit(id: Long) = Action {
    SalesPerson.findById(id).map { salesperson =>
      Ok(html.salespersons.editForm(id, salespersonForm.fill(salesperson)))
    }.getOrElse(NotFound)
  }
  
  def update(id: Long) = Action { implicit request =>
    salespersonForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.salespersons.editForm(id, formWithErrors)),
      salesperson => {
        SalesPerson.update(id, salesperson)
        Home.flashing("success" -> "Person %s has been updated".format(salesperson.name))
      }
    )
  }
  
  def list(page: Int, orderBy: Int, filter: String) = Action { implicit request =>
    Ok(html.salespersons.salespersonsOverview(
      SalesPerson.list(page = page, orderBy = orderBy, filter = ("%"+filter+"%")),
      orderBy, filter
    ))
  }
  
  def delete(id: Long) = Action {
    SalesPerson.delete(id)
    Home
  }
  
  def save = Action { implicit request =>
    salespersonForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.salespersons.createForm(formWithErrors)),
      salesPerson => {
        SalesPerson.insert(salesPerson)
        Home
      }
    )
    
  }
  
}