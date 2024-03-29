package controllers

import anorm.NotAssigned
import anorm.Pk
import models.Product
import play.api.data.Form
import play.api.data.Forms.ignored
import play.api.data.Forms.mapping
import play.api.data.Forms.nonEmptyText
import play.api.data.Forms.of
import play.api.data.format.Formats.longFormat
import play.api.mvc.Action
import play.api.mvc.Controller
import views.html
import play.api.libs.json._

object ProductController extends Controller with Secured {

  val Home = Redirect(routes.ProductController.list(0, 2, ""))

  val productForm: Form[Product] = Form(
    mapping(
      "id" -> ignored(NotAssigned: Pk[Long]),
      "productCode" -> nonEmptyText,
      "productName" -> nonEmptyText,
      "productDescription" -> nonEmptyText,
      "productPrice" -> of[Long])(Product.apply)(Product.unapply))

  def create = withUser { user =>
    implicit request =>
      Ok(html.products.createForm(productForm, user))
  }

  def edit(id: Long) = withUser { user =>
    implicit request =>
      Product.findById(id).map { product =>
        Ok(html.products.editForm(id, productForm.fill(product), user))
      }.getOrElse(NotFound)
  }

  def update(id: Long) = withUser { user =>
    implicit request =>
      productForm.bindFromRequest.fold(
        formWithErrors => BadRequest(html.products.editForm(id, formWithErrors, user)),
        product => {
          Product.update(id, product)
          Home.flashing("success" -> "Person %s has been updated".format(product.productName))
        })
  }

  def list(page: Int, orderBy: Int, filter: String) = withUser { user =>
    implicit request =>
      Ok(html.products.productsOverview(
        Product.list(page = page, orderBy = orderBy, filter = ("%" + filter + "%")),
        orderBy, filter, user))
  }

  def delete(id: Long) = Action {
    Product.delete(id)
    Home
  }

  def save = withUser { user =>
    implicit request =>
      productForm.bindFromRequest.fold(
        formWithErrors => BadRequest(html.products.createForm(formWithErrors, user)),
        product => {
          Product.insert(product)
          Home
        })

  }
  
  def productlistForId(id: String) = Action{ 
	  val productList = Product.all()
	  Json.toJson(productList.head)
	  Ok(Json.toJson(productList))
	  //Ok(Json.toJson(Map("status" -> "OK", "message" -> ("Hello " + id))))
  }
}

