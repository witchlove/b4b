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
  orderStatus : String,
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
      get[String]("orderStatus") ~
      get[Option[String]]("orderRemarks") ~
      get[Long]("salespersonId") map {
        case id ~ orderCode ~ orderDate ~orderStatus ~ orderRemarks ~ salespersonId => Order(id, orderCode, salespersonId, orderDate,orderStatus, orderRemarks, None)
      }
  }

  val withOrderItems = Order.orderParser ~ (OrderItem.orderItemParser ?) map {
    case order ~ orderItem => (order, orderItem)
  }
  
  val withOrderItemsAndProducts = Order.orderParser ~ (OrderItem.orderItemParser ?) ~ (Product.productParser ?) map {
    case order ~ orderItem ~ product => (order,orderItem, product)
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

    DB.withTransaction  { implicit con =>
      SQL(
        """
          insert into orders 
    	  (id,orderCode,orderDate,orderStatus,orderRemarks,salesPersonId) 
    	  values
          (
    		 {id},{orderCode},{orderDate},{orderStatus},{orderRemarks},{salesPersonId}
          )
         """).on(
          'id -> orderId,
          'orderCode -> order.orderCode,
          'orderDate -> order.orderDate,
          'orderStatus -> order.orderStatus,
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
          salesPersonId = {salesPersonId},
          orderDate = {orderDate},
          orderStatus = {orderStatus},
          orderRemarks = {orderRemarks}
          where id = {id}
        """).on(
          'id -> id,
          'orderCode -> order.orderCode,
          'salesPersonId -> order.salesPersonId,
          'orderDate -> order.orderDate,
          'orderStatus -> order.orderStatus,
          'orderRemarks -> order.orderRemarks).executeUpdate()
    }
    
    OrderItem.deleteOrderItemsForOrder(id)
    
    if (!(order.orderItems equals (None))) {
      val orderItemsToSave = order.orderItems.get.filter(input => input.productId.isDefined)

      orderItemsToSave map (input => {
        if (!input.equals(None)) {
          OrderItem.insert(id, input)
        }
      })
    }
  }

  def findById(id: Long): Order = {
    DB.withConnection { implicit connection =>
      {
        val orderData: Order = SQL("select * from orders where orders.id = {id}").on('id -> id).as(orderParser.single)
        val orderItemData: Seq[OrderItem] = SQL("select * from orderitems where orderId ={id}").on('id -> id).as(OrderItem.orderItemParser *)
        
        
        
        Order(orderData.id, orderData.orderCode, orderData.salesPersonId, orderData.orderDate,orderData.orderStatus,orderData.orderRemarks, Option(orderItemData))
      }
    }
  }
  
  
  
  def findFullDetails(id : Long) : Any = {
    DB.withConnection { implicit connection =>
      {
        val orderData = SQL("""select *  
        								from orders , 
        								orderitems , 
        								products inner join orderitems t1 on orders.id = t1.orderid 
        								inner join orderitems t2 on t2.PRODUCTID  = products.id 
        								where orders.id = {id}""").on('id -> id).as(Order.withOrderItemsAndProducts *)
       
        								
        Logger.debug(orderData.groupBy(_._1).groupBy(_._2).toString)
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
  
  def deleteOrderItemsForOrder(orderId : Long)  = {
    DB.withConnection { implicit con =>
      SQL(
        """
          delete from orderItems
    	  where orderItems.orderId = {orderId}
         """).on(
          'orderId -> orderId).executeUpdate()
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

object OrderStatus extends Enumeration {
  type OrderStatus = Value
  val New,Validated,Deliverd = Value
  
  def orderStates() : Seq[(String,String)] = {
		 Seq [(String,String)](("new","New"))
  }
  
  def orderStatesUpdate() : Seq[(String,String)] = {
		 Seq [(String,String)](("new","New"),("saved","Saved"),("ordered","Ordered"),("Delivered","Delivered"))
  }
  
}