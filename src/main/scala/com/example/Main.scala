// akka
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors

// akka http
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.*
import akka.http.scasladsl.server.Directives.*

@main def httpserver: Unit = 
    implicit val actorSystem = ActorSystem(Behaviors.empty, "akka-http")

    val route = get {
        path("hello") {
            complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, "Hellow akka http world!"))
        }
    }

    Http().newServerAt("127.0.0.1", 8080).bind(route)