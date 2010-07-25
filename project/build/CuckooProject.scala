import sbt._

class CuckooProject(info: ProjectInfo) extends DefaultProject(info)
{
  val slfApi = "org.slf4j" % "slf4j-api" % "1.6.+"
  val slfBack = "org.slf4j" % "slf4j-simple" % "1.6.+"

//  val scalacheck = "org.scala-tools.testing" % "scalacheck" % "1.7"
  val scalatest = "org.scalatest" % "scalatest" % "1.2"

  val junit = "junit" % "junit" % "4.8.+"

  override def ivyXML =
    <dependencies>
      <dependency org="javax.activation" name="activation" rev="1.0.2"/>
      <dependency org="com.sun.xml.fastinfoset" name="FastInfoset" rev="1.2.7"/>
      <dependency org="com.sun.japex.jdsl" name="jdsl" rev="1.0.25"/>
<!--      <dependency org="javax.xml" name="jsr173" rev="1.0"/> -->
      <dependency org="xml-resolver" name="xml-resolver" rev="1.2"/>
      <dependency org="com.sun.xml.stream" name="sjsxp" rev="1.0.1"/>
      <dependency org="org.jvnet.staxex" name="stax-ex" rev="1.2"/>
      <dependency org="com.sun.xml.stream.buffer" name="streambuffer" rev="0.8"/>
      <dependency org="xerces" name="xercesImpl" rev="2.9.1"/>
      <dependency org="jfree" name="jcommon" rev="1.0.15"/>
      <dependency org="jfree" name="jfreechart" rev="1.0.12"/>
    </dependencies>
}
