package models

import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._
import java.math.BigDecimal
import play.api.libs.json.Format
import play.api.libs.json.JsObject
import play.api.libs.json.JsNumber
import play.api.libs.json.JsString
import play.api.libs.json.JsValue

case class Product(
	id: Pk[Long],
	productCode : String,
	productName : String,
	productDescription : String,
	productPrice : Long // TODO BERT convert to double after upgrade to 2.1
)

object Product {
  
  val productParser = {
    get[Pk[Long]]("id") ~
    get[String]("productCode") ~
    get[String]("productName") ~
    get[String]("productDescription") ~
    get[Long]("productPrice") map {
      case id~productCode~productName~productDescription~productPrice => Product(id, productCode,productName, productDescription, productPrice)
    }
  }
  
  implicit object UserFormat extends Format[Product] {
  
  def reads(json: JsValue) = Product(
    Id((json \ "id").as[String].toLong) ,
    (json \ "productCode").as[String],
    (json \ "productName").as[String],
    (json \ "productDescription").as[String],
    (json \ "productPrice").as[Long]
  )
    
  def writes(product: Product) = JsObject(Seq(
    "id" -> JsNumber(product.id.get),
    "productCode" -> JsString(product.productCode),
    "productName" -> JsString(product.productName),
    "productDescription"-> JsString(product.productDescription)
  ))
}
  
  def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = ""): Page[Product] = {
    
    val offest = pageSize * page
    
    DB.withConnection { implicit connection =>
      
      val products = SQL(
        """
          select * from products 
          where products.productName like {filter}
          order by {orderBy} nulls last
          limit {pageSize} offset {offset}
        """
      ).on(
        'pageSize -> pageSize, 
        'offset -> offest,
        'filter -> filter,
        'orderBy -> orderBy
      ).as(Product.productParser *)

      val totalRows = SQL(
        """
          select count(*) from products 
         where products.productName like {filter}
        """
      ).on(
        'filter -> filter
      ).as(scalar[Long].single)

      Page(products, page, offest, totalRows)
      
    }
    }
  
  def findById(id : Long) : Option[Product] = {
    DB.withConnection { implicit connection =>
      SQL("select * from products where id = {id}").on('id -> id).as(productParser.singleOpt)
    }
  }
  
  def findByProductCode(code : String) : Option[Product] = {
    DB.withConnection { implicit connection =>
      SQL("select * from products where productCode = {code}").on('code -> code).as(productParser.singleOpt)
    }
  }
  
  def findProductlist() : Set[String] = {
    val ret : Set[String] = Set.apply("test1","test2","test3")
    ret
  }
  
  def verifyProductCode(productCode : String) : Boolean = {
	findByProductCode(productCode).isDefined
  }
  
  def update(id: Long, product : Product) = {
	  DB.withConnection { implicit connection =>
      SQL(
        """
          update products
          set productCode = {productCode},
          productName = {productName},
          productDescription = {productDescription},
          productPrice  = {productPrice}
          where id = {id}
        """
      ).on(
        'id -> id,
        'productCode -> product.productCode,
        'productName -> product.productName,
        'productDescription -> product.productDescription,
        'productPrice -> product.productPrice
      ).executeUpdate()
    }
  }
  
  def delete(id: Long) = {
    DB.withConnection { implicit connection =>
      SQL("delete from products where id = {id}").on('id -> id).executeUpdate()
    }
  }
  
  def all(): List[Product] = DB.withConnection {
    implicit c => SQL("select * from products").as(productParser *)
  }
  
  def allForSelect : Seq[(String,String)] = {
    val products = all
    products map (input => (input.id.get.toString,input.productCode + "-" +  input.productName)) toSeq
  }
  
  def insert(product : Product) = {
    DB.withConnection { implicit con => 
     SQL(
        """
          insert into products 
    	  (productCode,productName,productDescription,productPrice) 
    	  values
          (
    		 {productCode},{productName},{productDescription},{productPrice}
          )
         """
      ).on(
        'productCode -> product.productCode,
        'productName -> product.productName,
        'productDescription -> product.productDescription,
        'productPrice -> product.productPrice
      ).executeUpdate()
    }
  }
  
  
}