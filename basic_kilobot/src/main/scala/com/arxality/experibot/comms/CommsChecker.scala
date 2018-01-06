package com.arxality.experibot.comms

import com.arxality.experibot.simulator.RobotService
import com.arxality.experibot.robots.kilobot.DebugableKilobot

trait CommsChecker extends RobotService {
  
  def inCommsRange(r: DebugableKilobot): Option[Seq[DebugableKilobot]] = {
    val inRange = streamRobots().filter(x => { r.id != x.id && r.isInRange(x) })
    Option(inRange).filter(_.nonEmpty)
  }
 
  def inCommsRange(rId: Int): Option[Seq[DebugableKilobot]] = {
    val subject = findRobot(rId)
    subject.map(r => streamRobots().filter { x => rId != x.id && r.isInRange(x) } )
  }
  
}