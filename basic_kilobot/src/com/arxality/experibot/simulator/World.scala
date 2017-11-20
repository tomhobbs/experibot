package com.arxality.experibot.simulator

class Position(val x: Int, val y: Int) {
  override def toString: String = s"(x=$x, y=$y)"
  
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

class World(generation: Int = 0, robots: Seq[Robot]) {
  
  def debug(): World = {
    println(s"-- DEBUG WORLD (Gen: $generation) --")
    println(s"Num Robots: ${robots.length}")
    
    robots.foreach { _.debug(this) }
    
    println(s"-----------------")
    
    this
  }
  
  def debug(xs: Seq[(Boolean, Robot)]): Unit = {
    
  }
  
  def inCommsRange(r: Robot): Seq[Robot] = {
    robots.filter { x => r.id != x.id && r.isInRange(x) }
  }
  
  /**
   * Tick/Heatbeat approach is to;
   * 1. Gather all the messages that need transmitting
   * 2. Cause all the robots to tick/loop
   * 3. Deliver all the messages
   * 
   * 2. and 3. could be swapped around, if so desired.  The choice to put them
   * this way around was made arbitrarily.
   */
  def tick(): World = {
    val toDeliver = robots.map(r => r.getMessagesToSend()).flatten.flatten
    
    val nextGen = robots.map(r => {
      val msgs = toDeliver.filter(m => m.isFor(this, r))
      r.tick().deliver(msgs)
    })
    
    debug(nextGen)
    new World((generation+1), nextGen.map( _._2 ))
  }

  def findRobot(id: Int): Option[Robot] = {
    robots.find(r => id == r.id)
  }
  
}

object World {
  
}