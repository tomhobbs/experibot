package com.arxality.experibot.comms

import com.arxality.experibot.simulator.Robot
import com.arxality.experibot.simulator.World

trait Postman {
  
  def deliver(world: World, msgs: Message): (Boolean, Robot)
  
}