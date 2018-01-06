package com.arxality.experibot.simulator

import com.arxality.experibot.kilobot.DebugableKilobot
import com.arxality.experibot.kilobot.KilobotMessage
import com.arxality.experibot.kilobot.DebuggableKilobotMessage

trait RobotService {
  
  def nextTick(): RobotService
  
  def completeTick(): RobotService
  
  def streamRobots(f: (DebugableKilobot => DebugableKilobot)) : RobotService
  
  def collectOutgoingMessages(f: (DebugableKilobot => Option[DebuggableKilobotMessage])): RobotService
  
  def deliverAllMessages(f: ((DebugableKilobot, Int) => DebugableKilobot)): RobotService
  
}