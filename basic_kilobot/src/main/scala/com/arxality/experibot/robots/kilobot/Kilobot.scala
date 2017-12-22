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

case class KilobotMessage(msgType: Short, data: Option[Array[Short]])

case class RGB(red: Short, green:Short, blue: Short)

abstract class Kilobot(log: (String) => Unit) {
  def setup(): Kilobot = {
    this
  }
  
  def in(m: KilobotMessage, dist: Double): Kilobot;
  def out(): Option[KilobotMessage]
  def loop(): Kilobot;
  def setColour(colour: RGB): Kilobot;
  def transmissionSuccess(): Kilobot;
  
}

