package io.rlc.infra.utils

object UUID {
  def next(): java.util.UUID = java.util.UUID.randomUUID()
}
