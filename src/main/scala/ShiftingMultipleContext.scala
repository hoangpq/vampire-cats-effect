package vampire.learning.scala

import cats.effect.{IO, IOApp}

import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext
import debug._

object ShiftingMultipleContext extends IOApp.Simple {
  def run = (ec("1"), ec("3")) match {
    case (ec1, ec2) =>
      for {
        _ <- IO("one").debug
        _ <- IO.shift(ec1)
        _ <- IO("two").debug
        _ <- IO.shift(ec2)
        _ <- IO("three").debug
      } yield ()
  }

  def ec(name: String): ExecutionContext =
    ExecutionContext.fromExecutor(Executors.newSingleThreadExecutor { r =>
      val t = new Thread(r, s"pool-$name-thread-1")
      t.setDaemon(true)
      t
    })
}