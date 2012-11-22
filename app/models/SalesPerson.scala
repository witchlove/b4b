package models

import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._

case class SalesPerson(
    id: Pk[Long],
    name :String,
    firstName : String,
    mobile : Option[String])

object SalesPerson {
  
  val salesPersonParser = {
    get[Pk[Long]]("SalesPersons.id") ~
    get[String]("SalesPersons.name") ~
    get[String]("SalesPersons.firstName") ~
    get[Option[String]]("SalesPersons.mobile") map {
      case id~name~firstName~mobile => SalesPerson(id, name, firstName, mobile)
    }
  }
  
  def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = ""): Page[SalesPerson] = {
    
    val offest = pageSize * page
    
    DB.withConnection { implicit connection =>
      
      val salespersons = SQL(
        """
          select * from salespersons 
          where salespersons.name like {filter}
          order by {orderBy} nulls last
          limit {pageSize} offset {offset}
        """
      ).on(
        'pageSize -> pageSize, 
        'offset -> offest,
        'filter -> filter,
        'orderBy -> orderBy
      ).as(SalesPerson.salesPersonParser *)

      val totalRows = SQL(
        """
          select count(*) from salespersons 
         where salespersons.name like {filter}
        """
      ).on(
        'filter -> filter
      ).as(scalar[Long].single)

      Page(salespersons, page, offest, totalRows)
      
    }
    
  }
  
  def all(): List[SalesPerson] = DB.withConnection {
    implicit c => SQL("select * from salespersons").as(salesPersonParser *)
  }
  
  def findById(id : Long) : Option[SalesPerson] = {
    DB.withConnection { implicit connection =>
      SQL("select * from salespersons where id = {id}").on('id -> id).as(salesPersonParser.singleOpt)
    }
  }
  
  def update(id: Long, salesPeron : SalesPerson) = {
	  DB.withConnection { implicit connection =>
      SQL(
        """
          update salespersons
          set name = {name}, firstName = {firstName}, mobile = {mobile}
          where id = {id}
        """
      ).on(
        'id -> id,
        'name -> salesPeron.name,
        'firstName -> salesPeron.firstName,
        'mobile -> salesPeron.mobile
      ).executeUpdate()
    }
  }
  
  def delete(id: Long) = {
    DB.withConnection { implicit connection =>
      SQL("delete from salespersons where id = {id}").on('id -> id).executeUpdate()
    }
  }
  
  def insert(salesPerson : SalesPerson) = DB.withConnection {
    implicit con => SQL("insert into salespersons (name,firstName,mobile) values({name},{firstname},{mobile})")
    					.on('name -> salesPerson.name ,
    					    'firstname -> salesPerson.firstName,
    					    'mobile -> salesPerson.mobile)
    					.executeUpdate
  }
   
}