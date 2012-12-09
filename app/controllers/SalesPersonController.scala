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
import models.User

object SalesPersonController extends Controller with Secured {
  
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
  
  def create = withUser { user  => implicit request =>
    Ok(html.salespersons.createForm(salespersonForm,user))
  }
  
  def edit(id: Long) = withUser { user  => implicit request =>
    SalesPerson.findById(id).map { salesperson =>
      Ok(html.salespersons.editForm(id, salespersonForm.fill(salesperson),user))
    }.getOrElse(NotFound)
  }
  
  def update(id: Long) = withUser { user  => implicit request =>
    salespersonForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.salespersons.editForm(id, formWithErrors,user)),
      salesperson => {
        SalesPerson.update(id, salesperson)
        Home.flashing("success" -> "Person %s has been updated".format(salesperson.name))
      }
    )
  }
  
  def list(page: Int, orderBy: Int, filter: String) = withUser { user  => implicit request =>
    Ok(html.salespersons.salespersonsOverview(
      SalesPerson.list(page = page, orderBy = orderBy, filter = ("%"+filter+"%")),
      orderBy, filter,user))
  }
  
  def delete(id: Long) = Action {
    SalesPerson.delete(id)
    Home
  }
  
  def save = withUser { user  => implicit request =>
    salespersonForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.salespersons.createForm(formWithErrors,user)),
      salesPerson => {
        SalesPerson.insert(salesPerson)
        Home
      }
    )
    
  }
  
}