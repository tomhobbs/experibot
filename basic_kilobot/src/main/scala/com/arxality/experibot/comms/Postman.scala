package com.arxality.experibot.comms

import com.arxality.experibot.simulator.World
import com.arxality.experibot.robots.kilobot.DebugableKilobot

trait Postman {
  
  def deliver(world: World, msgs: Message): (Boolean, DebugableKilobot)
  
}