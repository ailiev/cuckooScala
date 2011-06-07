package cuckoo.performance.japex

import java.{util => ju}
import java.{lang => jl}

class JavaHashMapGetDriver extends GetDriver[ju.Map[jl.Long,jl.Integer]] {
 	type MapT = ju.Map[jl.Long,jl.Integer]

    override def makeMap (capacity : Int, loadFactor : Float) =
      new ju.HashMap[jl.Long,jl.Integer](capacity, loadFactor.toFloat)

    override def mapget(map:MapT, key:KeyT) = map.get(key)

    override def mapset(map:MapT, key:KeyT, value:ValT) = map.put(key, value)
}
