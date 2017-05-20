package base

import scala.util.{ Failure, Success, Try }

trait FailureReporterSupport {

  def withFailureLogging[T](f: ⇒ T, report: Throwable ⇒ _): T = {
    Try(f) match {
      case Success(x) ⇒ x
      case Failure(exc) ⇒
        report(exc)
        throw exc
    }
  }
}

object FailureReporterSupport extends FailureReporterSupport
