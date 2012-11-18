package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current

case class Order(id : Long, orderDesc : String, salesperson : SalesPerson , items : List[OrderItem]) {
}
 

object Order {
  
  val order = {
		  get[Long]("id") ~ 
		  get[String]("description") ~
		  get[String]("name")~
		  get[String]("firstname") map {
		  case id~description~name~firstname => Order(id,description, SalesPerson(name,firstname),Nil)
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