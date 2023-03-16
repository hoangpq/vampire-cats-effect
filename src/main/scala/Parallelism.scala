package vampire.learning.scala

import cats.effect.{IO, IOApp}
import cats.implicits._
import debug._

object Parallelism extends IOApp.Simple {
  def run = for {
    _ <- IO(s"number of CPUs: $numCpus").debug
    _ <- tasks.debug
  } yield ()

  // ensure we submit more than this number of tasks
  val numCpus = Runtime.getRuntime.availableProcessors()
  val tasks = List.range(0, numCpus * 2).parTraverse(task)

  // does nothing
  def task(i: Int): IO[Int] = IO(i).debug

}
