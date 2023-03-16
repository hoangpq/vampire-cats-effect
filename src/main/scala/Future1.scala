package vampire.learning.scala

import debug._

import cats.effect.{ContextShift, IO}
import cats.syntax.parallel._

import scala.concurrent.ExecutionContext.Implicits.global

object Future1 extends App {

  val hello = IO(println(s"[${Thread.currentThread().getName}] Hello"))
  val world = IO(println(s"[${Thread.currentThread().getName}] World"))

  private val hw1: IO[Unit] =
    for {
      _ <- hello
      _ <- world
    } yield ()

  // Provide an implicit ContextShift instance for IO
  implicit val cs: ContextShift[IO] = IO.contextShift(global)

  // Define some IO values
  val io1 = IO(10).debug
  val io2 = IO("Hello").debug
  val io3 = IO(true).debug

  // Apply a function to the results of multiple IOs using parMapN
  val hw2: IO[String] = (io1, io2, io3).parMapN { (intVal, stringVal, boolVal) =>
    s"$stringVal, the integer value is $intVal and the boolean value is $boolVal"
  }

  println(hw2.debug.unsafeRunSync())
}
