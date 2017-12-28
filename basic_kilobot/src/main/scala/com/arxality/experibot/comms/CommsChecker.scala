package com.arxality.experibot.comms

import com.arxality.experibot.simulator.Robot
import com.arxality.experibot.simulator.RobotService

trait CommsChecker extends RobotService {
  
  def inCommsRange(r: Robot): Option[Seq[Robot]] = {
    val inRange = streamRobots().filter(x => { r.id != x.id && r.isInRange(x) })
    Option(inRange).filter(_.nonEmpty)
  }
 
  def inCommsRange(rId: Int): Option[Seq[Robot]] = {
    val subject = findRobot(rId)
    subject.map(r => streamRobots().filter { x => rId != x.id && r.isInRange(x) } )
  }
  
}