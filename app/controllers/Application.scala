package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import views.html
import utils.MobilityDetector
import com.codahale.jerkson.Json
import models.MobileCheck
import org.bson.types.ObjectId
import java.util.Date

case class UrlWrapper(url: String)

object Application extends Controller {
  
  def index = Action {
    Ok(views.html.index())
  }

  def checkMobility = Action(parse.json) { implicit request =>
    val url = (request.body \ "url").as[String]

    val fixedUrl = if(!url.startsWith("http")) "http://" + url else url

    val features = MobilityDetector.detect(fixedUrl)
    MobileCheck.save(MobileCheck(new ObjectId, fixedUrl, new Date, features))

    Ok(Json.generate(features))
  }
  
}