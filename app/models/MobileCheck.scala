package models

import org.bson.types.ObjectId
import java.util.Date
import utils.MobileFeatures
import com.novus.salat.dao.{ModelCompanion, SalatDAO}
import com.novus.salat.dao._
import se.radley.plugin.salat._
import mongoContext._
import play.api.Play.current


case class MobileCheck(
  id: ObjectId = new ObjectId,
  url: String,
  when: Date,
  features: MobileFeatures
)

object MobileCheck extends ModelCompanion[MobileCheck, ObjectId] {

  val collection = mongoCollection("checks")
  val dao = new SalatDAO[MobileCheck, ObjectId](collection = collection) {}
}
