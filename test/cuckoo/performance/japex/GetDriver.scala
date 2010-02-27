package cuckoo.performance.japex

import com.sun.japex.JapexDriverBase;
import com.sun.japex.TestCase;

import util.Slf4JLogger

import scala.collection.mutable.{Map => MutableMap}

class GetDriver extends JapexDriverBase with Slf4JLogger
{
  var capacity : Int = 0
  var timedBatchSize : Int = 0

  var htable : MutableMap[Long,Int] = null

  val rand = new scala.util.Random

  var keys : Array[Long] = null

  var keyIdx = 0

  var goldPot : Int = 0

  override def prepare (testcase : TestCase) : Unit =
    {
      capacity = testcase.getIntParam("tableCapacity")
      timedBatchSize = testcase.getIntParam("timedBatchSize")

      info("Using capacity {} and batch size {}",
      		capacity, timedBatchSize)

      htable = new CHT(capacity)

      keys = new Array(capacity)

      // fill up the table and the keys array
      for (i <- 0 until (capacity*9)/10) {
        val key = rand.nextLong
        htable.update(key, rand.nextInt)
        keys(i) = key
      }
    }

  override def run (testcase : TestCase) : Unit =
    {
      var i=0
      while ( i < timedBatchSize ) {

        val value = htable.get(keys(keyIdx))

        goldPot = goldPot + value.getOrElse(0)
    	keyIdx = (keyIdx+1)%capacity
    	i = i+1
      }
    }

  def repeat[T] (numTimes : Int, fGen : (Unit => Unit)) = {
    for (i <- 0 until numTimes) {
      fGen()
    }
  }
}
