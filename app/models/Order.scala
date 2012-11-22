package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current

case class Order( orderDesc : String, salespersonId : Long )

object Order {
  
  val order = {
		  get[Long]("id") ~ 
		  get[String]("description") ~
		  get[Long]("salespersonId") map {
		  case id~description~salespersonId=> Order(description,salespersonId)
		  }
  }

  
  def all(): List[Order] = DB.withConnection {
    implicit c => SQL("select * from orders").as(order *)
  }
  
  def create(description: String) = DB.withConnection {
    implicit con => SQL("insert into orders(description) values ({desc})")
    					.on('desc -> description)
    					.executeUpdate
  }
  def delete(id: Long) =
    DB.withConnection{
      implicit con => SQL("delete from orders where id={orderId}")
    		  		.on('orderId -> id)
    		  		.executeUpdate
    
  }
}