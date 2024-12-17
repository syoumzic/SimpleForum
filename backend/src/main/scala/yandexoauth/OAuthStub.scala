package yandexoauth

import cats.effect.kernel.Async
import domain.model.User
import domain.oauth.OAuthAlg
import org.http4s.client.Client

object OAuthStub {
  def build[F[_]: Async](client: Client[F]): OAuthAlg[F] = new OAuthAlg[F] {
    def getUser(token: User.token): F[User] = {
      Async[F].pure(User(User.login("stub_login")))
    }
  }
}
