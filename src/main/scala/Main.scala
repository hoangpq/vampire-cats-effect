package vampire.learning.scala

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.{DurationInt, FiniteDuration}
import scala.concurrent.{Await, Future}
// import cats.implicits._
import scala.concurrent.ExecutionContext.Implicits.global

case class MyIO[A](unsafeRun: () => A) {
  def map[B](f: A => B): MyIO[B] = MyIO(() => f(unsafeRun()))

  def flatMap[B](f: A => MyIO[B]): MyIO[B] =
    MyIO(() => f(unsafeRun()).unsafeRun())
}

object MyIO {
  def putStr(s: => String): MyIO[Unit] = MyIO(() => println(s))
}

object Main {

  def main(args: Array[String]): Unit = {
    // applicative
    // println((Option(1), Option(2), Option(3)).mapN(_ + _ + _ + 1))

    // val print = Future(println("Hello"))
    // val twice = print.flatMap(_ => print)
    // Await.result(twice, 3.seconds)

    val hello = MyIO.putStr("Hello")
    val world = MyIO.putStr("World")

    val helloWorld = for {
      _ <- hello
      _ <- world
    } yield ()

    helloWorld.unsafeRun()

    val clock: MyIO[Long] = MyIO(() => System.currentTimeMillis())

    def time[A](action: MyIO[A]): MyIO[(FiniteDuration, A)] = {
      for {
        start <- clock
        a <- action
        end <- clock
      } yield (FiniteDuration(end - start, TimeUnit.MILLISECONDS), a)
    }

    val timedHello = time(MyIO.putStr("hello"))
    timedHello.unsafeRun() match {
      case (duration, _) => println(s"'hello' took $duration")
    }


  }
}