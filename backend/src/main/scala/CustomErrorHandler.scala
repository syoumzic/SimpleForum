import cats.data.{Kleisli, OptionT}
import cats.effect.Sync
import cats.implicits._
import domain.database.{DiscussionNotFound, UserExist, UserNotFound}
import domain.model.InvalidDiscussionId
import domain.oauth.IllegalToken
import org.http4s._
import org.http4s.dsl.Http4sDsl

object CustomErrorHandler {
  def routes[F[_] : Sync](service: HttpRoutes[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._

    Kleisli { req =>
      service.run(req).handleErrorWith { error =>
        OptionT.liftF(error match {
            case InvalidDiscussionId => BadRequest("Discussion id is not parsable")
            case IllegalToken => BadRequest("token is invalid")
            case UserNotFound => BadRequest("user not found")
            case UserExist => Ok("user already exist")
            case DiscussionNotFound => BadRequest("discussion not found")
            case e: Throwable =>
              println(e)
              InternalServerError(s"Internal server error")
          })
      }
    }
  }
}