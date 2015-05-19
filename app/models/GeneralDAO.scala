package models

//import reactivemongo.api._
//import scala.concurrent.ExecutionContext.Implicits.global
//
///**
// * Created by d_rc on 18/05/15.
// */
//trait GeneralDAO {
//
//  val mongoTarget = "localhost"
//  val driver = new MongoDriver
//  implicit val context = play.api.libs.concurrent.Execution.Implicits.defaultContext
//
//  def connect(col: String) = {
//    val connection = driver.connection(List(mongoTarget))
//    val db = connection("scaloud")
//    db(col)
//  }
//}
//