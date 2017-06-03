package io.rlc.infra

import akka.actor.Props

trait AggregateProps[T] {
  def props(id: String): Props
}
