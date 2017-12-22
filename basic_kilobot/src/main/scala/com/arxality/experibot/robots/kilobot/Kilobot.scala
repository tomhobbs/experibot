package com.arxality.experibot.robots.kilobot

import com.arxality.experibot.simulator.Robot
import com.arxality.experibot.comms.Message
import com.arxality.experibot.simulator.World
import com.arxality.experibot.simulator.Position

object Kilobot {
  val COMMS_RANGE = 1;
  
  val rand = scala.util.Random
  
  def rand_soft(): Short = {
    rand.nextInt(255).toShort
  }
}

case class KilobotMessage(msgType: Short, data: Option[Array[Short]]) {
  def loggableValue(): java.util.Map[String,Any] = {
    val map = new java.util.HashMap[String,Any]();
    map.put("type", msgType)
    data.map(d => { map.put("data", d.toSeq) })
    return map
  }
}

case class RGB(red: Short, green:Short, blue: Short) {
  def loggableValue(log: java.util.Map[String, Any]): Unit  = {
    val map = new java.util.HashMap[String, Short]()
    map.put("r", red)
    map.put("g", green)
    map.put("b", blue)
    log.put("colour", map)
  }
}

abstract class Kilobot {
  
  def setup(): Kilobot = {
    this
  }
  
  def in(m: KilobotMessage, dist: Double): Kilobot;
  def out(): Option[KilobotMessage]
  def loop(): Kilobot;
  def setColour(colour: RGB): Kilobot;
  def transmissionSuccess(): Kilobot;
  def loggableValue(log: java.util.Map[String, Any])
}

