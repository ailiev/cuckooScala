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

package cuckoo;

import scala.collection.mutable.Map
import scala.util.logging.Logged
import Math.abs

import util.Slf4JLogger

import java.util.Arrays

class CHT[K,V] (alloc : Int)
extends Map[K,V] with Slf4JLogger
{

  val MAX_UPDATE_RECURSION=100

  /** How many hash functions? */
  val H = 2
  /** Bin size for each hash function. */
  val B = 4

  val rand = new scala.util.Random

  // the randomizing params for the hash functions.
  val As = Array (hashParam,hashParam)
  val Bs = Array (hashParam,hashParam)

  def hashParam = rand.nextLong.abs // / (Math.MAX_INT*2l)

  debug("As = " + Arrays.toString(As));
  debug("Bs = " + Arrays.toString(Bs));

  // val hashes = new Array[(Int => Int)] (H)

  /** Entry will be null if no entry at that index. */
  var table = new Array[(K,V)](alloc)

  var _size = 0

  val NUM_BUCKETS = alloc/B	// integer division rounds down.

  for (i <- 0 until H) {
    // Dietzfelbinger strongly universal hash function, really a small variation on
    // a standard multiplicative hash
      // we have the hash return the bucket number.
    // hashes(i) = x => ( ( As(i) * x.abs.toLong + Bs(i) ) >>> 32 ).toInt % NUM_BUCKETS
  }

  private def hashF(hashNum:Int, x:Int)  =
    ( ( ( ( As(hashNum) * x.toLong + Bs(hashNum) ) // randomize the input
    		>>> 33 ) // back down to 32-bit non-negative int
      * NUM_BUCKETS ) >>> 31	// normalize to the correct range. 
    ).toInt

  private def binStartIdx (hashValue:Int) = hashValue * B


  def get  (key : K) : Option[V] = {
    val hcode = key.hashCode

    var i=0
    while(i < H) {
      val binStart = binStartIdx( hashF(i, hcode) )

      // go through the bins
      var binOff = 0
      while (binOff < B) {
        val tableVal = table(binStart+binOff)
        if ((tableVal ne null) && tableVal._1 == key) return Some (tableVal._2)
        binOff = binOff+1
      }
      i = i+1
    }

    None
  }

  /**
   * @return Some(idx, binStartIdx) if key is found.
   * 			Some(-1, emptySlotIdx) if not found,
   * 				and 'findEmpty' is true, and an empty slot for that key is available
   * 			None if not found and no empty slot requested or available.
   */
  private def findIndex
  (key : K, hashcode : Int, findEmpty:Boolean) : Option[(Int,Int)] =
    {
    var emptySlotIdx : Int = -1
    var i=0
    while(i < H) {
      val binStart = binStartIdx( hashF(i, hashcode) )

      // go through the bins
      var binOff = 0
      while (binOff < B) {
        val idx = binStart+binOff
        val tableVal = table(idx)
        if ((tableVal ne null) && tableVal._1 == key) return Some (idx, binStart)
        if (findEmpty && emptySlotIdx == -1 && (tableVal eq null)) {
          // we use the first available empty slot when inserting
          emptySlotIdx = idx
        }
        binOff = binOff+1
      }
      i = i+1
    }

    emptySlotIdx match {
      case -1 => None
      case _  => Some(-1, emptySlotIdx)
    }
  }

  def update(key:K, value:V) = updateHelper(key, value, 0);

  private def updateHelper (key:K, value:V, depth:Int) : Unit = {
    val hcode = key.hashCode

    findIndex(key, hcode, true) match {
      case Some((-1, emptySlotIdx)) => {
        // not present and empty slot was found
        table(emptySlotIdx) = (key, value)
        _size = _size+1
        return
      }
      case Some((idx, binStart)) => {
        // update the value, size is the same.
        table(idx) = (key, value)
        return
      }
      case None => // need to evict and re-try the insert ...
    }

    // bin is full. so we:
    // check for excessive recursion
    if (depth > MAX_UPDATE_RECURSION) {
      val msg = "Failed to insert after " + MAX_UPDATE_RECURSION +
                " attempts. Table size is " +  _size
      info(msg)
      throw new RuntimeException (msg);
      // TODO: implement table grow and re-hashing if needed.
      // TODO: would be nice to report the original update params which caused
      // the failure.
    }

    if (_size > 0.92*alloc) {
      warn("Inserting into a almost full table with size {} and alloc {}",
           _size, alloc)
    }

    // - select a bin at random, ie. using one of our hash functions at random
    val binIdx = binStartIdx ( hashF(rand.nextInt(H), hcode) )
    // - remove the oldest entry in it, which is in first idx of the bin --> (k,v)
    val oldEntry = table(binIdx)
    debug ("Evicting " + oldEntry + " while inserting " + (key,value))
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

  def elements = table.elements.filter { _ ne null }

  def -= (key : K) : Unit = {
    findIndex(key, key.hashCode, false) match {
      case Some((idx, binStart)) => {
          // slide the subsequent entries in the bin down
          for (j <- idx until binStart+B-1) {
        	  table(j) = table(j+1)
          }
          // the last bin entry will be empty
          table(binStart+B-1) = null
          _size = _size-1
      }
      case (None) => debug("Do not have key {}", key)
    }
  }

  override def clear = {
    for (i <- 0 until table.size) {
      table(i) = null
    }
    _size = 0
  }

}

object Utils
{
}