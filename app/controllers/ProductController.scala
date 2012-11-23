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

object ProductController extends Controller {

  val Home = Redirect(routes.ProductController.list(0, 2, ""))

  val productForm: Form[Product] = Form(
    mapping(
      "id" -> ignored(NotAssigned: Pk[Long]),
      "productCode" -> nonEmptyText,
      "productName" -> nonEmptyText,
      "productDescription" -> nonEmptyText,
      "productPrice" -> of[Long])(Product.apply)(Product.unapply))

  def create = Action {
    Ok(html.products.createForm(productForm))
  }

  def edit(id: Long) = Action {
    Product.findById(id).map { product =>
      Ok(html.products.editForm(id, productForm.fill(product)))
    }.getOrElse(NotFound)
  }

  def update(id: Long) = Action { implicit request =>
    productForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.products.editForm(id, formWithErrors)),
      product => {
        Product.update(id, product)
        Home.flashing("success" -> "Person %s has been updated".format(product.productName))
      })
  }

  def list(page: Int, orderBy: Int, filter: String) = Action { implicit request =>
    Ok(html.products.productsOverview(
      Product.list(page = page, orderBy = orderBy, filter = ("%" + filter + "%")),
      orderBy, filter))
  }

  def delete(id: Long) = Action {
    Product.delete(id)
    Home
  }

  def save = Action { implicit request =>
    productForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.products.createForm(formWithErrors)),
      product => {
        Product.insert(product)
        Home
      })

  }

}