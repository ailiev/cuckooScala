package cuckoo.performance.japex

import cuckoo.CHT

import java.{lang => jl}

class CuckooGetDriver extends GetDriver[CHT[jl.Long, jl.Integer]] {
	type MapT = CHT[jl.Long, jl.Integer]
    override def makeMap (capacity : Int, loadFactor : Float) = new CHT(capacity)
    
    override def mapget(map:MapT, key:jl.Long) = map.get(key).getOrElse(0)

    override def mapset(map:MapT, key:jl.Long, value:ValT) = map.update(key, value)
}
