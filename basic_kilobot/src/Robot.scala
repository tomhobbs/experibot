//import Message.Out
//import Message.In
import World.Position

/**
 * Abstract because it makes no sense (in the physical world) for a certain
 * implementation to be more than one kind of Robot.  Given this is a very
 * simple comms-only simulator, it's unlikely that we'll want to mix in any
 * other common behaviours
 * 
 * @param id - Should be used for simulator/debug only.  Not to be used for swarm algos
 */
abstract class Robot(val id: Int, val pos: Position) extends Postman {
  
  def role(): String
  
  def log(msg: String) = {
    println(s"[$id]   [$role]   $msg");
  }
  
  def debug(w: World): Unit

  def isInRange(x: Robot): Boolean = {
    val dist = pos.diff(x.pos)
    return dist <= Kilobot.COMMS_RANGE
  }
  
  def tick(): Robot
  
  def getMessagesToSend(): Option[Seq[Message]]


}
