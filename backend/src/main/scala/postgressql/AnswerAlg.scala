package postgressql

import cats.effect.kernel.{Async, Sync}
import cats.implicits.{catsSyntaxApplicativeError, toFlatMapOps, toFunctorOps}
import domain.database.AnswerAlg
import domain.model.{Answer, Discussion, Field, User}
import doobie._
import doobie.implicits._

object AnswerAlg {
  def build[F[_]: Async](transactor: Transactor[F]): F[AnswerAlg[F]] =
    for {
      _ <- createTable(transactor).attempt.flatMap {
        case Right(_) => Sync[F].pure(println("success create answer table"))
        case Left(err) => Sync[F].pure(println(s"failure to create answer table: error $err"))
      }
      answerAlg = impl(transactor)
    } yield answerAlg

    private def impl[F[_]: Async](transactor: Transactor[F]): AnswerAlg[F] = new AnswerAlg[F] {
      override def getWholeAnswers(id: Discussion.id): F[List[Answer]] =
        sql"""
           SELECT A.description, U.login
           FROM Answers A
           JOIN Users U ON U.id = A.author_id
           WHERE A.discussion_id = ${id.value}
         """
          .query[(String, String)]
          .to[List]
          .transact(transactor)
          .map(_.map { case (description, login) =>
            Answer(
              Field.description(description),
              User.login(login)
            )
          })

      override def putAnswer(token: User.token, id: Discussion.id, answer: Answer): F[Unit] = {
        sql"""
          WITH user_id AS (
            SELECT id
            FROM Users
            WHERE token = ${token.value}
          )
          INSERT INTO Answers (discussion_id, description, author_id)
          SELECT ${id.value}, ${answer.description.value}, id
          FROM user_id
        """
          .update
          .run
          .transact(transactor)
          .void
      }
    }

    private def createTable[F[_]: Sync](transactor: Transactor[F]): F[Unit] = {
    sql"""
      CREATE TABLE IF NOT EXISTS Answers (
        id SERIAL PRIMARY KEY,
        discussion_id INTEGER NOT NULL,
        description TEXT NOT NULL,
        author_id INTEGER NOT NULL,
        FOREIGN KEY (discussion_id) REFERENCES Discussions(id),
        FOREIGN KEY (author_id) REFERENCES Users(id)
    );
    """
      .update
      .run
      .transact(transactor)
      .void
  }
}
