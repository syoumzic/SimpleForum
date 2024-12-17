import cats.effect.implicits.effectResourceOps
import cats.effect.kernel.Async
import com.comcast.ip4s._
import doobie.Transactor
import fs2.io.net.Network
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder

object Server {

  def run[F[_] : Async : Network]: F[Nothing] = {
    for {
      client <- EmberClientBuilder.default[F].build

      transactor = Transactor.fromDriverManager[F](
        driver = classOf[org.postgresql.Driver].getName,
        url = "jdbc:postgresql://localhost:5432/database",
        user = "user",
        pass = "password",
      )

      userAlg <- postgressql.UserAlg.build[F](transactor).toResource
      discussionAlg <- postgressql.DiscussionAlg.build[F](transactor).toResource
      answerAlg <- postgressql.AnswerAlg.build[F](transactor).toResource

      oauth = yandexoauth.OAuth.build[F](client)

      httpApp = CustomHttpApp.build[F](
        oauth,
        answerAlg,
        discussionAlg,
        userAlg
      )

      _ <-
        EmberServerBuilder.default[F]
          .withHost(ipv4"0.0.0.0")
          .withPort(port"8080")
          .withHttpApp(httpApp)
          .build
    } yield ()
  }.useForever
}