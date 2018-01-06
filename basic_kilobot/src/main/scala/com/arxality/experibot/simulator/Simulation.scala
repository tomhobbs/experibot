package com.arxality.experibot.simulator

import com.arxality.experibot.kilobot.DebugableKilobot
import com.arxality.experibot.kilobot.KilobotMessage

object Simulation {
  
  def tick(rs: RobotService) = {
    
    rs
      .nextTick()
      .streamRobots(_.tick())
      .collectOutgoingMessages(_.toSend())
      .deliverAllMessages((sender: DebugableKilobot, nDeliveredTo: Int) => {
          if(0 == nDeliveredTo) {
            /*
             *  In this case no contention on the connection would have been
             *  detected and so as far as the sender knows, the transmission
             *  was a success
             */
            sender.transmissionSuccess()
          } else {
            // TODO - optionally fail a transmission?  How to detect contention?
            sender.transmissionSuccess()
          }
        }
      )
      .completeTick()
    
  }
  
}