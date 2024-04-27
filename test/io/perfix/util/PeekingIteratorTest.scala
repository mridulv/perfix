package io.perfix.util

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class PeekingIteratorTest extends AnyWordSpec with Matchers {

  "PeekingIterator" should {

    "return None if peeked when empty" in {
      val emptyIterator = Iterator.empty[Int]
      val peekingIterator = new PeekingIterator(emptyIterator)

      peekingIterator.peek() shouldBe None
      peekingIterator.hasNext shouldBe false
    }

    "peek the next element without removing it" in {
      val iterator = Iterator(1, 2, 3)
      val peekingIterator = new PeekingIterator(iterator)

      peekingIterator.peek() shouldBe Some(1)
      peekingIterator.peek() shouldBe Some(1) // Check idempotence of peek
      peekingIterator.hasNext shouldBe true
      peekingIterator.next() shouldBe 1
      peekingIterator.next() shouldBe 2
    }

    "correctly indicate if there are more elements to iterate over" in {
      val iterator = Iterator(1)
      val peekingIterator = new PeekingIterator(iterator)

      peekingIterator.hasNext shouldBe true
      peekingIterator.next() shouldBe 1
      peekingIterator.hasNext shouldBe false
      an[NoSuchElementException] should be thrownBy peekingIterator.next()
    }

    "handle transitions between peeking and next correctly" in {
      val iterator = Iterator(1, 2, 3)
      val peekingIterator = new PeekingIterator(iterator)

      peekingIterator.peek() shouldBe Some(1)
      peekingIterator.next() shouldBe 1
      peekingIterator.hasNext shouldBe true
      peekingIterator.peek() shouldBe Some(2)
      peekingIterator.next() shouldBe 2
      peekingIterator.peek() shouldBe Some(3)
      peekingIterator.next() shouldBe 3
      peekingIterator.hasNext shouldBe false
      peekingIterator.peek() shouldBe None
      an[NoSuchElementException] should be thrownBy peekingIterator.next()
    }

  }
}

