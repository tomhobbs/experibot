import World.Position
import Message.In

object Experiment1 {
  
  class Skin(id: Int, pos: Position, toReceive: Option[In] = None, toSend: Option[In] = None) extends Kilobot(id, pos, toReceive, toSend) {
    
    def loop(): Kilobot = {
      log("Looping")
      new Skin(id, pos)
    }

    def receive(msg: Message.In): Kilobot = {
      log(s"Received --> $msg")
      new Skin(id, pos)
    }

    def role(): String = " SKIN"

    def toSend(): Option[Message.Out] = {
      None
    }
    
  }
  
  class Plasm(id: Int, pos: Position, toReceive: Option[In] = None, toSend: Option[In] = None) extends Kilobot(id, pos, toReceive, toSend) {
    
    def loop(): Kilobot = {
      log("Looping")
      new Plasm(id, pos)
    }

    def receive(msg: Message.In): Kilobot = {
      log(s"Received --> $msg")
      new Plasm(id, pos)
    }

    def role(): String = "PLASM"

    def toSend(): Option[Message.Out] = {
      None
    }
    
  }
  
}