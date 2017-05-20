package base

case class LogicError(service: String, message: String) extends RuntimeException(s"$service - $message")
