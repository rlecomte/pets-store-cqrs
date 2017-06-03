package io.rlc.infra

package object workflow {
  case class OK(aggregateId: String)
  case class Error(message: String)
}
