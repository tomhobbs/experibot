package com.arxality.experibot.comms

import com.arxality.experibot.simulator.World
import com.arxality.experibot.simulator.Robot

//object MessageDeliveryService {
//  
//  
//  def deliverMessages(world: World): Unit = {
//    /*
//     *  Try to avoid gather Seqs in the main algo for now, ultimately this will
//     *  probable involve streaming out/in of a database to provide the scale
//     */
//    world.streamRobots().map(r => {
//      val x = r.getMessagesToSend(world).map(msgs => {
//        val dms = deliveryManifests(world, msgs)
//        val drs = world.deliver(dms)
//        world.handleDeliveryResponse(drs)
//      })
//    })
//  }
//  
//}