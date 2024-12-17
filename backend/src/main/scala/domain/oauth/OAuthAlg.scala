package domain.oauth

import domain.model.User

trait OAuthAlg[F[_]] {
  def getUser(token: User.token): F[User]
}

case object IllegalToken extends Exception
