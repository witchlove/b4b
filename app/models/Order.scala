package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.api.Logger
import java.util.Date

case class Order(
  id: Pk[Long],
  orderCode: String,
  salesPersonId: Long,
  orderDate: Date,
  orderRemarks: Option[String],
  orderItems: Option[Seq[OrderItem]])

case class OrderItem(
  id: Pk[Long],
  productId: Option[Long],
  quantity: Option[Long])

object Order {

  val orderParser = {
    get[Pk[Long]]("id") ~
      get[String]("orderCode") ~
      get[Date]("orderDate") ~
      get[Option[String]]("orderRemarks") ~
      get[Long]("salespersonId") map {
        case id ~ orderCode ~ orderDate ~ orderRemarks ~ salespersonId => Order(id, orderCode, salespersonId, orderDate, orderRemarks, None)
      }
  }

  val withOrderItems = Order.orderParser ~ (OrderItem.orderItemParser ?) map {
    case order ~ orderItem => (order, orderItem)
  }

  private def nextId(): Long = {
    DB.withConnection { implicit con =>
      SQL(
        """
          select ORDER_ID_SEQ.nextval
        """).as(scalar[Long].single)
    }
  }

  def insert(order: Order) = {

    val orderId = Order.nextId

    DB.withConnection { implicit con =>
      SQL(
        """
          insert into orders 
    	  (id,orderCode,orderDate,orderRemarks,salesPersonId) 
    	  values
          (
    		 {id},{orderCode},{orderDate},{orderRemarks},{salesPersonId}
          )
         """).on(
          'id -> orderId,
          'orderCode -> order.orderCode,
          'orderDate -> order.orderDate,
          'orderRemarks -> order.orderRemarks,
          'salesPersonId -> order.salesPersonId).executeUpdate()
    }
    if (!(order.orderItems equals (None))) {
      val orderItemsToSave = order.orderItems.get.filter(input => input.productId.isDefined)

      orderItemsToSave map (input => {
        if (!input.equals(None)) {
          OrderItem.insert(orderId, input)
        }
      })
    }
  }

  def update(id: Long, order: Order) = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          update orders
          set orderCode = {orderCode},
          salesPersonId = {salesPersonId}
          where id = {id}
        """).on(
          'id -> id,
          'orderCode -> order.orderCode,
          'salesPersonId -> order.salesPersonId).executeUpdate()
    }
  }

  def findById(id: Long): Order = {
    DB.withConnection { implicit connection =>
      {
        val orderData: Order = SQL("select * from orders where orders.id = {id}").on('id -> id).as(orderParser.single)
        val orderItemData: Seq[OrderItem] = SQL("select * from orderitems where orderId ={id}").on('id -> id).as(OrderItem.orderItemParser *)

        Order(orderData.id, orderData.orderCode, orderData.salesPersonId, orderData.orderDate, orderData.orderRemarks, Option(orderItemData))
      }
    }
  }

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
      get[Option[Long]]("productId") ~
      get[Option[Long]]("quantity") map {
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