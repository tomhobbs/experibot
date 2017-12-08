package com.arxality.experibot.simulator

trait RobotService {
  
  def streamRobots(): Seq[Robot]
  def findRobot(id: Int): Option[Robot]
  def findRobots(f: (Robot => Boolean)): Seq[Robot]
}