import sbt._

class CuckooProject(info: ProjectInfo) extends DefaultProject(info)
{
  val slfApi = "org.slf4j" % "slf4j-api" % "1.6.+"
  val slfBack = "org.slf4j" % "slf4j-simple" % "1.6.+"

  val scalaj_collection = "org.scalaj" %% "scalaj-collection" % "1.1" % "test"

  // scalacheck 1.9 is only built with scala 2.9.0
  val scalacheck = "org.scala-tools.testing" %% "scalacheck" % "1.9" % "test"

  val scalatest = "org.scalatest" % "scalatest" % "1.2" % "test"

  val junit = "junit" % "junit" % "4.8.+" % "test"

  override def ivyXML =
    <dependencies>
	  <dependency org="com.sun.japex" name="japex-distribution" rev="1.2.3" conf="test">
	  	<exclude org="javax.xml" name="jsr173"/>
	  </dependency>
	  <!--
      <dependency org="javax.activation" name="activation" rev="1.0.2" conf="test"/>
      <dependency org="org.apache.ant" name="ant" rev="1.8.+" conf="test"/>  
      <dependency org="com.sun.xml.fastinfoset" name="FastInfoset" rev="1.2.7" conf="test"/>
      <dependency org="com.sun.japex.jdsl" name="jdsl" rev="1.0.25" conf="test"/>
      <dependency org="javax.xml" name="jsr173" rev="1.0"/>
      <dependency org="xml-resolver" name="xml-resolver" rev="1.2" conf="test"/>
      <dependency org="com.sun.xml.stream" name="sjsxp" rev="1.0.1" conf="test"/>
      <dependency org="org.jvnet.staxex" name="stax-ex" rev="1.2" conf="test"/>
      <dependency org="com.sun.xml.stream.buffer" name="streambuffer" rev="0.8" conf="test"/>
      <dependency org="xerces" name="xercesImpl" rev="2.9.1" conf="test"/>
      <dependency org="jfree" name="jcommon" rev="1.0.15" conf="test"/>
      <dependency org="jfree" name="jfreechart" rev="1.0.12" conf="test"/>
	  -->
    </dependencies>
}
