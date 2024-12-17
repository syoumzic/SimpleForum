package yandexoauth

import cats.effect.kernel.Async
import cats.implicits.{catsSyntaxMonadError, toFunctorOps}
import domain.model.User
import domain.oauth.OAuthAlg
import io.circe.Decoder
import io.circe.generic.auto._
import io.circe.generic.semiauto.deriveDecoder
import org.http4s.EntityDecoder
import org.http4s.circe.jsonOf
import org.http4s.client.Client
import org.http4s.dsl.Http4sDsl
import domain.oauth.IllegalToken
object OAuth {
  case class OAuthResponse(
    id: String,
    login: String,
    client_id: String,
    display_name: String,
    real_name: String,
    first_name: String,
    last_name: String,
    sex: String,
    psuid: String,
  )

  implicit val oauthResponseDecoder: Decoder[OAuthResponse] = deriveDecoder[OAuthResponse]
  implicit def OAuthResponseEntityDecoder[F[_]: Async]: EntityDecoder[F, OAuthResponse] = jsonOf[F, OAuthResponse]

  def build[F[_]: Async](client: Client[F]): OAuthAlg[F] = new OAuthAlg[F] {
    def getUser(token: User.token): F[User] = {
      val dsl = new Http4sDsl[F] {}

      val request = s"https://login.yandex.ru/info?&oauth_token=${token.value}"

      client.expect[OAuthResponse](request).map { response =>
        User(User.login(response.login))
      }.adaptError(e => IllegalToken)
    }
  }
}
