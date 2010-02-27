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
      val mapImpl = testcase.getParam("mapImpl")
      val loadFactor = testcase.getDoubleParam("loadFactor")

      info("Using capacity {} and batch size {}",
      		capacity, timedBatchSize)

      htable = mapImpl match
        { case "cuckoo"	=> new CHT(capacity)
          case "java.util.HashMap" =>
            new scala.collection.jcl.HashMap(new java.util.HashMap(capacity, loadFactor.toFloat))
          }

      keys = new Array(capacity)

      // fill up the table and the keys array
      val numKeys = (capacity.toDouble*loadFactor*0.98).toInt
      info ("Using map entries: " + numKeys)
      for (i <- 0 until numKeys) {
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
