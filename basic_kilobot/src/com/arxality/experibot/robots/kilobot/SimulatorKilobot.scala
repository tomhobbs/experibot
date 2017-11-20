package com.arxality.experibot.robots.kilobot

import com.arxality.experibot.comms.Message
import com.arxality.experibot.simulator.World
import com.arxality.experibot.simulator.Robot
import com.arxality.experibot.simulator.Position

class DebuggableKilobotMessage(override val id: Int,  
                               override val senderId: Int,  
                               override val recipientIds: Option[Seq[Int]],  // For Kilobots this will always be None, since that isn't how KB comms works
                               msgType: Short,  
                               data: Array[Short]) extends Message(id, senderId, None) {
  
  /*
   * Kilobots just use broadcast, so find everything within comms range and
   * check that.
   */
  def isFor(w: World, r: Robot): Boolean = {
    val recipients = w.findRobot(senderId).map(r => w.inCommsRange(r) );
    recipients.contains(r)
  }
  
}

class DebugableKilobot(val role: String = "Kilobot", 
                       id: Int, 
                       pos:Position, 
                       toReceive: Option[DebuggableKilobotMessage] = None, 
                       toSend: Option[DebuggableKilobotMessage] = None) 
                       extends Robot(id, pos) {
  
  override def isInRange(x: Robot): Boolean = {
    val dist = pos.diff(x.pos)
    return dist <= Kilobot.COMMS_RANGE
  }
   
  def loop(): DebugableKilobot = {
    // TODO
    this
  }
  
  override def tick = loop
  
  def debug(w: World): Unit = {
    val inMyRange = w.inCommsRange(this).map { x => x.id }
    log(s"$pos  In range of: $inMyRange")
  }

  def toSend(): Option[DebuggableKilobotMessage] = {
     None
  }
      
  def deliver[T <: Message](msgs: Seq[T]): (Boolean, Robot) = {
    // TODO - Choose a message, and deliver it (or decide delivery failed)
    (false, this)
  }
  
  def receive(msg: DebuggableKilobotMessage, dist: Double): DebugableKilobot = {
    // Noop
    this
  }
  
  def getMessagesToSend(): Option[Seq[Message]] = {
    toSend.map(m => Seq(m)) // Kilobots can only send one message at a time
  }
}