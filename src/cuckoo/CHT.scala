package cuckoo;

import scala.collection.mutable.Map

class CHT[K,V] (size : Int) extends Map[K,V] {

  var table = new Array[(K,V)](size)

  val hashes = new Array[(Int => Int)] (2)

  hashes(0) = x => x+1
  hashes(1) = x => x+3

  def get  (key : K) : Option[V] = {
    val hcode = key.hashCode
    for (hash <- hashes) {
      val tableIdx = hash(hcode)
      val tableVal = table(tableIdx)
      if ((tableVal ne null) && tableVal._1 == key) return Some (tableVal._2)
    }

    None
  }

  def update (key:K, value:V) : Unit = {
    
  }

  def size () = 7

  def elements = null

  def -= (key : K) : Unit = {
    
  }
}
