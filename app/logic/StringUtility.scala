package logic

import java.util.Base64
import scala.annotation.tailrec

/**
 * utility class for handling strings
 *
 * @author Jakub Zitny <zitnyjak@fit.cvut.cz>
 * @since Mon May 18 05:15:21 CEST 2015
 */
object StringUtility {

  val RandomStringDefaultLength = 6
  val AppNamePrefix = "scaloud_app"

  /**
   * @see http://goo.gl/fTTCyp
   * @param length duh
   * @return random string of given length (base64'd)
   */
  def randomString(length: Int = RandomStringDefaultLength): String = {
    @tailrec
    def randomStringTailRecursive(n: Int, list: List[Char]):List[Char] = {
      if (n == 1) util.Random.nextPrintableChar :: list
      else randomStringTailRecursive(n-1, util.Random.nextPrintableChar :: list)
    }
    Base64.getEncoder.encodeToString(randomStringTailRecursive(length, Nil).map(_.toByte).toArray)
  }

  /**
   * generates app name
   * lowecased random string with prefix
   * @return app name string
   */
  def generateAppName(): String = {
    AppNamePrefix + randomString().toLowerCase
  }

}
