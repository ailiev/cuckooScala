package cuckoo.performance.japex

class CuckooGetDriver extends GetDriver {
    def makeMap (capacity : Int, loadFactor : Float) = new CHT(capacity)
}
