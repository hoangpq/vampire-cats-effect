package vampire.learning.scala

import cats.effect.{IO, IOApp}
import cats.implicits._
import debug._

object ParTraverse extends IOApp.Simple {
  /*
  traverse only requires the effect to have an
  Applicative instance, the Applicative[IO.Par] is
  where the parallelism "happens"

  You can also think of (par)Traverse as a variation of (par)MapN
  where results are collected, but where every input effect has the
  same output type

  parSequence turns a nested structure "inside-out"
   */

  val numOrTasks = 100
  val tasks: List[Int] = List.range(0, numOrTasks)

  def task(id: Int): IO[Int] = IO(id).debug

  val traversal_ = tasks.parTraverse(task).debug.void
  val tasks1: List[IO[Int]] = List.tabulate(numOrTasks)(task)

  def run = tasks1.parSequence.debug.void
}
