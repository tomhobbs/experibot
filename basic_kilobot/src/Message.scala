import java.util.UUID


abstract class Message(val id: Int, val senderId: Int, val recipientIds: Option[Seq[Int]]) {
	
  def isFor(w: World, r: Robot): Boolean
  
}
