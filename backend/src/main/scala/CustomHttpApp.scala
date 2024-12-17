import cats.effect.Async
import domain.database.{AnswerAlg, DiscussionAlg, UserAlg}
import domain.oauth.OAuthAlg
import org.http4s.HttpApp
import org.http4s.server.Router
import org.http4s.server.middleware.Logger

object CustomHttpApp {
  def build[F[_]: Async](
    oauthAlg: OAuthAlg[F],
    answerAlg: AnswerAlg[F],
    discussionAlg: DiscussionAlg[F],
    userAlg: UserAlg[F],
  ): HttpApp[F] = {

    val apiRoutes = Router(
      "/api" -> CustomRoutes.routes(oauthAlg, answerAlg, discussionAlg, userAlg)
    )

    val apiErrorRoutes = CustomErrorHandler.routes(apiRoutes)
    val corsErrorRoutes = CORSmiddlware.routes(apiErrorRoutes)

    Logger.httpApp(logHeaders = true, logBody = true)(corsErrorRoutes.orNotFound)
  }
}
