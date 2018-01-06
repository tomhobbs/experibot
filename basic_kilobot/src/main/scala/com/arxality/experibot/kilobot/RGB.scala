package com.arxality.experibot.kilobot

import com.arxality.experibot.logging.Loggable
import org.bson.Document

case class RGB(red: Short, green:Short, blue: Short) extends Loggable {
  override def toDocument(): Document  = {
    new Document()
        .append("r", red)
        .append("g", green)
        .append("b", blue)
  }
}