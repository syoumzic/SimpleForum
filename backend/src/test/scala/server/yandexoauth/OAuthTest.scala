import cats.effect._
import cats.effect.implicits._
import org.http4s._
import org.http4s.client.Client
import org.http4s.circe._
import io.circe.Json
import io.circe.syntax._
import io.circe.generic.auto._
import munit.CatsEffectSuite
import domain.model.User
import domain.oauth.OAuthAlg
import yandexoauth.OAuth

class OAuthTest extends CatsEffectSuite {
  val testToken = User.token("test_token")

  val oauthResponseJson = """{"id":"1","login":"login","client_id":"kajdio","display_name":"Name Surname","real_name":"Name Surname","first_name":"Name","last_name":"Surname","sex":"sex","psuid":"psu_id"}"""

  val mockClient: Client[IO] = Client { req =>
    assert(req.uri.toString == "https://login.yandex.ru/info?&oauth_token=test_token")
    Resource.pure(Response[IO](Status.Ok).withEntity(oauthResponseJson))
  }

  test("OAuth.getUser should return User with correct login") {
    println(oauthResponseJson)
    val oauthAlg: OAuthAlg[IO] = OAuth.build(mockClient)
    val result = oauthAlg.getUser(testToken).unsafeRunSync()
    assertEquals(result, User(User.login("login")))
  }
}