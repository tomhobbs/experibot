package com.arxality.experibot.kilobot

import com.arxality.experibot.simulator.Position
import com.arxality.experibot.logging.Loggable
import org.bson.Document
import com.typesafe.scalalogging.LazyLogging
import ch.qos.logback.classic.Level

object Kilobot {
  val COMMS_RANGE = 1.0;
  
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

abstract class Kilobot extends Loggable {
  
  def setup(): Kilobot
  
  def in(m: KilobotMessage, dist: Double): Kilobot
  def out(): Option[KilobotMessage]
  def loop(): Kilobot
  def setColour(colour: RGB): Kilobot
  def transmissionSuccess(): Kilobot

}

