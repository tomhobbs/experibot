package com.arxality.experibot.robots.kilobot

import com.arxality.experibot.comms.Message
import org.bson.Document
import com.arxality.experibot.simulator.World
import com.arxality.experibot.logging.Loggable

class DebuggableKilobotMessage(override val id: Int,  
                               override val senderId: Int,  
                               override val recipientIds: Option[Seq[Int]],  // For Kilobots this will always be None, since that isn't how KB comms works
                               msgType: Short,  
                               data: Option[Seq[Short]]) extends Message(id, senderId, None) with Loggable {
  
  override def toDocument(): Document = {
    val doc = new Document()
                .append("msg_id", id)
                .append("sender_id", senderId)
                .append("msg_type", msgType)
    
    (recipientIds, data) match {
      case (Some(rIds), Some(bytes)) => {
        doc.append("recipient_ids", rIds).append("data", bytes)
      }
      case (_, Some(bytes)) => {
        doc.append("data", bytes)
      }
      case (Some(rIds), _) => {
        doc.append("recipient_ids",rIds)
      }
      case (None, None) => doc
    }
    
  }
  
  /*
   * Kilobots just use broadcast, so find everything within comms range and
   * check that.
   */
  def isFor(w: World, r: DebugableKilobot): Boolean = {
    val recipients = w.findRobot(senderId).map(r => w.inCommsRange(r) )
    recipients.contains(r)
  }
  
  def toRaw(): KilobotMessage = {
    new KilobotMessage(msgType, data);
  }
}