/*
 * Scala implementation of Cuckoo hashing.
 *
 * Copyright (C) 2010, Alexander Iliev <alex.iliev@gmail.com>
 *
 * All rights reserved.
 *
 * This code is released under a BSD license.
 * Please see LICENSE.txt for the full license and disclaimers.
 *
 */

package cuckoo.performance.japex

import com.sun.japex.JapexDriverBase;
import com.sun.japex.TestCase;
import com.sun.japex.Constants

import java.lang.Long

import util.Slf4JLogger

import scala.collection.mutable.{Map => MutableMap}
import java.{lang => jl}

/** Japex driver for testing the get() method of a Scala mutable map. */
abstract class GetDriver[MapT <: AnyRef] extends JapexDriverBase with Slf4JLogger
{
  type KeyT = jl.Long
  type ValT = jl.Integer
  
  var capacity : Int = 0
  var timedBatchSize : Int = 0

  var htable : MapT = null.asInstanceOf[MapT]

  val rand = new scala.util.Random

  var keys : Array[KeyT] = _

  /** Subclass should define how to make the particular map. */
  def makeMap (capacity : Int, loadFactor : Float) : MapT
  
  def mapget(map:MapT, key:KeyT) : ValT
  def mapset(map:MapT, key:KeyT, value:ValT) : Unit

  override def prepare (testcase : TestCase) : Unit =
    {
      capacity = testcase.getIntParam("tableCapacity")
      timedBatchSize = testcase.getIntParam("timedBatchSize")
      val loadFactor = testcase.getDoubleParam("loadFactor")

      info("Using capacity {} and batch size {}",
      		capacity, timedBatchSize)

      htable = makeMap (capacity, loadFactor.toFloat)
      keys = new Array(capacity)

      prepareTestMap (htable, keys, capacity, loadFactor.toFloat)
    }

  def prepareTestMap
  (map : MapT, keys : Array[KeyT],
   capacity : Int, loadFactor : Float) =
  {
      // fill up the table and the keys array
      val numKeys = (capacity.toDouble*loadFactor*0.98).toInt
      info ("Using map entries: " + numKeys)
      for (i <- 0 until numKeys) {
        val key = Long.valueOf(rand.nextLong)
        mapset(map, key, rand.nextInt)
        keys(i) = key
      }

      // fill in the rest of the lookup keys
      for (i <- 0 until capacity-numKeys) {
        keys(numKeys + i) = keys(i)
      }
  }

  override def run (testcase : TestCase) : Unit =
    {
      var keyIdx = rand.nextInt(keys.size)
      var goldPot = 0
      var i=0
      while ( i < timedBatchSize ) {

        val value = mapget(htable, keys(keyIdx))

        goldPot = goldPot + value
    	keyIdx = (keyIdx + 1) % capacity
    	i = i+1
      }
    }

  override def finish (testcase : TestCase) : Unit =
	  {

	  }

  def repeat[T] (numTimes : Int, fGen : (Unit => Unit)) = {
    for (i <- 0 until numTimes) {
      fGen()
    }
  }
}
