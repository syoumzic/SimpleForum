package domain.database

import domain.model.{Discussion, IndexDiscussion, User}

trait DiscussionAlg[F[_]] {
  def getWholeDiscussionsWithIndex: F[List[IndexDiscussion]]
  def getDiscussion(id: Discussion.id): F[Discussion]
  def getWholeDiscussionsWithIndex(token: User.token): F[List[IndexDiscussion]]
  def putDiscussion(token: User.token, discussion: Discussion): F[Unit]
}

case object DiscussionNotFound extends Exception

