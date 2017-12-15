package com.arxality.experibot.simulator

import com.arxality.experibot.comms.Message
import com.arxality.experibot.comms.DeliveryManifest
import com.arxality.experibot.comms.DeliveryResponse
import scala.collection.mutable.HashMap
import com.arxality.experibot.comms.MessageDeliveryCollector
import com.arxality.experibot.comms.CommsChecker

class Position(val x: Int, val y: Int) {
  override def toString: String = s"(x=$x, y=$y)"
  
  def diff(p: Position): Double = {
    Math.abs(
      if(x == p.x) y - p.y
      else if(y == p.y) x - p.x
      else {
        val dX = x - p.x
        val dY = y - p.y
        Math.sqrt((dX*dX) + (dY*dY))
      }
    )
  }
}


class World(generation: Int = 0, robots: Seq[Robot]) 
    extends MessageDeliveryCollector with CommsChecker with RobotService
{//, toDeliver: Option[Seq[Message]] = None) {
  
  import scala.util.Random
  
  def init(): World = {
    val ready = Random.shuffle(robots).map(_.init)
    new World(generation, ready)
  }
  
  def debug(): World = {
    println(s"-- DEBUG WORLD (Gen: $generation) --")
    println(s"Num Robots: ${robots.length}")
    
    robots.foreach { _.debug(this) }
    
    println(s"-----------------")
    
    this
  }
  
  def debug(xs: Seq[Robot]): Unit = {
    
  }
  
  def findRobots(f: (Robot => Boolean)): Seq[Robot] = {
    streamRobots().filter(f)
  }
  
  
  def tick(): World = {
    val updated = deliverMessages()
    val updatedIds = updated.map(_.id)
    val notUpdated = findRobots(r => { !updatedIds.contains(r.id) })
    
    val all = updated ++ notUpdated
    val nextGen = all.map(_.tick)
    
    new World((generation+1), nextGen)
  }

  def findRobot(id: Int): Option[Robot] = {
    robots.find(r => id == r.id)
  }

  def mapRobots[T](f: (Robot => T)): Seq[T] = {
    ???
  }

  //TODO actually stream (and randomise?)
  def streamRobots(): Seq[Robot] = {
    robots
  }

  /*
   * Returns all Robots that have either send messages or received them (or both)
   */
  def deliverMessages(): Seq[Robot] = {
    // TODO Replace with actual streams that wont blow the memory for large worlds
    streamRobots()
      .filter(_.hasMessageToSend)
      .map(r => {
        
        def lookup(cache: Map[Int,Robot], id: Int): Option[Robot] = {
          cache.get(id).orElse(findRobot(id))
        }
        
        val deliveryManifests = r.getMessagesToSend(this)
          .map(msg => {
            collectDeliveryManifests(msg)
          })
          .flatten.flatten
          
        val deliveryResponses = deliveryManifests
          .foldLeft((Map[Int,Robot](),Seq[DeliveryResponse]()))((acc,dm) => {
            val recipient = lookup(acc._1, dm.recipient)
//            println(s"Delivering to $recipient")
            recipient.map(r => {
              val raw = r.deliver(this, dm.msg)
              val dr = DeliveryResponse(dm, raw._1)
              
//              println(s"Delivery response>> ${dr}");
              
              /*
               * We keep changing and replace the state of the robots we've
               * already found and continue to evolve them as they receive
               * more messages
               */
              val acc2 = acc._1 + (r.id -> raw._2)
              (acc2, acc._2 :+ dr)
            }).getOrElse(acc)
          })
          
        val cache = deliveryResponses._1
        val resps = deliveryResponses._2
        
        val deliveryRecipts = resps.foldLeft(cache)((acc,dr) => {
          val sender = lookup(acc, dr.manifest.sender)
          sender.map(r => {
            val updated = r.delivered(dr.success, dr.manifest.msg.id)
            acc + (r.id -> updated)
          }).getOrElse(acc)
        })
        
        return deliveryRecipts.values.toSeq
      })
  }
    
}

object World {
  
}