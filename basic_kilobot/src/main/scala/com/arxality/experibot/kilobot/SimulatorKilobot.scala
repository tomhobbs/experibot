package com.arxality.experibot.kilobot

import com.arxality.experibot.simulator.Position
import com.typesafe.scalalogging.LazyLogging
import org.slf4j.MDC

import com.arxality.experibot.logging.Loggable
import org.bson.Document
import ch.qos.logback.classic.Level
import com.arxality.experibot.simulator.RobotService

class DebugableKilobot(val role: String,    
                       val id: Int,   
                       val pos: Position,   
                       kilobot: Kilobot) 
                       extends LazyLogging 
                       with Loggable {
  
  def this(role: String = "Kilobot", 
           id: Int,
           pos: Position,
           botBuilder: () => Kilobot) = {
    
    
    this(role, id, pos, botBuilder())
  }
  
  def wrap(kilobot: Kilobot) = new DebugableKilobot(role, id, pos, kilobot)

  override def toDocument(): Document = {
    new Document()
            .append("robot_id", id.toString())
            .append("pos", pos.toDocument())
            .append("robot_role", role)
            .append("robot", kilobot.toDocument())
  }
           
  def init(): DebugableKilobot = {
    val ready = kilobot.setup()
    logger.info("init", ready)
    wrap(ready)
  }
  
  def tick(): DebugableKilobot = {
    val next = wrap(kilobot.loop())
    logger.info("tick", next)
    next
  }
  
  def transmissionSuccess(): DebugableKilobot = {
    val next = wrap(kilobot.transmissionSuccess())
    logger.info("transmission_success", next)
    next
  }
  
  def toSend(): Option[DebuggableKilobotMessage] = {
    val raw = kilobot.out()
    raw.map(m => {
      val out = new DebuggableKilobotMessage(DebugableKilobot.nextMsgId(), id, m.msgType, m.data)
      logger.info("sending", this)
      out
    })
  }
  
  def deliver(msg: DebuggableKilobotMessage, dist: Double): DebugableKilobot = {
    val next = wrap(kilobot.in(msg.toRaw(), dist))     
    logger.info("received_msg", next)
    next
  }

}

object DebugableKilobot {
  
  var msgIds: Int = 1;
  
  def nextMsgId(): Int = {
    msgIds = msgIds + 1
    msgIds
  }
  
  def areInRange(a: DebugableKilobot, b: DebugableKilobot): Boolean = {
    a.pos.diff(b.pos) <= Kilobot.COMMS_RANGE
  }
}