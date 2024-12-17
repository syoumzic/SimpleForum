package domain.model

import scala.util.Try

object Discussion {
  case class id(value: Int)

  def toId(stringValue: String): id =
  Try(id(stringValue.toInt)).getOrElse(throw InvalidDiscussionId)
}

case object InvalidDiscussionId extends Exception
case class IndexDiscussion(id: Discussion.id, title: Field.title, description: Field.description, author: User.login)
case class Discussion(title: Field.title, description: Field.description, author: User.login)
