package com.arxality.experibot.simulator

import com.typesafe.scalalogging.LazyLogging
import com.arxality.experibot.logging.Loggable
import org.bson.Document
import com.arxality.experibot.kilobot.DebugableKilobot
import com.arxality.experibot.kilobot.KilobotMessage
import com.arxality.experibot.kilobot.DebuggableKilobotMessage

class InMemory(generation: Int = 0, 
               robots: Seq[DebugableKilobot],
               outgoing: Seq[DebuggableKilobotMessage] = Seq()) 
    extends RobotService 
    with LazyLogging {
  
  import scala.util.Random
  
  def init(): RobotService = {
    val ready = Random.shuffle(robots).map(_.init)
    new InMemory(generation, ready)
  }
 
  def nextTick(): RobotService = {
    new InMemory(generation+1, robots)
  }
  
  def completeTick(): RobotService = {
    this
  }

  def streamRobots(f: (DebugableKilobot => DebugableKilobot)): RobotService = {
    val next = robots.map(f)
    new InMemory(generation, next)
  }
    
  def collectOutgoingMessages(f: (DebugableKilobot => Option[DebuggableKilobotMessage])): RobotService = {
    val msgs = robots.map(f).flatten
    new InMemory(generation, robots, msgs)
  }

  def distanceBetween(a: Option[DebugableKilobot], b: Option[DebugableKilobot]): Double = {
    (a, b) match {
      case (None, _) => -1.0
      case (_, None) => -1.0
      case (Some(x), Some(y)) => x.pos.diff(y.pos)
    }
  }
  
  def deliverAllMessages(f: ((DebugableKilobot, Int) => DebugableKilobot)): RobotService = {
    
    // TODO - Refactor the nasty vals out of this
    var deliveredTo = robots.map(r => (r.id, r)).toMap
    var deliveryReceipts = Map[Int, Int]()
    
    def deliver(sender: Option[DebugableKilobot], msg: DebuggableKilobotMessage, recipient: Option[DebugableKilobot]): Unit = {
      val dist = distanceBetween(sender, recipient)
      recipient.map(r => {
        val updated = r.deliver(msg, dist)
        deliveredTo += (updated.id -> updated)
        
        val count = deliveryReceipts.getOrElse(msg.senderId, 0)
        deliveryReceipts += (msg.senderId -> (count + 1))
      })
    }
        
    def deliverAll(msgs: Seq[DebuggableKilobotMessage]) = {
      msgs.foreach(msg => {
        val sender = deliveredTo.get(msg.senderId)
        findRobotsInRange(robots, msg.senderId).foreach(recipient => { deliver(sender, msg, Some(recipient)) })
      })
    }
    
    deliverAll(outgoing)
    
    deliveryReceipts.foreach(receipt => { 
      deliveredTo
        .get(receipt._1)
        .map(r => { f(r, receipt._2) })
    })
    
    new InMemory(generation, deliveredTo.values.toSeq)
  }
  
  def findRobotsInRange(robots: Seq[DebugableKilobot], id: Int): Seq[DebugableKilobot] = {
    val from = robots.find(_.id == id)
    
    from.map(f => {
      robots.filter(r => {
          r.id != id && DebugableKilobot.areInRange(f, r)
        })
    }).getOrElse(Seq())
    

  }
}
