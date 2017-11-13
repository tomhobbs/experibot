
class Message {
	
}

object Message {
  
  type Payload = (Short, Short, Short, Short, Short, Short, Short, Short, Short)

	case class Out(msgType: Short, data: Payload) extends Message
	
	case class In(msgType: Short, data:Payload, distance: Double) extends Message
  
}
