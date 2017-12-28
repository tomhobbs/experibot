package com.arxality.experibot.simulator

import com.arxality.experibot.comms.Postman
import com.arxality.experibot.comms.Message
import org.slf4j.MDC

/**
 * Abstract because it makes no sense (in the physical world) for a certain
 * implementation to be more than one kind of Robot.  Given this is a very
 * simple comms-only simulator, it's unlikely that we'll want to mix in any
 * other common behaviours
 * 
 * @param id - Should be used for simulator/debug only.  Not to be used for swarm algos
 */
abstract class Robot(val id: Int, val pos: Position) extends Postman {
  
  def init(): Robot
  
  def role(): String
  
  def isInRange(x: Robot): Boolean
  
  def tick(): Robot
  
  def hasMessageToSend(): Boolean
  
  def getMessagesToSend(world: World): Seq[Message]

  def delivered(success: Boolean, msgId: Int): Robot
  
  def log(msg: String): Unit
  
}
