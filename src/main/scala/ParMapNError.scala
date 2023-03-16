package vampire.learning.scala

import cats.effect.{IO, IOApp}
import cats.syntax.parallel._
import debug._

import scala.concurrent.duration.DurationInt

object ParMapNError extends IOApp.Simple {

  val ok = IO("hi").debug
  val ko1 = IO.sleep(1.seconds).as("ko1").debug *> IO.raiseError[String](new RuntimeException("oh!")).debug
  val ko2 = IO.raiseError[String](new RuntimeException("noes!")).debug

  val e1 = (ok, ko1).parTupled.void
  val e2 = (ko1, ok).parTupled.void
  val e3 = (ko1, ko2).parTupled.void

  val attempt_ = e1.attempt.debug *>
    IO("___").debug *>
    e2.attempt.debug *>
    IO("___").debug *>
    e3.attempt.debug *> IO.pure(()).void

  def run: IO[Unit] = attempt_ *> IO(println("hello, world!"))
}
