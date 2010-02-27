package cuckoo.performance.japex

import com.sun.japex.TestCase
import com.sun.japex.TestCaseImpl
import com.sun.japex.ParamsImpl

// Doing this to get the SizeOf info, as that doesn't work when run under
// japex. Japex's custom classloader loses the static Instrumentaion field set by
// the SizeOf class in its premain callback
object JapexDirectRun extends Application
{
  // val driver = new CuckooGetDriver
  val driver = new JavaHashMapGetDriver

  val testcase = new TestCaseImpl("hahaha", new ParamsImpl)
  testcase.setIntParam("tableCapacity", 20000)
  testcase.setIntParam("timedBatchSize", 500)
  testcase.setDoubleParam("loadFactor", 0.92)

  driver.prepare(testcase)

  driver.finish(testcase)
}
