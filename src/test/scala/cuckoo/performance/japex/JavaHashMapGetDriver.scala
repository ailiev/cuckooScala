package cuckoo.performance.japex

import scalaj.collection.Imports._

import scala.collection.mutable

class JavaHashMapGetDriver extends GetDriver {
 
    def makeMap (capacity : Int, loadFactor : Float) =
      new java.util.HashMap[java.lang.Long,Int](capacity, loadFactor.toFloat).asScalaMutable
}
