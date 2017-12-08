package com.arxality.experibot.robots.kilobot.example

import com.arxality.experibot.robots.kilobot.Kilobot
import com.arxality.experibot.robots.kilobot.KilobotMessage
import com.arxality.experibot.robots.kilobot.RGB

object FireflyMessage {
  val FLASH: Short = 20
  
  def flash(c: RGB): KilobotMessage = {
    KilobotMessage(FLASH, Some(Array(c.red, c.green, c.blue)))
  }
  
}

class Firefly(log: (String) => Unit, 
              val me: Short, 
              val colour: RGB,
              val toSend: Option[KilobotMessage] = None,
              val received: Option[KilobotMessage] = None,
              val transmission_success: Boolean = false,
              val light_on: Boolean = false,
              val ticks: Int = 0) extends Kilobot(log) { 
  
  def this(log: (String) => Unit) = this(log, 0, RGB(0,0,0))
  
  override def setup(): Firefly = {
    val me = Kilobot.rand_soft()
    val colour = rand_colour()
    log(s"Setup - $me - $colour")
    new Firefly(log, me, colour)
  }
  
  def in(m: KilobotMessage, dist: Double): Kilobot = {
    log(s"Received: $m")
    new Firefly(log, me, colour, toSend, Some(m), transmission_success)
  }

  // Deliberately writing to procedural style to make translation to C++ easier
  def loop(): Kilobot = {
    log("Looping")
    
    if(received_message()) {
      log("Handle received message")
      this
    } else {
      log("No new message")
      if(light_on) {
        new Firefly(log, me, colour, toSend, received, transmission_success, false)
      } else if(should_flash()) {
        log(s"Flashing: $colour")
        val out = FireflyMessage.flash(colour)
        new Firefly(log, me, colour, Some(out), received, transmission_success, true)
      } else {
        this
      }
    }
    
  }

  def received_message(): Boolean = {
    received.map(_ => true).getOrElse(false)
  }
  
  def out(): Option[KilobotMessage] = {
    toSend
  }

  def setColour(new_colour: RGB): Kilobot = {
    log("Setting new colour")
    new Firefly(log, me, new_colour, toSend, received)
  }

  def transmissionSuccess(): Kilobot = {
    log("Message delivered")
    new Firefly(log, me, colour, None, received, true)
  }

  // 50:50 chance of flashing
  def should_flash(): Boolean = {
    0 == Kilobot.rand_soft() % 1 // TODO change back: 2
  }
  
  def rand_colour(): RGB = {
    val r = rand_colour_component()
    val g = rand_colour_component()
    val b = rand_colour_component()
    new RGB(r,g,b)
  }
  
  def rand_colour_component(): Short = {
    val r = Kilobot.rand_soft()
    if (r <= 85) 0
    else if (r <= 170) 1
    else 2
  }
  
  override def toString(): String = {
    s"Firefly[colour: $colour, toSend: $toSend, received: $received]"
  }
}