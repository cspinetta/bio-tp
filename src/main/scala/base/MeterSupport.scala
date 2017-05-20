package base

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.Try

trait MeterSupport {

  def withTimeLogging[T](f: ⇒ T, processTime: Long ⇒ _): T = {
    val start = System.currentTimeMillis()
    val tryF = Try(f)
    val total = System.currentTimeMillis() - start
    processTime(total)
    tryF.get
  }

  def withTimeLoggingAsync[T](f: ⇒ Future[T], processTime: Long ⇒ _)(implicit ec: ExecutionContext): Future[T] = {
    val start = System.currentTimeMillis()
    val evaluatedF = f // Don't use it again!!
    evaluatedF.onComplete(_ ⇒ {
      val total = System.currentTimeMillis() - start
      processTime(total)
    })
    evaluatedF
  }

  def withTimeLoggingInNano[T](f: ⇒ T, processTime: Long ⇒ _): T = {
    val start = System.nanoTime()
    val tryF = Try(f)
    val total = System.nanoTime() - start
    processTime(total)
    tryF.get
  }

  def withTimeLoggingInMicro[T](f: ⇒ T, processTime: Long ⇒ _): T = {
    val start = System.nanoTime()
    val tryF = Try(f)
    val total = (System.nanoTime() - start) / 1000
    processTime(total)
    tryF.get
  }

}

object MeterSupport extends MeterSupport
