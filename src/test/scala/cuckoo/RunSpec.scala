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

package cuckoo

/** Small runner for the Map ScalaCheck spec */
object RunSpec extends Application {  
  import org.scalacheck._  
  import org.scalacheck.Test._  

  val params = Params(2000, 7000, 1, 90, new java.util.Random(), 1, new ConsoleReporter(1))
  new MapSpecification(new cuckoo.CHT[java.lang.Long,Int] (55)).check(params)
}
