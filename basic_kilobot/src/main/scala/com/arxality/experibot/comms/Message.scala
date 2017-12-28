package com.arxality.experibot.comms

import com.arxality.experibot.simulator.Robot
import com.arxality.experibot.simulator.World

case class DeliveryManifest(sender: Int, msg: Message, recipient: Int)
case class DeliveryResponse(manifest: DeliveryManifest, success: Boolean)

abstract class Message(val id: Int, val senderId: Int, val recipientIds: Option[Seq[Int]]) {
	
  def isFor(w: World, r: Robot): Boolean
  
}
