package vampire.learning.scala

import cats.effect.IO
import org.polyvariant.colorize.Colorize

object debug {
  implicit class DebugHelper[A](ioa: IO[A]) {
    def debug: IO[A] =
      for {
        a <- ioa
        _ = println(s"[${Thread.currentThread().getName}] $a")
      } yield a
  }

}
