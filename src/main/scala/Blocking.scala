package vampire.learning.scala

import cats.effect.{Blocker, IO, IOApp}
import debug._

object Blocking extends IOApp.Simple {
  def run = Blocker[IO].use { blocker =>
    withBlocker(blocker).void
  }

  /*def blockingDebug[A](blocker: Blocker, a: => A): IO[A] =
    blocker.delay {
      val value = a
      println(s"[${Thread.currentThread().getName}] $value")
      value
    }*/

  /*
  if something doesn't have a callback API, then you
  know it's blocking
   */

  /*
  We can produce an asynchronous boundary with the
  IO.shift method
   */

  def withBlocker(blocker: Blocker): IO[Unit] =
    for {
      _ <- IO("on default").debug
      _ <- blocker.blockOn(IO("on blocker").debug)
      _ <- IO("where am I?").debug
      /* shift to run on different thread in the context */
      _ <- IO("one").debug
      _ <- IO.shift
      _ <- IO("two").debug
      _ <- IO.shift
      _ <- IO("three").debug
    } yield ()

}
