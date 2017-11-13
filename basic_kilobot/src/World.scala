

class World(robots: Seq[Robot]) {
  
  def debug(): Unit = {
    println(s"-- DEBUG WORLD --")
    println(s"Num Robots: ${robots.length}")
    
    robots.foreach { _.debug(this) }
    
    println(s"-----------------")
  }
  
  def inCommsRange(r: Robot): Seq[Robot] = {
    robots.filter { x => r.id != x.id && r.isInRange(x) }
  }
  
}

object World {
  
  case class Position(val x: Int, val y: Int) {
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
  
}