package base

import java.util.UUID
import java.util.concurrent.atomic.AtomicLong

trait IDGenerator {
  def newID: String = UUID.randomUUID().toString
}

trait IDGeneratorLong {
  private val _idCounter = new AtomicLong
  def newID: Long = _idCounter.incrementAndGet()
}
