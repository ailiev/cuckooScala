package cuckoo

object RunSpec extends Application {  
  import org.scalacheck._  
  import org.scalacheck.Test._  

  val params = Params(100, 7000, 1, 100, new java.util.Random(), 1, 1)  
  MapSpecification.check(params)
}
