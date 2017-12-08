package com.arxality.experibot

import com.arxality.experibot.simulator.World
import com.arxality.experibot.robots.kilobot.DebugableKilobot
import com.arxality.experibot.simulator.Position
import com.arxality.experibot.robots.kilobot.example.Firefly
import com.arxality.experibot.robots.kilobot.Kilobot

object Main extends App {
  
  def log(s: String) {
    println(s);
  }
    
  println("Starting")
  
  def botBuilder(log: (String) => Unit): Kilobot = {
    new Firefly(log)
  }
  
  val world = new World(0,
        Seq(
            
            new DebugableKilobot("Firefly",  11, new Position(1,1), botBuilder, log)
            , new DebugableKilobot("Firefly",  12, new Position(1,2), botBuilder, log)
//            new DebugableKilobot("Firefly",  13, new Position(1,3), botBuilder, log),
//            new DebugableKilobot("Firefly",  14, new Position(1,4), botBuilder, log),
//            new DebugableKilobot("Firefly",  15, new Position(1,5), botBuilder, log),
//            
//            new DebugableKilobot("Firefly",  21, new Position(2,1), botBuilder, log),
//            new DebugableKilobot("Firefly",  22, new Position(2,2), botBuilder, log),
//            new DebugableKilobot("Firefly",  23, new Position(2,3), botBuilder, log),
//            new DebugableKilobot("Firefly",  24, new Position(2,4), botBuilder, log),
//            new DebugableKilobot("Firefly",  25, new Position(2,5), botBuilder, log),
//            
//            new DebugableKilobot("Firefly",  31, new Position(3,1), botBuilder, log),
//            new DebugableKilobot("Firefly",  32, new Position(3,2), botBuilder, log),
//            new DebugableKilobot("Firefly",  33, new Position(3,3), botBuilder, log),
//            new DebugableKilobot("Firefly",  34, new Position(3,4), botBuilder, log),
//            new DebugableKilobot("Firefly",  35, new Position(3,5), botBuilder, log),
//            
//            new DebugableKilobot("Firefly",  41, new Position(4,1), botBuilder, log),
//            new DebugableKilobot("Firefly",  42, new Position(4,2), botBuilder, log),
//            new DebugableKilobot("Firefly",  43, new Position(4,3), botBuilder, log),
//            new DebugableKilobot("Firefly",  44, new Position(4,4), botBuilder, log),
//            new DebugableKilobot("Firefly",  45, new Position(4,5), botBuilder, log),
//            
//            new DebugableKilobot("Firefly",  51, new Position(5,1), botBuilder, log),
//            new DebugableKilobot("Firefly",  52, new Position(5,2), botBuilder, log),
//            new DebugableKilobot("Firefly",  53, new Position(5,3), botBuilder, log),
//            new DebugableKilobot("Firefly",  54, new Position(5,4), botBuilder, log),
//            new DebugableKilobot("Firefly",  55, new Position(5,5), botBuilder, log)
            
            )
      )
  
  world.init().tick().tick().tick()
  
  println("Stopped")
  
}