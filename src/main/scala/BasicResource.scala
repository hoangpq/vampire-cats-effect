package vampire.learning.scala

import debug._

import cats.effect.{IO, IOApp, Resource}

import java.io.RandomAccessFile

class FileBufferReader private(in: RandomAccessFile) {
  def readBuffer(offset: Long): IO[(Array[Byte], Int)] =
    IO {
      in.seek(offset)
      val buf = new Array[Byte](FileBufferReader.bufferSize)
      val len = in.read(buf)
      (buf, len)
    }

  private def close: IO[Unit] = IO(in.close())
}

object FileBufferReader {
  val bufferSize = 4096

  def makeResource(fileName: String): Resource[IO, FileBufferReader] =
    Resource.make {
      IO(new FileBufferReader(new RandomAccessFile(fileName, "r")))
    } { res =>
      res.close
    }
}

object BasicResource extends IOApp.Simple {
  def resourceTest: IO[Unit] = stringResource.use { s =>
    IO(s"$s is so cool!").debug
    // try to raise error, resource if property released event when the use effect fails
    IO.raiseError(new RuntimeException("oh noes!"))
  }.attempt.debug.void

  val stringResource: Resource[IO, String] =
    Resource.make(
      IO("> acquiring stringResource").debug
        // must return IO
        *> IO("String")
    )(_ => IO("< releasing stringResource").debug.void)

  /*
  Since we want the Resource to manage the hidden state, we make the close method inaccessible
  from outside caller

 We make our Resource by creating the FileBufferReader is an IO effect,
 ensuring that we close the state when the Resource is released
   */
  def run = FileBufferReader
    .makeResource("src/main/scala/Asynchronous.scala")
    .use { s =>
      for {
        data <- s.readBuffer(0)
        _ <- IO(println(new String(data._1.slice(0, data._2), "UTF-8"))).debug
      } yield ()
    }.attempt.void
}
