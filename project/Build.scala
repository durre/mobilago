import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "mobilago"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      "net.databinder.dispatch" %% "core" % "0.9.1",
      "org.ccil.cowan.tagsoup" % "tagsoup" % "1.2.1",
      "se.radley" %% "play-plugins-salat" % "1.1"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      routesImport += "se.radley.plugin.salat.Binders._",
      templatesImport += "org.bson.types.ObjectId"
    )

}
