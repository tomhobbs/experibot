package com.arxality.experibot.robots.kilobot

import com.arxality.experibot.simulator.World
import com.arxality.experibot.simulator.Robot
import com.arxality.experibot.simulator.Position
import com.arxality.experibot.comms.Message
import com.typesafe.scalalogging.LazyLogging
import org.slf4j.MDC

import com.arxality.experibot.simulator.World.checkpoint

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

class DebugableKilobot(val role: String,    
                       id: Int,   
                       pos: Position,   
                       kilobot: Kilobot) 
                       extends Robot(id, pos) with LazyLogging {
  
  def this(role: String = "Kilobot", 
           id: Int,
           pos: Position,
           botBuilder: () => Kilobot) = this(role, id, pos, botBuilder())

  def appendData(log: java.util.Map[String,Any]): Unit = {
    log.put("position", pos.loggableValue())
    kilobot.loggableValue(log)
  }
           
  def init(): DebugableKilobot = {
    val ready = kilobot.setup()
    MDC.put("robot_id", id.toString)
    MDC.put("robot_role", role)
    checkpoint("init", new DebugableKilobot(role, id, pos, ready))
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
    checkpoint("tick", new DebugableKilobot(role, id, pos, next))
  }

  override def debug(w: World): Unit = {
    logger.debug(s"[$id] [$role] $kilobot")
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
        (true, checkpoint("msg_in", copyWith(kb)))
      })
    }).flatten.getOrElse( (false, this) ) 
  }
  
  def delivered(success: Boolean, msgIds: Int): DebugableKilobot = {
    checkpoint("delivered", new DebugableKilobot(role, id, pos, kilobot.transmissionSuccess()))
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
                         checkpoint("delivered", this)
                       }

}