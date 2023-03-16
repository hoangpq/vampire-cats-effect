package vampire.learning.scala

import cats.effect.{IO, IOApp}
import cats.implicits.catsSyntaxApplicativeError

import scala.concurrent.Future

object Effect extends IOApp.Simple {

  val divide: PartialFunction[Int, Int] = {
    case d: Int if d != 0 => 12 / d
  }

  def run: IO[Unit] = {


    println(divide.isDefinedAt(1))

    import cats.syntax.apply._

    val ohNoes: IO[Int] = IO.delay(throw new RuntimeException("oh noes!"))
    val twelve: IO[Int] = IO.delay(1 / 0)

    // lift an exception into IO, as long as we provide the "expected" type of the IO
    val ohNoes2: IO[Int] = IO.raiseError(new RuntimeException("oh noes!"))
    val fut: IO[String] = IO.fromFuture(IO.pure(Future.successful("Hello world")))

    val ohNoes3 = IO.raiseError[Int](new RuntimeException("oh noes!"))
    // ohNoes3.handleErrorWith(_ => IO(12)).flatMap(IO.println)

    // ohNoes3.handleError(_ => 12).void
    // ohNoes3.handleErrorWith(t => IO.raiseError(new RuntimeException("What the hell?"))).void

    // if we explicitly want to transform the error into another error,
    // we could use adaptError instead;
    /*ohNoes3.adaptError {
      case e: RuntimeException => new IllegalArgumentException(e)
    }.void*/

    // attempt
    // instead of hiding the error-handling we're now exposing the error,
    // but also delaying the error handling by "lifting" the error in to a
    // successful IO value

    // IO.delay(print("Hello world"))
    val attempted: IO[Either[Throwable, Int]] = ohNoes3
      .map(i => Right(i): Either[Throwable, Int])
      .handleErrorWith(t => IO.pure(Left(t)))

    val result = attempted.onError {
      case _ => IO(println("Oh noes with onError!"))
    }.void


    val io: IO[String] = IO(5 / 0)
      .flatMap(_ => IO("Success!"))

    val result1: IO[String] = io.onError {
      case _: ArithmeticException => IO(println("Division by zero error"))
    }

    result1
      .handleErrorWith(t => IO(println("Something wrong")))
      .void

    // adaptError => transform any error into a successful value
    // handleError(f: Throwable => A): IO[A]
    // handleErrorWith(f: Throwable => IO[A]): IO[A]
    // recover(f: PartialFunction[Throwable, A]): IO[A]


    {
      println(IO("hello, world").unsafeToFuture())
    }

    // transform some kinds of errors into successful values
    result1.recover {
      case _: ArithmeticException => "Division by zero error [recover]"
    }.flatMap(v => IO(println(v)))

    // recoverWith(f: PartialFunction[Throwable, IO[A]]): IO[A]
    // transform some kinds of errors into another effect

    // attempt: IO[Either[Throwable, A]]
    // make errors visible but delay error-handling

    // otherwise, use handleErrorWith(f: Throwable => IO[A]): IO[A]

    // attempted.flatMap(IO.println)
  }
}