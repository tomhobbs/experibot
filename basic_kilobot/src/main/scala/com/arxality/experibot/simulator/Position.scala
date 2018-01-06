package com.arxality.experibot.simulator

import com.arxality.experibot.logging.Loggable
import org.bson.Document

class Position(val x: Int, val y: Int) extends Loggable {
  
  override def toString: String = s"(x=$x, y=$y)"
  
  override def toDocument(): Document = {
    new Document()
         .append("x", x)
         .append("y", y)
  }
  
  def diff(p: Position): Double = {
    Math.abs(
      if(x == p.x) y - p.y
      else if(y == p.y) x - p.x
      else {
        val dX = x - p.x
        val dY = y - p.y
        Math.sqrt((dX*dX) + (dY*dY))
      }
    )
  }
  
}