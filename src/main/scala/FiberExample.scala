package vampire.learning.scala

import cats.effect.{IO, IOApp}
import debug._

import cats.effect.implicits._
import cats.implicits._

import scala.concurrent.duration.{DurationInt, FiniteDuration, durationToPair}

object FiberExample extends IOApp.Simple {

  /*
  first *> second is equivalent to (first, second).mapN((_, b) => second)
   */
  def task: IO[String] = IO.sleep(2.seconds) *> IO("task").debug

  /*
  With racePair, we can complete our implementation of cancellation-on-error
   */
  def myParMapN[A, B, C](ia: IO[A], ib: IO[B])(f: (A, B) => C): IO[C] =
    IO.racePair(ia, ib).flatMap {
      case Left((a, fb)) => (IO.pure(a), fb.join).mapN(f)
      case Right((fa, b)) => (fa.join, IO.pure(b)).mapN(f)
    }

  /*def run = for {
    // start: IO[Fiber[IO, A]]
    fiber <- task.start
    _ <- IO("pre-join").debug
    _ <- fiber.join.debug
    _ <- IO("post-join").debug
  } yield ()*/

  /*
  IO.never is a built-in non-terminating effect
  It has type IO[Nothing], so since type Nothing is a subtype of every other type,
  and type with no values, this effect can never complete
  But it can be cancelled
   */
  val taskNeverFinish: IO[String] = IO("task").debug *> IO.never

  // fiber cancel
  def fiberCancel = for {
    fiber <- taskNeverFinish
      .onCancel(IO("I was cancelled").debug.void)
      .start
    _ <- IO("pre-cancel").debug
    _ <- fiber.cancel
    _ <- IO("canceled").debug
  } yield ()

  // tickingClock example
  val tickingClock: IO[Unit] =
    for {
      _ <- IO(System.currentTimeMillis).debug
      _ <- IO.sleep(1.second)
      _ <- tickingClock
    } yield ()

  val ohNoes = IO.sleep(2.second) *> IO.raiseError(new RuntimeException("oh noes!"))

  val together = (tickingClock, ohNoes).parTupled.void

  def annotatedSleep(name: String, duration: FiniteDuration): IO[Unit] =
    (
      IO(s"$name: starting").debug *>
        IO.sleep(duration) *>
        IO(s"$name: done").debug
      ).onCancel(IO(s"$name: cancelled").debug.void).void

  val task2: IO[Unit] = annotatedSleep("task2", 100.millis)
  val timeout: IO[Unit] = annotatedSleep("timeout", 500.millis)

  def annotatedSleepRun = for {
    /*done <- IO.race(task2, timeout)
    _ <- done match {
      case Left(_) => IO("task2: won").debug
      case Right(_) => IO("timeout: won").debug
    }*/
    // This is common pattern and can be replace by IO.timeout
    _ <- task2.timeoutTo(500.millis, IO("timed out").debug)
  } yield ()

  /*
  Racing without automatic cancellation
  IO.racePair
   */


  def run = myParMapN(
    IO("hello"),
    IO("world")
  )((a, b) => s"$a $b").debug.void
}
