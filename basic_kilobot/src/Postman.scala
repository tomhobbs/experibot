trait Postman {
  
  type DeliveryResponse = Boolean
  
  def deliver[T <: Message](msgs: Seq[T]): (DeliveryResponse, Robot)
  
}