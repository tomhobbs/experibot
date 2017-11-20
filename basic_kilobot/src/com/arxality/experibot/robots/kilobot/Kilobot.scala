package com.arxality.experibot.robots.kilobot

import com.arxality.experibot.simulator.Robot
import com.arxality.experibot.comms.Message
import com.arxality.experibot.simulator.World
import com.arxality.experibot.simulator.Position

object Kilobot {
  val COMMS_RANGE = 1;
}

case class KilobotMessage(msgType: Short, data: Array[Short])

class RGB(red: Short, green:Short, blue: Short)

abstract class Kilobot(log: (String) => Unit) {
  def in(m: KilobotMessage, dist: Int): Kilobot;
  def out(): Option[KilobotMessage]
  def loop(): Kilobot;
  def setColour(colour: RGB): Kilobot;
}







