package models

import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._

case class SalesPerson(name :String , firstName : String)


object SalesPerson {
  
  val salesPersonParser = {
     get[String]("name")~
     get[String]("firstname") map {
       case name~firstname => SalesPerson(name,firstname)
     }
  }
  
  def all(): List[SalesPerson] = DB.withConnection {
    implicit c => SQL("select * from salespersons").as(salesPersonParser *)
  }
  
  def create(name : String, firstname : String) = DB.withConnection {
    implicit con => SQL("insert into salespersons(name,firstname) values ({name,firstname})")
    					.on('name -> name ,
    					    'firstname -> firstname)
    					.executeUpdate
  }
  
}