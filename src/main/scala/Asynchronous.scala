package vampire.learning.scala

import debug._

import cats.effect.{IO, IOApp}

import java.util.concurrent.CompletableFuture
import scala.jdk.FunctionConverters._

object Asynchronous extends IOApp.Simple {

  val effect: IO[String] = fromCF(IO(cf()))

  def fromCF[A](cfa: IO[CompletableFuture[A]]): IO[A] =
    cfa.flatMap { fa =>
      IO.async { cb =>
        val handler: (A, Throwable) => Unit = {
          case (a, null) => cb(Right(a))
          case (null, t) => cb(Left(t))
          case (a, t) => sys.error(s"CompletableFuture should always have one null, got $a $t")
        }
        fa.handle(handler.asJavaBiFunction)
      }
    }

  def cf(): CompletableFuture[String] =
    CompletableFuture.supplyAsync(() => "woo!")

  def run = effect.debug.void
}
