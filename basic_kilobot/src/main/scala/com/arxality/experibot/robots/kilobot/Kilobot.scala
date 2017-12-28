package com.arxality.experibot.robots.kilobot

import com.arxality.experibot.simulator.Robot
import com.arxality.experibot.comms.Message
import com.arxality.experibot.simulator.World
import com.arxality.experibot.simulator.Position
import com.arxality.experibot.logging.Loggable
import org.bson.Document
import com.typesafe.scalalogging.LazyLogging

object Kilobot {
  val COMMS_RANGE = 1;
  
  val rand = scala.util.Random
  
  def rand_soft(): Short = {
    rand.nextInt(255).toShort
  }
}

case class KilobotMessage(msgType: Short, data: Option[Seq[Short]]) extends Loggable {

  override def toDocument(): Document = {
    val doc = data.map(d => new Document("data", d)).getOrElse(new Document())
    doc.append("type", msgType)
  }
}

case class RGB(red: Short, green:Short, blue: Short) extends Loggable {
  override def toDocument(): Document  = {
    new Document()
        .append("r", red)
        .append("g", green)
        .append("b", blue)
  }
}

abstract class Kilobot extends LazyLogging with Loggable {
  
  def setup(): Kilobot = {
    this
  }
  
  def in(m: KilobotMessage, dist: Double): Kilobot
  def out(): Option[KilobotMessage]
  def loop(): Kilobot
  def setColour(colour: RGB): Kilobot
  def transmissionSuccess(): Kilobot
  
  def info[R <: Kilobot](msg: String, kb: R): R = {
    logger.info(msg, kb)
    kb
  }
  
  def warn[R <: Kilobot](msg: String, kb: R): R = {
    logger.warn(msg, kb)
    kb
  }
  
  def error[R <: Kilobot](msg: String, kb: R): R = {
    logger.error(msg, kb)
    kb
  }
    
  def debug[R <: Kilobot](msg: String, kb: R): R = {
    logger.debug(msg, kb)
    kb
  }
}

