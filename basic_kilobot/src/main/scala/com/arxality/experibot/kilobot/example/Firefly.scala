package com.arxality.experibot.kilobot.example

import com.arxality.experibot.kilobot.Kilobot
import com.arxality.experibot.kilobot.KilobotMessage
import com.arxality.experibot.kilobot.RGB
import java.util.regex.Pattern.LazyLoop
import com.typesafe.scalalogging.LazyLogging
import org.bson.Document
import com.arxality.experibot.logging.Loggable
import ch.qos.logback.classic.Level

object FireflyMessage {
  val FLASH: Short = 20
  
  def flash(c: RGB): KilobotMessage = {
    KilobotMessage(FLASH, Some(Array(c.red, c.green, c.blue)))
  }
  
}

class ColourMemory(val red: (Short,Short,Short), 
                   val green: (Short,Short,Short), 
                   val blue: (Short,Short,Short)) extends Loggable {
  
  def this() = this((0,0,0), (0,0,0), (0,0,0))
  
  def remember(c: RGB): ColourMemory = {
    
    def shortInc(a: Int): Short = (a + 1).toShort
    
    def inc(i: Short, t: (Short,Short,Short)): (Short,Short,Short) = {
      if(0 == i) (shortInc(t._1), t._2, t._3)
      else if(1 == i) (t._1, shortInc(t._2), t._3)
      else (t._1, t._2, shortInc(t._3))
    }
    
    val r = inc(c.red, red)
    val g = inc(c.green, green)
    val b = inc(c.blue, blue)
    
    new ColourMemory(r,g,b)
  }
  
  override def toDocument(): Document = {
    new Document()
      .append("red", red)
      .append("green", green)
      .append("blue", blue)
  }
}

class Firefly(val me: Short, 
              val colour: RGB,
              val toSend: Option[KilobotMessage] = None,
              val received: Option[KilobotMessage] = None,
              val transmission_success: Boolean = false,
              val light_on: Boolean = false,
              val ticks: Int = 0,
              val setupComplete: Boolean = false,
              val memory: ColourMemory = new ColourMemory()) extends Kilobot with LazyLogging with Loggable { 
  
  def this() = this(0, RGB(0,0,0))
  
  override def setup(): Firefly = {
    val me = Kilobot.rand_soft()
    val colour = rand_colour()
    new Firefly(me, colour, None, None, false, false, 0, true)
  }
  
  override def toDocument(): Document  = {
    
    val doc = (toSend, received) match {
      case (Some(out), Some(in)) => {
        new Document()
          .append("msg_in", in.toDocument())
          .append("msg_out", out.toDocument())
      }
      case (None, Some(in)) => new Document("msg_in", in.toDocument())
      case (Some(out), None) => new Document("msg_out", out.toDocument())
      case (None, None) => new Document()
    }
    
    doc
      .append("colour", colour.toDocument())
      .append("transmission_success", transmission_success)
      .append("light_on", light_on)
      .append("ticks", ticks)
      .append("setupComplete", setupComplete)
      .append("memory", memory.toDocument())

  }
  
  def in(m: KilobotMessage, dist: Double): Kilobot = {
    new Firefly(me, 
                colour, 
                toSend, 
                Some(m), 
                transmission_success, 
                light_on, 
                ticks, 
                setupComplete, 
                memory)
  }

  def loop(): Kilobot = {
    if(!setupComplete) {
      logger.error("loop is called without setup being complete")
      this
    } else {
      
    if(received_message()) {
      received.map(m => {
        m.msgType match {
          case FireflyMessage.FLASH => {
            var adapt = adaptColour(toRGB(m.data))
            val newColour = adapt._2
            val newMemory = adapt._1
            val out = FireflyMessage.flash(newColour)
            val ff = new Firefly(me, 
                                 newColour, 
                                 Some(out), 
                                 received, 
                                 transmission_success, 
                                 true, 
                                 ticks+1, 
                                 true, 
                                 newMemory)
            logger.info(s"Flashing new colour $newColour")
            ff
          }
          case _ => {
            val ff = new Firefly(me, 
                                 colour, 
                                 toSend, 
                                 None, 
                                 false, 
                                 light_on, 
                                 ticks+1, 
                                 true, 
                                 memory)
            logger.warn(s"Unknown message type: ${m.msgType}")
            ff
          }
        }
      }).getOrElse(this)
    } else {
      logger.debug("No new message")
      if(light_on) {
        new Firefly(me, 
            colour, 
            toSend, 
            received, 
            transmission_success, 
            false, 
            ticks+1, 
            true, 
            memory)
      } else if(should_flash()) {
        val out = FireflyMessage.flash(colour)
        val ff = new Firefly(me, 
                             colour, 
                             Some(out), 
                             received, 
                             transmission_success, 
                             true, 
                             ticks+1, 
                             true, 
                             memory)
        logger.info(s"Flashing: $colour")
        ff
      } else {
        this
      }
    }
    }
    
  }

  def received_message(): Boolean = {
    received.map(_ => true).getOrElse(false)
  }
  
  def out(): Option[KilobotMessage] = {
    toSend
  }

  def setColour(c: RGB): Firefly = {
    new Firefly(me, 
                c, 
                out, 
                received, 
                transmission_success, 
                light_on, 
                ticks, 
                setupComplete, 
                memory)
  }
  
  def transmissionSuccess(): Kilobot = {
    val ff = new Firefly(me, 
                         colour, 
                         None, 
                         received, 
                         true, 
                         light_on, 
                         ticks, 
                         true, 
                         memory)
    logger.debug("Message delivery succes")
    ff
  }

  // 50:50 chance of flashing
  def should_flash(): Boolean = {
    0 == Kilobot.rand_soft() % 2
  }
  
  def rand_colour(): RGB = {
    val r = rand_colour_component()
    val g = rand_colour_component()
    val b = rand_colour_component()
    new RGB(r,g,b)
  }
  
  def toRGB(data: Option[Seq[Short]]): RGB = {
    data.map(xs => {
      val r = xs(0)
      val g = xs(1)
      val b = xs(2)
      RGB(r,g,b)
    }).getOrElse(RGB(0,0,0))
  }
  
  def adaptColour(c: RGB): (ColourMemory, RGB) = {
    val updated = memory.remember(c)
    
    val r = if(colour.red == c.red) c.red else mostCommon(updated.red)
    val g = if(colour.green == c.green) c.green else mostCommon(updated.green)
    val b = if(colour.blue == c.blue) c.blue else mostCommon(updated.blue)
    (updated, RGB(r,g,b))
  }
  
  def mostCommon(memory: (Short,Short,Short)): Short = {
    val a = memory._1
    val b = memory._2
    val c = memory._3
    
    // In a Kilobot, max value for colour component is 3, so be sure its capped
    Math.min(3, Math.max(a, Math.max(b, c))).toShort
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