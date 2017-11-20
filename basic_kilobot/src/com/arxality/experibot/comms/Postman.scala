package com.arxality.experibot.comms

import com.arxality.experibot.simulator.Robot

trait Postman {
  
  def deliver[T <: Message](msgs: Seq[T]): (Boolean, Robot)
  
}