package com.arxality.experibot.comms

import com.arxality.experibot.simulator.Robot
import com.arxality.experibot.simulator.World

abstract class Message(val id: Int, val senderId: Int, val recipientIds: Option[Seq[Int]]) {
	
  def isFor(w: World, r: Robot): Boolean
  
}
