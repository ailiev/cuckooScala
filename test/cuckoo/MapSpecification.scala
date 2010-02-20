package cuckoo

import org.scalacheck.Commands
import org.scalacheck.Gen
import org.scalacheck.Gen._
import org.scalacheck.Arbitrary._

import scala.util.logging._

import scala.collection.immutable.LongMap
import scala.collection.immutable.Map

object MapSpecification extends Commands with ConsoleLogger {

  // This is our system under test. All commands run against this instance.
  val htable:scala.collection.mutable.Map[Long,Int] =
    new CHT[Long,Int](55) with ConsoleLogger;

  // This is our state type that encodes the abstract state. The abstract state
  // should model all the features we need from the real state, the system
  // under test. We should leave out all details that aren't needed for
  // specifying our pre- and postconditions. The state type must be called
  // State and be immutable.
  case class State(mappings : Map[Long,Int])

  // initialState should reset the system under test to a well defined
  // initial state, and return the abstract version of that state.
  def initialState() = {
    htable.clear
    State(LongMap.empty)
  }

  // We define our commands as subtypes of the traits Command or SetCommand.
  // Each command must have a run method and a method that returns the new
  // abstract state, as it should look after the command has been run.
  // A command can also define a precondition that states how the current
  // abstract state must look if the command should be allowed to run.
  // Finally, we can also define a postcondition which verifies that the
  // system under test is in a correct state after the command exectution.

  case class Put(key:Long, value:Int) extends Command {
	log(this.toString)

    def run(s: State) = { log("Doing " + this); htable.update(key, value) }

    def nextState(s: State) = State(s.mappings.update(key, value))

    // if we want to define a precondition, we add a function that
    // takes the current abstract state as parameter and returns a boolean
    // that says if the precondition is fulfilled or not. In this case, we
    // have no precondition so we just let the function return true. Obviously,
    // we could have skipped adding the precondition at all.
    preConditions += (s => true)
  }

  case class Get(key:Long) extends Command {
	log(this.toString)

    def run(s: State) = { log("Doing " + this); htable.get(key) }
    def nextState(s: State) = s

    postConditions += {
      case (s0, s1, r:Option[Int])	=> r == s1.mappings.get(key)
      case _						=> false
    }
  }

  // This is our command generator. Given an abstract state, the generator
  // should return a command that is allowed to run in that state. Note that
  // it is still neccessary to define preconditions on the commands if there
  // are any. The generator is just giving a hint of which commands that are
  // suitable for a given state, the preconditions will still be checked before
  // a command runs. Sometimes you maybe want to adjust the distribution of
  // your command generator according to the state, or do other calculations
  // based on the state.
  def genCommand(s: State): Gen[Command] =
    Gen.oneOf(genCommand_get(s.mappings),
              genCommand_put(s.mappings))

  def genCommand_get (mappings:Map[Long,Int]) : Gen[Get] =
    frequency ( (19, genKey(mappings).map(Get)),
                (1,  randGet) )

  def genCommand_put (mappings:Map[Long,Int]) : Gen[Put] = {
    frequency ( (9, randPut),
    			(1, repeatPut(mappings)) )
  }

  def randPut = for {
    key <- choose(0, 1234567890l)
    value <- choose(-234567, 3456789)
  } yield (Put(key,value))

  def randGet = for (key <- choose(0, 1234567890l)) yield (Get(key))

  /** An update of a random existing key */
  def repeatPut (mappings:Map[Long,Int]) = for {
      key <- genKey(mappings)
      value <- choose(-234567, 3456789)
  } yield (Put(key,value))

  /** A random key from the given map */
  def genKey[T] (mappings:Map[T,_]) = oneOf(mappings.keySet.toArray)

  // a variation of Gen.oneOf
  def oneOf[T](gs: Seq[T]) = if(gs.isEmpty) fail else for {
    i <- choose(0,gs.length-1)
    x <- gs(i)
  } yield x

}
