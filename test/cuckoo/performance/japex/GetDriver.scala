package cuckoo.performance.japex

import com.sun.japex.JapexDriverBase;
import com.sun.japex.TestCase;

import scala.collection.mutable.{Map => MutableMap}

class GetDriver extends JapexDriverBase
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

      htable = new CHT(capacity)

      // fill up the table.
      for (i <- 0 until (capacity*9)/10) {
        htable.update(rand.nextLong, rand.nextInt)
      }

      keys = Array(capacity)
      htable.keys.readInto(keys)
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
