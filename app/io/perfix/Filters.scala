package io.perfix

import org.apache.pekko.stream.Materializer
import play.api.mvc.{Filter, RequestHeader, Result}
import javax.inject.Inject
import javax.inject.Singleton

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class Filters @Inject()(implicit override val mat: Materializer,
                        exec: ExecutionContext) extends Filter {

  override def apply(nextFilter: RequestHeader => Future[Result])(requestHeader: RequestHeader): Future[Result] = {

    nextFilter(requestHeader).map { result =>
      println("Headers " + result)
      result.withHeaders(
        "Access-Control-Allow-Origin" -> "*"
        , "Access-Control-Allow-Methods" -> "OPTIONS, GET, POST, PUT, DELETE, HEAD" // OPTIONS for pre-flight
        , "Access-Control-Allow-Headers" -> "Accept, Content-Type, Origin, X-Json, X-Prototype-Version, X-Requested-With" //, "X-My-NonStd-Option"
        , "Access-Control-Allow-Credentials" -> "true"
      )
    }
  }

}
