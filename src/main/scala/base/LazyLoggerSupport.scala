package base

import org.slf4j.{ Logger ⇒ SLF4JLogger }

trait LazyLoggerSupport {
  val logger = LazyLogger(this.getClass)
}

class LazyLogger(val underlying: SLF4JLogger) {

  @inline final def isTraceEnabled = underlying.isTraceEnabled
  @inline final def trace(msg: ⇒ String): Unit = if (isTraceEnabled) underlying.trace(msg.toString)
  @inline final def trace(msg: ⇒ String, t: ⇒ Throwable): Unit = if (isTraceEnabled) underlying.trace(msg, t)

  @inline final def isDebugEnabled = underlying.isDebugEnabled
  @inline final def debug(msg: ⇒ String): Unit = if (isDebugEnabled) underlying.debug(msg.toString)
  @inline final def debug(msg: ⇒ String, t: ⇒ Throwable): Unit = if (isDebugEnabled) underlying.debug(msg, t)

  @inline final def isErrorEnabled = underlying.isErrorEnabled
  @inline final def error(msg: ⇒ String): Unit = if (isErrorEnabled) underlying.error(msg.toString)
  @inline final def error(msg: ⇒ String, t: ⇒ Throwable): Unit = if (isErrorEnabled) underlying.error(msg, t)

  @inline final def isInfoEnabled = underlying.isInfoEnabled
  @inline final def info(msg: ⇒ String): Unit = if (isInfoEnabled) underlying.info(msg.toString)
  @inline final def info(msg: ⇒ String, t: ⇒ Throwable): Unit = if (isInfoEnabled) underlying.info(msg, t)

  @inline final def isWarnEnabled = underlying.isWarnEnabled
  @inline final def warn(msg: ⇒ String): Unit = if (isWarnEnabled) underlying.warn(msg.toString)
  @inline final def warn(msg: ⇒ String, t: ⇒ Throwable): Unit = if (isWarnEnabled) underlying.warn(msg, t)
}

object LazyLogger {
  import scala.reflect.{ classTag, ClassTag }

  def apply(name: String): LazyLogger = new LazyLogger(org.slf4j.LoggerFactory.getLogger(name))
  def apply(cls: Class[_]): LazyLogger = apply(cls.getName)
  def apply[C: ClassTag](): LazyLogger = apply(classTag[C].runtimeClass.getName)
}
