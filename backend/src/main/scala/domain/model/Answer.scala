package domain.model

object Answer {
  case class id(value: Int) extends AnyVal
}

case class Answer(description: Field.description, user: User.login)