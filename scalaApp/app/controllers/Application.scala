package controllers

import anorm._
import play.api.db._
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.Play.current
import models.Cat


object Application extends Controller {
	
	def index = Action {
		Ok(views.html.index("Hello app"))
	}
  
	def cats = Action {
		val connection = DB.getConnection()
		
		DB.withConnection { implicit connection =>
		  	val selectCats = SQL("SELECT * FROM cats")
		  	
		  	val cats = SQL("SELECT * FROM cats")().collect {
		  	    case Row(name:String, url:String) => Cat(name, url)
			}
		  	
		  	val selectCats2 = SQL("Select * from cats")
 
			val cats2 = selectCats2().map(row => 
			    row[String]("name") -> row[String]("url")
			).toList
		  	
			Ok(views.html.list.render("title", cats2))
		}
	}
	
	def newCat = Action {
	  
		val catForm = createCatForm()
	  
		Ok(views.html.cat.render("title", catForm))
	}

	def createCat = Action { implicit request => 

		val catForm = createCatForm()
		
		catForm.bindFromRequest.fold(
			formWithErrors => {
				BadRequest(views.html.cat.render("title", formWithErrors))  
			},
			catData => {
			  
				val connection = DB.getConnection()
			  
			    DB.withConnection { implicit connection =>
			  		val id: Option[Long] = SQL("INSERT INTO cats(name, url) VALUES ({name}, {url})").on(
			  		    "name" -> catData.name, 
			  		    "url" -> catData.url).executeInsert()
			  		    
			  		Redirect(routes.Application.cats())
			  	}			 
			}
		)
	}
	
	def createCatForm(): Form[Cat] = {
		val catForm = Form(
			mapping(
		        "name" -> nonEmptyText,
		        "url"  -> nonEmptyText
		    )(Cat.apply)(Cat.unapply)
		)
		
		return catForm
	}
}