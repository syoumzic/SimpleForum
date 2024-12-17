import cats.effect.Async
import cats.effect.kernel.Sync
import cats.syntax.all._
import domain.database.{AnswerAlg, DiscussionAlg, UserAlg}
import domain.model.{Answer, Discussion, Field, User}
import domain.oauth.OAuthAlg
import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.circe.toMessageSyntax
import org.http4s.dsl.Http4sDsl

object CustomRoutes {
  implicit val AnswerIdEncoder: Encoder[Answer.id] = Encoder.instance { id =>
    Json.fromInt(id.value)
  }

  implicit val DiscussionIdEncoder: Encoder[Discussion.id] = Encoder.instance { id =>
    Json.fromInt(id.value)
  }

  implicit val FieldTitleEncoder: Encoder[Field.title] = Encoder.instance { title =>
    Json.fromString(title.value)
  }

  implicit val FieldDescriptionEncoder: Encoder[Field.description] = Encoder.instance { description =>
    Json.fromString(description.value)
  }

  implicit val FieldIdEncoder: Encoder[User.id] = Encoder.instance { id =>
    Json.fromInt(id.value)
  }

  implicit val FieldLoginEncoder: Encoder[User.login] = Encoder.instance { login =>
    Json.fromString(login.value)
  }

  implicit val FieldTokenEncoder: Encoder[User.token] = Encoder.instance { token =>
    Json.fromString(token.value)
  }

  private case class PutDiscussionRequest(
     token: String,
     title: String,
     description: String,
  )

  private case class PutAnswerRequest(
     token: String,
     discussionId: String,
     description: String
  )

  private case class RequestWithToken(
     token: String
  )

  def routes[F[_]: Async](oauthAlg: OAuthAlg[F], answerAlg: AnswerAlg[F], discussionAlg: DiscussionAlg[F], userAlg: UserAlg[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._

    HttpRoutes.of[F] {
      case GET -> Root / "discussions" =>
        for {
          indexDiscussions <- discussionAlg.getWholeDiscussionsWithIndex
          resp <- Ok(indexDiscussions.asJson)
        }yield resp

      case GET -> Root / "discussion" / rawDiscussionID =>
        for {
            discussionID <- Sync[F].pure(Discussion.toId(rawDiscussionID))
            discussion   <- discussionAlg.getDiscussion(discussionID)
            answers      <- answerAlg.getWholeAnswers(discussionID)
            resp <- Ok((discussion, answers).asJson)
        } yield resp

      case req @ POST -> Root / "login" =>
        for {
          request <- req.asJsonDecode[RequestWithToken]
          token = User.token(request.token)
          _ <- Sync[F].delay(println("success parse token"))
          _ <- userAlg.assertNoExist(token)
          _ <- Sync[F].delay(println("user not found success"))
          newUser <- oauthAlg.getUser(token)
          _ <- Sync[F].delay(println("success get data of user from remote server"))
          _ <- userAlg.putUser(token, newUser)
          _ <- Sync[F].delay(println("success put new user"))
          resp <- Ok("user successfully registering")
        } yield resp

      case req @ POST -> Root / "myDiscussion" =>
        for{
          request <- req.asJsonDecode[RequestWithToken]
          _ <- Sync[F].delay(println("success get request"))
          token = User.token(request.token)
          _ <- userAlg.assertExist(token)
          _ <- Sync[F].delay(println("success user assert"))
          indexDiscussions <- discussionAlg.getWholeDiscussionsWithIndex(token)
          _ <- Sync[F].delay(println("success get whole discussion"))
          resp <- Ok(indexDiscussions.asJson)
        } yield resp

      case req @ POST -> Root / "putDiscussion" =>
        for {
          request <- req.asJsonDecode[PutDiscussionRequest]

          token = User.token(request.token)
          _ <- userAlg.assertExist(token)

          title = Field.title(request.title)
          description = Field.description(request.description)
          user <- userAlg.getUser(token)
          discussion = Discussion(title, description, user.login)

          _ <- discussionAlg.putDiscussion(token, discussion)

          resp <- Ok("successfully put discussion")
        } yield resp

      case req @ POST -> Root / "putAnswer" =>
        for {
          request <- req.asJsonDecode[PutAnswerRequest]

          token = User.token(request.token)
          _ <- userAlg.assertExist(token)
          _ <- Sync[F].delay(println("success assert exist"))

          discussionId = Discussion.toId(request.discussionId)
          description = Field.description(request.description)
          user <- userAlg.getUser(token)
          _ <- Sync[F].delay(println("success get user"))
          answer = Answer(description, user.login)

          _ <- answerAlg.putAnswer(token, discussionId, answer)
          _ <- Sync[F].delay(println("success put answer"))
          resp <- Ok("successfully put answer")
      } yield resp
    }
  }
}
