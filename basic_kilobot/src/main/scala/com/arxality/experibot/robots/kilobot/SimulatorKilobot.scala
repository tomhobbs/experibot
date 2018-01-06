package com.arxality.experibot.robots.kilobot

import com.arxality.experibot.simulator.World
import com.arxality.experibot.simulator.Position
import com.arxality.experibot.comms.Message
import com.typesafe.scalalogging.LazyLogging
import org.slf4j.MDC

import com.arxality.experibot.logging.Loggable
import org.bson.Document
import ch.qos.logback.classic.Level

class DebugableKilobot(val role: String,    
                       val id: Int,   
                       val pos: Position,   
                       kilobot: Kilobot) 
                       extends LazyLogging with Loggable {
  
  MDC.put("robot_id", id.toString())
  MDC.put("robot_role", role)
  
  def this(role: String = "Kilobot", 
           id: Int,
           pos: Position,
           botBuilder: () => Kilobot) = {
    
    
    this(role, id, pos, botBuilder())
  }

  def evolve(event: String, kilobot: Kilobot): DebugableKilobot = {
    val evolved = new DebugableKilobot(role, id, pos, kilobot)
    logger.info(event, evolved)
    evolved
  }
           
  override def toDocument(): Document = {
    new Document()
            .append("robot_id", id.toString())
            .append("pos", pos.toDocument())
            .append("robot_role", role)
            .append("robot", kilobot.toDocument())
  }
           
  def init(): DebugableKilobot = {
    val ready = kilobot.setup()
    evolve("init", ready)
  }
  
  def nextMsgId(): Int = {
    0  // TODO
  }
  
  def isInRange(x: DebugableKilobot): Boolean = {
    val dist = pos.diff(x.pos)
    return dist <= Kilobot.COMMS_RANGE
  }
   
  def tick: DebugableKilobot = {
    // TODO - support movement!
    evolve("tick", kilobot.loop())
  }

  def toSend(): Option[DebuggableKilobotMessage] = {
     kilobot.out().map(m => new DebuggableKilobotMessage(nextMsgId(), id, None, m.msgType, m.data))
  }
      
  def deliver(world: World, msg: Message): (Boolean, DebugableKilobot) = {
    
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
  
  def hasMessageToSend(): Boolean = {
    kilobot.out().isDefined
  }
  
  def getMessagesToSend(world: World): Seq[Message] = {
//    log(s"MSG OUT => ${kilobot.out()}")
    
    // Kilobots can only send one message at a time and only broadcast
    kilobot.out()
      .map(m => Seq(new DebuggableKilobotMessage(nextMsgId(), id, None, m.msgType, m.data)))
      .getOrElse(Seq())
  }
  
  override def toString(): String = {
    s"DebugableKilobot[id: $id, pos: $pos, robot: $kilobot]"
  }

  def delivered(success: Boolean, msgIds: Seq[Int]): DebugableKilobot = {
   evolve("delivered", kilobot.transmissionSuccess())
 }

}