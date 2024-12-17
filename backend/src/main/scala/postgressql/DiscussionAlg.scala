package postgressql

import cats.effect.kernel.{Async, Sync}
import cats.implicits.{catsSyntaxApplicativeError, toFlatMapOps, toFunctorOps}
import domain.database.{DiscussionAlg, DiscussionNotFound}
import domain.model.{Discussion, Field, IndexDiscussion, User}
import doobie._
import doobie.implicits._

object DiscussionAlg {
  def build[F[_] : Async](transactor: Transactor[F]): F[DiscussionAlg[F]] =
    for {
      _ <- createTable(transactor).attempt.flatMap {
        case Right(_) => Sync[F].pure(println("success create discussion table"))
        case Left(err) => Sync[F].pure(println(s"failure to create discussion table: error $err"))
      }
      answerAlg = impl(transactor)
    } yield answerAlg

    def impl[F[_]: Async](transactor: Transactor[F]): DiscussionAlg[F] = new DiscussionAlg[F] {
      override def getWholeDiscussionsWithIndex: F[List[IndexDiscussion]] =
        sql"""
          SELECT D.id, D.title, D.description, U.login
          FROM Discussions D
          JOIN Users U ON D.author_id = U.id
       """
          .query[(Int, String, String, String)]
          .to[List]
          .transact(transactor)
          .map(list => list.map {
            case (id, title, description, login) =>
              IndexDiscussion(
                Discussion.id(id),
                Field.title(title),
                Field.description(description),
                User.login(login)
              )
          })

      override def getDiscussion(id: Discussion.id): F[Discussion] =
        sql"""
          SELECT D.title, D.description, U.login
          FROM Discussions D
          JOIN Users U ON D.author_id = U.id
          WHERE D.id = ${id.value}
        """
          .query[(String, String, String)]
          .option // Используем option для обработки случая, когда обсуждение не найдено
          .transact(transactor)
          .flatMap {
            case Some((title, volume, authorName)) =>
              Async[F].pure(
                Discussion(
                  Field.title(title),
                  Field.description(volume),
                  User.login(authorName)
                )
              )
            case None =>
              Async[F].raiseError(DiscussionNotFound)
          }

      override def getWholeDiscussionsWithIndex(token: User.token): F[List[IndexDiscussion]] =
        sql"""
          WITH user_id AS (
            SELECT id, login
            FROM Users
            WHERE token = ${token.value}
          )
          SELECT D.id, D.title, D.description, U.login
          FROM Discussions D
          JOIN user_id U ON D.author_id = U.id;
        """
          .query[(Int, String, String, String)]
          .to[List]
          .transact(transactor)
          .map(_.map { case (rawDiscussionId, rawDiscussionTitle, rawDiscussionVolume, rawUserLogin) =>
            IndexDiscussion(Discussion.id(rawDiscussionId),
              Field.title(rawDiscussionTitle),
              Field.description(rawDiscussionVolume),
              User.login(rawUserLogin)
            )
          })

      override def putDiscussion(token: User.token, discussion: Discussion): F[Unit] =
        sql"""
         WITH user_id AS (
          SELECT id
          FROM Users
          WHERE token = ${token.value}
        )
        INSERT INTO Discussions (author_id, title, description)
        SELECT id, ${discussion.title}, ${discussion.description}
        FROM user_id
       """
          .update
          .run
          .transact(transactor)
          .void
  }

  def createTable[F[_]: Sync](transactor: Transactor[F]): F[Unit] = {
    sql"""
      CREATE TABLE IF NOT EXISTS Discussions (
        id SERIAL PRIMARY KEY,
        author_id INTEGER NOT NULL,
        title TEXT NOT NULL,
        description TEXT NOT NULL,
        FOREIGN KEY (author_id) REFERENCES Users(id)
    );
    """
      .update
      .run
      .transact(transactor)
      .void
  }
}
