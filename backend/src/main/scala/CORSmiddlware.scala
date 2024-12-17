import cats.effect.kernel.Async
import org.http4s.server.middleware.CORS
import org.http4s.{HttpRoutes, Method}

import scala.concurrent.duration.DurationInt

object CORSmiddlware {
  def routes[F[_]: Async](service: HttpRoutes[F]): HttpRoutes[F] = {
    CORS.policy
      .withAllowOriginAll
      .withAllowHeadersAll
      .withAllowMethodsIn(Set(Method.GET, Method.POST))
      .withAllowCredentials(false)
      .withMaxAge(1.day)
      .apply(service)
  }
}
