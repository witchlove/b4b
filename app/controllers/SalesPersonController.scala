package controllers

import models._
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.data.Forms._
import play.api.data.Forms.text
import play.api.data.format.Formats.longFormat
import play.api.mvc.Action
import play.api.mvc.Controller
import views.html

import anorm._

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
  
  def create = Action {
    Ok(html.salespersons.createForm(salespersonForm))
  }
  
  def list = Action {
   Ok(html.salespersons.salespersonsOverview(SalesPerson.all));
  }
  
  def save = Action { implicit request =>
    salespersonForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.salespersons.createForm(formWithErrors)),
      salesPerson => {
        SalesPerson.insert(salesPerson)
        Redirect(routes.SalesPersonController.list)
      }
    )
    
  }
  
}