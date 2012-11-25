package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.api.Logger

case class Order(
  id: Pk[Long],
  orderCode: String,
  salesPersonId: Long,
  orderItems: Option[Seq[OrderItem]])

case class OrderItem(
  id: Pk[Long],
  productId: Long,
  quantity: Long)

object Order {

  val orderParser = {
    get[Pk[Long]]("id") ~
      get[String]("orderCode") ~
      get[Long]("salespersonId") map {
        case id ~ orderCode ~ salespersonId => Order(id, orderCode, salespersonId, None)
      }
  }

  val withOrderItems = Order.orderParser ~ (OrderItem.orderItemParser ?) map {
    case order ~ orderItem => (order, orderItem)
  }

  def insert(order: Order) = {
    DB.withConnection { implicit con =>
      SQL(
        """
          insert into orders 
    	  (orderCode,salesPersonId) 
    	  values
          (
    		 {orderCode},{salesPersonId}
          )
         """).on(
          'orderCode -> order.orderCode,
          'salesPersonId -> order.salesPersonId).executeUpdate()
    }
    order.orderItems.get map (input => {
      if (!input.equals(None)) {
        OrderItem.insert(1, input)
      }
    })

  }

//  def findById(id: Long): (Order, List[OrderItem]) = {
//    DB.withConnection { implicit connection =>
//      {
//        val result = SQL("select * from orders left join orderitems on orders.id = orderid  where orders.id = {id}").on('id -> id).as(withOrderItems *)
//      }
//    }
//  }

  def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = ""): Page[Order] = {

    val offest = pageSize * page

    DB.withConnection { implicit connection =>

      val orders = SQL(
        """
          select * from orders 
          where orders.orderCode like {filter}
          order by {orderBy} nulls last
          limit {pageSize} offset {offset}
        """).on(
          'pageSize -> pageSize,
          'offset -> offest,
          'filter -> filter,
          'orderBy -> orderBy).as(Order.orderParser *)

      val totalRows = SQL(
        """
          select count(*) from products 
         where products.productName like {filter}
        """).on(
          'filter -> filter).as(scalar[Long].single)

      Page(orders, page, offest, totalRows)

    }
  }
}

object OrderItem {

  val orderItemParser = {
    get[Pk[Long]]("id") ~
      get[Long]("productId") ~
      get[Long]("quantity") map {
        case id ~ productId ~ quantity => OrderItem(id, productId, quantity)
      }
  }

  def insert(orderId: Long, orderItem: OrderItem) = {
    DB.withConnection { implicit con =>
      SQL(
        """
          insert into orderItems
    	  (orderId,productId,quantity) 
    	  values
          (
    		 {orderId},{productId},{quantity}
          )
         """).on(
          'orderId -> orderId,
          'productId -> orderItem.productId,
          'quantity -> orderItem.quantity).executeUpdate()
    }

  }

}