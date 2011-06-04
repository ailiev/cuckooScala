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

import org.scalacheck.Commands
import org.scalacheck.Prop
import org.scalacheck.Gen
import org.scalacheck.Gen._
import org.scalacheck.Arbitrary._

import scala.collection.immutable.HashMap
import scala.collection.immutable.Map

import scala.collection.mutable.{Map => MutableMap}

import java.lang.Long

/** ScalaCheck specification for a mutable Scala map. */
class MapSpecification (htable:MutableMap[Long,Int])
extends Commands with util.Slf4JLogger
{
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
    State(new HashMap[Long,Int])
  }

  // We define our commands as subtypes of the traits Command or SetCommand.
  // Each command must have a run method and a method that returns the new
  // abstract state, as it should look after the command has been run.
  // A command can also define a precondition that states how the current
  // abstract state must look if the command should be allowed to run.
  // Finally, we can also define a postcondition which verifies that the
  // system under test is in a correct state after the command exectution.

  case class Update(key:Long, value:Int) extends Command {
	debug(this.toString)

    def run(s: State) = {
      info("Doing " + this)
      htable.update(key, value)
      htable
    }

    def nextState(s: State) = State(s.mappings + ((key, value)))

    postConditions += {
      case (s0, s1, table:MutableMap[Long,Int])	=>
        Prop.propBoolean(table.size == s1.mappings.size) :|
          "Table size: expected %d actual %d".format(s1.mappings.size, table.size)
      case _						=> false
    }
  }

  /** Insert a new entry. */
  case class Insert(override val key:Long, override val value:Int)
  extends Update(key,value)
  {
    preConditions += {
      case (State(mappings)) => ! mappings.contains(key)
    }
  }

  case class Get(key:Long) extends Command {
	debug(this.toString)

    def run(s: State) = { info("Doing " + this); htable.get(key) }
    def nextState(s: State) = s

    postConditions += {
      case (s0, s1, r:Option[Int])	=> r == s1.mappings.get(key)
      case _						=> false
    }
  }

  /** Get on an absent key. */
  case class GetAbsent(override val key:Long) extends Get(key) {
    preConditions += {
      case (State(mappings)) => ! mappings.contains(key)
    }    
  }

  case class Remove(key:Long) extends Command {
    def run(s: State)       = { info("Doing {}", this); htable -= key; htable }
    def nextState(s: State) = State(s.mappings - key)

    postConditions += {
      case (s0, s1, table:MutableMap[Long,Int]) => ! table.contains(key)
      case _                                    => false
    }

    postConditions += {
      case (s0, s1, table:MutableMap[Long,Int]) => table.size == s1.mappings.size
      case _                                    => false
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
    Gen.frequency( (10, genCommand_get(s.mappings)),
                   (10, genCommand_put(s.mappings)),
                   (1,  genCommand_remove(s.mappings)) )

  def genCommand_get (mappings:Map[Long,Int]) : Gen[Get] =
    frequency ( (19, genExistingKey(mappings).map(Get)),
                (1,  genKey.map(GetAbsent)) )

  def genCommand_put (mappings:Map[Long,Int]) : Gen[Update] =
    frequency ( (9, randInsert),
    			(1, randUpdate(mappings)) )

  def genCommand_remove (mappings:Map[Long,Int]) : Gen[Remove] =
    frequency ( (19, genExistingKey(mappings).map(Remove)),
                (1,  genKey.map(Remove)) )

  def randInsert = for {
    key <- genKey
    value <- genValue
  } yield (Insert(key,value))

  // scalacheck doesn't want to work with the whole number range, so trimming it some.
  def genKey = choose(Math.MIN_LONG/4, Math.MAX_LONG/2).map(Long.valueOf)
  def genValue = choose(Math.MIN_INT/4, Math.MAX_INT/2)

  /** An update of a random existing key */
  def randUpdate (mappings:Map[Long,Int]) = for {
      key <- genExistingKey(mappings)
      value <- genValue
  } yield (Update(key,value))

  /** A random key from the given map */
  def genExistingKey[T:ClassManifest] (mappings:Map[T,_]) : Gen[T] = {
    val arr = mappings.keySet.toArray
    oneOf(arr)
  }

  // a variation of Gen.oneOf
  def oneOf[T](gs: Array[T]) : Gen[T] = if(gs.isEmpty) fail else for {
    i <- choose(0,gs.length-1)
    x <- gs(i)
  } yield x

}
