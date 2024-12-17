package domain.model

object User {
  case class id(value: Int) extends AnyVal
  case class login(value: String) extends AnyVal
  case class token(value: String) extends AnyVal
}

case class User(login: User.login)
