package cuckoo.performance.japex

class JavaHashMapGetDriver extends GetDriver {
    def makeMap (capacity : Int, loadFactor : Float) = 
      new scala.collection.jcl.HashMap(new java.util.HashMap(capacity, loadFactor.toFloat))
}
