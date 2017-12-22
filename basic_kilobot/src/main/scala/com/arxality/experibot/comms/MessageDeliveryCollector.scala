package com.arxality.experibot.comms

trait MessageDeliveryCollector extends CommsChecker {
  
  private def toDelivery(msg: Message, recipient: Int) = DeliveryManifest(msg.senderId, msg, recipient)

  private def broadcastMessage(msg: Message): Option[Seq[DeliveryManifest]] = {
    val recipients = inCommsRange(msg.senderId)
    recipients.map(rs => {
      rs.map(r => DeliveryManifest(msg.senderId, msg, r.id) )
    })
  }  
  
  def collectDeliveryManifests(msgs: Seq[Message]): Option[Seq[DeliveryManifest]] = {
    val z = msgs.map(msg => {
      msg.recipientIds.map(ids => {
        val x: Seq[DeliveryManifest] = ids.map(id => toDelivery(msg, id))
        x
      }).orElse(broadcastMessage(msg))
    }).flatten.flatten
    Option(z).filter(_.nonEmpty)
  }
    
  def collectDeliveryManifests(msg: Message): Option[Seq[DeliveryManifest]] = {
    msg.recipientIds.flatMap(ids => {
        Some(ids.map(id => toDelivery(msg, id)))
      }).orElse(broadcastMessage(msg))
  }
}