package com.arxality.experibot.kilobot

import org.bson.Document
import com.arxality.experibot.logging.Loggable

class DebuggableKilobotMessage(val id: Int,  
                               val senderId: Int,  
                               msgType: Short,  
                               data: Option[Seq[Short]]) extends Loggable {
  
  override def toDocument(): Document = {
    val doc = new Document()
                .append("msg_id", id)
                .append("sender_id", senderId)
                .append("msg_type", msgType)
    
    data.map(d => doc.append("data", d)).getOrElse(doc)
  }
  
  def toRaw(): KilobotMessage = {
    new KilobotMessage(msgType, data);
  }
}