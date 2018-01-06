package com.arxality.experibot.simulator

import com.arxality.experibot.robots.kilobot.DebugableKilobot

trait RobotService {
  
  def streamRobots(): Seq[DebugableKilobot]
  
  def findRobot(id: Int): Option[DebugableKilobot]
  
  def findRobots(f: (DebugableKilobot => Boolean)): Seq[DebugableKilobot]
  
}