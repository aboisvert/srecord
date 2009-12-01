package srecord

import org.slf4j.{Logger => SLF4JLogger}
import org.slf4j.LoggerFactory

trait Logger {
  protected val LOG: SLF4JLogger = LoggerFactory.getLogger(getClass);
}
