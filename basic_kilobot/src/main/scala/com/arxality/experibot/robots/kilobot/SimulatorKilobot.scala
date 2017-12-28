package com.arxality.experibot.robots.kilobot

import com.arxality.experibot.simulator.World
import com.arxality.experibot.simulator.Robot
import com.arxality.experibot.simulator.Position
import com.arxality.experibot.comms.Message
import com.typesafe.scalalogging.LazyLogging
import org.slf4j.MDC

import com.arxality.experibot.logging.Loggable
import org.bson.Document

class DebuggableKilobotMessage(override val id: Int,  
                               override val senderId: Int,  
                               override val recipientIds: Option[Seq[Int]],  // For Kilobots this will always be None, since that isn't how KB comms works
                               msgType: Short,  
                               data: Option[Seq[Short]]) extends Message(id, senderId, None) with Loggable {
  
  override def toDocument(): Document = {
    val doc = new Document()
                .append("msg_id", id)
                .append("sender_id", senderId)
                .append("msg_type", msgType)
    
    (recipientIds, data) match {
      case (Some(rIds), Some(bytes)) => {
        doc.append("recipient_ids", rIds).append("data", bytes)
      }
      case (_, Some(bytes)) => {
        doc.append("data", bytes)
      }
      case (Some(rIds), _) => {
        doc.append("recipient_ids",rIds)
      }
      case (None, None) => doc
    }
    
  }
  
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
                       extends Robot(id, pos) with LazyLogging with Loggable {
  
  def this(role: String = "Kilobot", 
           id: Int,
           pos: Position,
           botBuilder: () => Kilobot) = this(role, id, pos, botBuilder())

  def evolve(event: String, kilobot: Kilobot): DebugableKilobot = {
    log(event, new DebugableKilobot(role, id, pos, kilobot))
  }
           
  override def toDocument(): Document = {
    new Document()
            .append("robot_id", id)
            .append("pos", pos.toDocument())
            .append("robot_role", role)
            .append("robot", kilobot.toDocument())
  }
           
  def init(): DebugableKilobot = {
    val ready = kilobot.setup()
    evolve("init", ready)
  }
  
  def log(msg: String): Unit = {
    logger.info(msg, this)
  }
  
  def log(msg:String, kb: DebugableKilobot): DebugableKilobot = {
    logger.info(msg, kb)
    kb;
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
    evolve("tick", kilobot.loop())
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
        (true, evolve("deliver", kb))
      })
    }).flatten.getOrElse( (false, this) ) 
  }
  
  def delivered(success: Boolean, msgIds: Int): DebugableKilobot = {
    val msg = if(success) "deliver_success" else "deliver_fail"
    evolve(msg, kilobot.transmissionSuccess())
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
   evolve("delivered", kilobot.transmissionSuccess())
 }

}