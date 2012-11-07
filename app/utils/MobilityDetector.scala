package utils

import xml.{NodeSeq, Text, Node}
import play.api.Logger
import java.net.URL


case class MobileFeatures(
  redirect: Boolean,
  viewport: Boolean,
  mediaQueries: Boolean,
  broken: Boolean,
  scriptCount: Int,
  cssCount: Int
)


object MobilityDetector {

  def detect(url: String):MobileFeatures = {
    Logger.info("Started crawling: "+url)
    try {
      val desktop = PageReader.read(url, false)
      val mobile = PageReader.read(url, true)

      // Wait for both mobile & desktop to finish
      val features = for {
        d <- desktop
        m <- mobile
      } yield MobileFeatures(
          d.get.location != m.get.location,
          isViewportPresent(m.get.content),
          checkForMediaQueries(listStylesheets(m.get.content), new URL(m.get.location)),
          false,
          listStylesheets(m.get.content).length,
          countScripts(m.get.content)
        )

      features()
    } catch {
      case e => {
        MobileFeatures(false, false, false, true, 0, 0)
      }
    }

  }

  def isViewportPresent(root: Node) = {
    val viewport = root \\ "meta" filter {_ \\ "@name" exists(_.text == "viewport")}
    viewport.length > 0
  }


  def checkForMediaQueries(stylesheets: NodeSeq, host: URL) = {
    try {
      val promises = stylesheets.map((css: NodeSeq) => PageReader.readResource(absoluteUrl((css \\ "@href").text, host)))

      // How to do this functional and without blocking?
      var found = false
      for (promise <- promises) {
        val cssString = promise()
        if (cssString.contains("@media")) found = true
      }
      found
    } catch {
      case e => {
        false
      }
    }
  }

  /**
   * Urls can come in the form "/style.css", "styles.css" and "http://www.domain.se/style.se"
   *
   * @param urlString The url to the css file
   * @param root      The host
   * @return          The absolute path, ex: "http://www.domain.se/style.se"
   */
  def absoluteUrl(urlString: String, root: java.net.URL) = {
    if (!urlString.startsWith("http://")) {
      val portStr = if (root.getPort < 0) "" else ":" + root.getPort
      root.getProtocol + "://" + root.getHost + portStr + (if (!urlString.startsWith("/")) "/" else "") + urlString
    }
    else
      urlString
  }

  def countScripts(root: Node) = {
    val scripts = root \\ "script"
    scripts.length
  }

  def listStylesheets(root: Node) = {
    val stylesheets = root \\ "link" filter {_ \\ "@rel" exists(_.text == "stylesheet")}
    stylesheets
  }
}
