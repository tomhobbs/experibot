package com.arxality.experibot.logging

import org.bson.Document

/**
 * Sadly tied to Mongo DB
 */
trait Loggable {
  
  def toDocument(): Document;
  
}