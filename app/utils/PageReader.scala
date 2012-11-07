package utils

import dispatch._
import xml.Node
import play.api.Logger
import java.util.Calendar
import java.io.InputStream
import java.net.{URL, IDN}

case class ContentAndLocation(content: Node, location: String)

object PageReader {

  val MOBILE_USER_AGENT = "Mozilla/5.0 (iPhone; U; CPU like Mac OS X; en) AppleWebKit/420+ (KHTML, like Gecko) Version/3.0 Mobile/1A543a Safari/419.3"
  val DESKTOP_USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/22.0.1207.1 Safari/537.1"

  def read(urlStr: String, asMobile: Boolean):Promise[Option[ContentAndLocation]] = {

    val request = url(urlStr).setFollowRedirects(true).addHeader("user-agent", if (asMobile) MOBILE_USER_AGENT else DESKTOP_USER_AGENT)
    val response = Http(request)

    val res = for { r <- response } yield (
      Some(ContentAndLocation(
        washHtml(r.getResponseBodyAsStream),
        r.getUri.toString)
      ))
    res
  }

  /**
   * Reads resources like .css
   *
   * @param urlStr  The resource to read
   * @return        The string with the resource
   */
  def readResource(urlStr: String) = {
    Http(url(urlStr) OK as.String)
  }

  /**
   * Dispatch should handle idn domains for us but this feature does not yet exist
   *
   * @param urlString The full url
   * @return          IDN encode the host
   */
  def idnSafe(urlString: String) = {
    val url = new URL(urlString)
    val host = IDN.toASCII(url.getHost)
    val path = if(url.getPath == null) "/" else url.getPath
    val query = if(url.getQuery == null) "" else url.getQuery
    url.getProtocol + "://" + host + path + query
  }


  def washHtml(stream: InputStream) = {
    val parserFactory = new org.ccil.cowan.tagsoup.jaxp.SAXFactoryImpl
    val parser = parserFactory.newSAXParser()
    val adapter = new scala.xml.parsing.NoBindingFactoryAdapter
    adapter.loadXML(new org.xml.sax.InputSource(stream), parser)
  }
}