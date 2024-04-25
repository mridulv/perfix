package io.perfix.util

class PeekingIterator[A](iterator: Iterator[A]) {
  private var lookaheadOption: Option[A] = None

  def peek(): Option[A] = {
    lookaheadOption match {
      case Some(_) => lookaheadOption
      case None => if (iterator.hasNext) {
        lookaheadOption = Some(iterator.next())
        lookaheadOption
      } else {
        None
      }
    }
  }

  def hasNext: Boolean = lookaheadOption.isDefined || iterator.hasNext

  def next(): A = {
    lookaheadOption match {
      case Some(value) =>
        lookaheadOption = None
        value
      case None =>
        if (iterator.hasNext) iterator.next()
        else throw new NoSuchElementException("No more elements")
    }
  }
}

