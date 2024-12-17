package postgressql

import cats.effect.kernel.{Async, Sync}
import cats.implicits.{catsSyntaxApplicativeError, toFlatMapOps, toFunctorOps}
import domain.database.{UserAlg, UserExist, UserNotFound}
import domain.model.User
import doobie._
import doobie.implicits._

object UserAlg {
  def build[F[_] : Async](transactor: Transactor[F]): F[UserAlg[F]] =
    for {
      _ <- createTable(transactor).attempt.flatMap {
        case Right(_) => Sync[F].pure(println("success create answer table"))
        case Left(err) => Sync[F].pure(println(s"failure to create discussion table: error $err"))
      }
      answerAlg = impl(transactor)
    } yield answerAlg

  def impl[F[_]: Async](transactor: Transactor[F]): UserAlg[F] = new UserAlg[F]{
    override def getUser(token: User.token): F[User] =
      sql"""
        SELECT login
        FROM Users
        WHERE token = ${token.value}
      """
        .query[String]
        .option
        .transact(transactor)
        .flatMap {
          case Some(login) =>
            Sync[F].pure(User(User.login(login)))
          case None =>
            Sync[F].raiseError(UserNotFound)
        }

    override def putUser(token: User.token, user: User): F[Unit] =
      sql"""
        INSERT INTO Users (login, token) VALUES
        (${user.login.value}, ${token.value})
       """
        .update
        .run
        .transact(transactor)
        .void

    override def assertNoExist(token: User.token): F[Unit] = {
      sql"""
           SELECT 1
           FROM Users
           WHERE token = ${token.value}
      """
        .query[Int]
        .option
        .transact(transactor)
        .flatMap {
          case Some(_) => Sync[F].raiseError(UserExist)
          case None => Sync[F].unit
        }
    }

    override def assertExist(token: User.token): F[Unit] = {
      sql"""
         SELECT 1
         FROM Users
         WHERE token = ${token.value}
      """
        .query[Int]
        .option
        .transact(transactor)
        .flatMap {
          case Some(_) => Sync[F].unit
          case None => Sync[F].raiseError(UserNotFound)
        }
    }
  }

  def createTable[F[_]: Sync](transactor: Transactor[F]): F[Unit] = {
    sql"""
      CREATE TABLE IF NOT EXISTS Users (
        id SERIAL PRIMARY KEY,
        login TEXT NOT NULL UNIQUE,
        token TEXT NOT NULL UNIQUE
      );
    """
      .update
      .run
      .transact(transactor)
      .void
  }
}