package com.arxality.experibot.robots.kilobot

import com.arxality.experibot.simulator.World
import com.arxality.experibot.simulator.Robot
import com.arxality.experibot.simulator.Position
import com.arxality.experibot.comms.Message

class DebuggableKilobotMessage(override val id: Int,  
                               override val senderId: Int,  
                               override val recipientIds: Option[Seq[Int]],  // For Kilobots this will always be None, since that isn't how KB comms works
                               msgType: Short,  
                               data: Option[Array[Short]]) extends Message(id, senderId, None) {
  
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

object DebugableKilobot {
  def wrap(id: Int, log: (String) => Unit): (String) => Unit = {
    (msg: String) => {
      log(s"[$id]\t$msg")
    }
  }
}

class DebugableKilobot(val role: String,    
                       id: Int,   
                       pos: Position,   
                       kilobot: Kilobot) 
                       extends Robot(id, pos) {
  
  def this(role: String = "Kilobot", 
           id: Int,
           pos: Position,
           botBuilder: ((String) => Unit) => Kilobot,
           log: (String) => Unit) 
           = this(role, id, pos, botBuilder(DebugableKilobot.wrap(id, log)))

  def init(): DebugableKilobot = {
    val ready = kilobot.setup()
    new DebugableKilobot(role, id, pos, ready)
  }
  
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
//    log(s"TICK ==> $next")
    new DebugableKilobot(role, id, pos, next)
  }
  
  def debug(w: World): Unit = {
//    val inMyRange = w.inCommsRange(this).map { x => x.id }
//    log(s"$pos  In range of: $inMyRange")
  }

  def toSend(): Option[DebuggableKilobotMessage] = {
     kilobot.out().map(m => new DebuggableKilobotMessage(nextMsgId(), id, None, m.msgType, m.data))
  }
      
  def deliver(world: World, msg: Message): (Boolean, Robot) = {
    
    val toDeliver = msg match {
        case _: DebuggableKilobotMessage => Some(msg.asInstanceOf[DebuggableKilobotMessage])
        case _ => None      
    }
    
    // TODO - Choose to deliver it (or decide delivery failed)
    toDeliver.map(m => {
      val sender = world.findRobot(m.senderId)
      sender.map(r => {
        val dist = pos.diff(r.pos)
        val kb = kilobot.in(m.toRaw(), dist)
        (true, copyWith(kb))
      })
    }).flatten.getOrElse( (false, this) ) 
  }
  
  def delivered(success: Boolean, msgIds: Int): DebugableKilobot = {
    new DebugableKilobot(role, id, pos, kilobot.transmissionSuccess())
  }
  
  override def hasMessageToSend(): Boolean = {
    kilobot.out().isDefined
  }
  
  override def getMessagesToSend(world: World): Seq[Message] = {
//    log(s"MSG OUT => ${kilobot.out()}")
    
    // Kilobots can only send one message at a time and only broadcast
    kilobot.out()
      .map(m => Seq(new DebuggableKilobotMessage(nextMsgId(), id, None, m.msgType, m.data)))
      .getOrElse(Seq())
  }
  
  override def toString(): String = {
    s"DebugableKilobot[id: $id, pos: $pos, robot: $kilobot]"
  }

  def delivered(success: Boolean, msgIds: Seq[Int]): Robot = {
                         this
                       }

}