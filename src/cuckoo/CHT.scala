package cuckoo;

import scala.collection.mutable.Map
import scala.util.logging.Logged
import Math.abs

import java.util.Arrays

class CHT[K,V] (alloc : Int) extends Map[K,V] with Logged {

  val MAX_UPDATE_RECURSION=100

  /** How many hash functions? */
  val H = 2
  /** Bin size for each hash function. */
  val B = 2

  val rand = new scala.util.Random

  // the randomizing params for the hash functions.
  val As = Array (rand.nextLong,rand.nextLong).map(abs).map{_ / (Math.MAX_INT*2l)}
  val Bs = Array (rand.nextLong,rand.nextLong).map(abs).map{_ / (Math.MAX_INT*2l)}

  log("As = " + Arrays.toString(As));
  log("Bs = " + Arrays.toString(Bs));

  val hashes = new Array[(Int => Int)] (H)

  /** Entry will be null if no entry at that index. */
  var table = new Array[(K,V)](alloc)

  var _size = 0

  val NUM_BUCKETS = alloc/B	// integer division rounds down.

  for (i <- 0 until H) {
    // Dietzfelbinger strongly universal hash function, really a small variation on
    // a standard multiplicative hash
      // we have the hash return the bucket number.
    hashes(i) = x => ( ( As(i) * x.abs.toLong + Bs(i) ) >>> 32 ).toInt % NUM_BUCKETS
  }

  def binStartIdx (hashValue:Int) = hashValue * B


  def get  (key : K) : Option[V] = {
    val hcode = key.hashCode
    for (hash <- hashes) {
      val binStart = binStartIdx( hash(hcode) )
      // go through the bins
      for (i <- binStart until binStart+B) {
        val tableVal = table(i)
        if ((tableVal ne null) && tableVal._1 == key) return Some (tableVal._2)
      }
    }

    None
  }

  def update(key:K, value:V) = updateHelper(key, value, 0);

  private def updateHelper (key:K, value:V, depth:Int) : Unit = {
    val hcode = key.hashCode

    for (hash <- hashes) {
      val binStart = binStartIdx(hash(hcode))
      // go through the bins
      for (i <- binStart until binStart+B) {
        val tableVal = table(i)
        if ( (tableVal eq null) || (tableVal._1 == key) ) {
          // found either empty slot or same key, so set new value here and we're done.
          table(i) = (key, value)
          _size = _size+1
          return
        }
      }
    }

    // bin is full. so we:
    // check for excessive recursion
    if (depth > MAX_UPDATE_RECURSION) {
      throw new RuntimeException ("Failed to insert after " + MAX_UPDATE_RECURSION +
                                    " attempts");
      // TODO: would be nice to report the original update params which caused
      // the failure.
    }

    // - select a bin at random
    val binIdx = binStartIdx ( hashes(rand.nextInt(B)) (hcode) )
    // - remove the oldest entry in it, which is in first idx of the bin --> (k,v)
    val oldEntry = table(binIdx)
    log ("Evicting " + oldEntry + " while inserting " + (key,value))
    // - slide the other entries up
    for (i <- 0 until B-1) {
      table(binIdx+i) = table(binIdx+i+1)
    }
    // - insert the new (key,value) at the end of the bin
    table(binIdx+B-1) = (key,value)
    // - recursively insert (k,v) into the table.
    updateHelper (oldEntry._1, oldEntry._2, depth+1)
  }

  def size () = _size

  def elements = null

  def -= (key : K) : Unit = {
    throw new UnsupportedOperationException("-=" + key)
  }

  override def clear  : Unit = {
    for (i <- 0 until table.size) {
      table(i) = null
    }
  }

}

object Utils
{
}