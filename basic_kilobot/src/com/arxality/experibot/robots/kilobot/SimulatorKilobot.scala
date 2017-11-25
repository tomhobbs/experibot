package com.arxality.experibot.robots.kilobot

import com.arxality.experibot.simulator.World
import com.arxality.experibot.simulator.Robot
import com.arxality.experibot.simulator.Position
import com.arxality.experibot.comms.Message

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
    val recipients = w.findRobot(senderId).map(r => w.inCommsRange(r) )
    recipients.contains(r)
  }
  
  def toRaw(): KilobotMessage = {
    new KilobotMessage(msgType, data);
  }
}

class DebugableKilobot(val role: String = "Kilobot", 
                       id: Int,
                       pos: Position,
                       kilobot: Kilobot)
                       extends Robot(id, pos) {

  def copyWith(kb: Kilobot): DebugableKilobot = {
    new DebugableKilobot(role, id, pos, kb)
  }
  
  def nextMsgId(): Int = {
    0  // TODO
  }
  
  override def isInRange(x: Robot): Boolean = {
    val dist = pos.diff(x.pos)
    return dist <= Kilobot.COMMS_RANGE
  }
   
  override def tick: DebugableKilobot = {
    // TODO - support movement!
    val next = kilobot.loop()
    new DebugableKilobot(role, id, pos, next)
  }
  
  def debug(w: World): Unit = {
    val inMyRange = w.inCommsRange(this).map { x => x.id }
    log(s"$pos  In range of: $inMyRange")
  }

  def toSend(): Option[DebuggableKilobotMessage] = {
     kilobot.out().map(m => new DebuggableKilobotMessage(nextMsgId(), id, None, m.msgType, m.data))
  }
      
  def deliver(world: World, msgs: Seq[Message]): (Boolean, Robot) = {
    if(msgs.isEmpty) (true, this)
    else {
      // TODO - Choose a message, and deliver it (or decide delivery failed)
      val toDeliver = msgs.flatMap(m => {
        m match {
          case _: DebuggableKilobotMessage => Some(m.asInstanceOf[DebuggableKilobotMessage])
          case _ => None
        }
      }).headOption
      
      toDeliver.map(m => {
        val sender = world.findRobot(m.senderId)
        sender.map(r => {
          val dist = pos.diff(r.pos)
          val kb = kilobot.in(m.toRaw(), dist)
          (true, copyWith(kb))
        })
      }).flatten.getOrElse( (false, this) ) 
    }
  }
  
  def delivered(success: Boolean, msgIds: Seq[Int]): DebugableKilobot = {
    if(msgIds.isEmpty) this
    else new DebugableKilobot(role, id, pos, kilobot.transmissionSuccess())
  }
  
  def getMessagesToSend(): Option[Seq[Message]] = {
    kilobot.out()
      .map(m => new DebuggableKilobotMessage(nextMsgId(), id, None, m.msgType, m.data))
      .map(Seq(_)) // Kilobots can only send one message at a time
  }
}