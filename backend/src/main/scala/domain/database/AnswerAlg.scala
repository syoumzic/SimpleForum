package domain.database

import domain.model.{Answer, Discussion, User}

trait AnswerAlg[F[_]] {
  def getWholeAnswers(id: Discussion.id): F[List[Answer]]
  def putAnswer(token: User.token, id: Discussion.id, answer: Answer): F[Unit]
}
