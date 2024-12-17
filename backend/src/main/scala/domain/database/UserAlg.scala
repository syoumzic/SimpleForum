package domain.database

import domain.model.User

trait UserAlg[F[_]] {
  def getUser(token: User.token): F[User]
  def putUser(token: User.token, user: User): F[Unit]
  def assertNoExist(token: User.token): F[Unit]
  def assertExist(token: User.token): F[Unit]
}

case object UserNotFound extends Exception
case object UserExist extends Exception

