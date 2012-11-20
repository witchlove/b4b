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
    get[Pk[Long]]("SalesPerson.id") ~
    get[String]("SalesPerson.name") ~
    get[String]("SalesPerson.firstName") ~
    get[Option[String]]("SalesPerson.mobile") map {
      case id~name~firstName~mobile => SalesPerson(id, name, firstName, mobile)
    }
  }

  def all(): List[SalesPerson] = DB.withConnection {
    implicit c => SQL("select * from salespersons").as(salesPersonParser *)
  }
  
  def insert(salesPerson : SalesPerson) = DB.withConnection {
    implicit con => SQL("insert into salespersonsvalues(name,firstName,mobile values({name},{firstname},{mobile})")
    					.on('name -> salesPerson.name ,
    					    'firstname -> salesPerson,
    					    'mobile -> salesPerson.mobile)
    					.executeUpdate
  }
   
}