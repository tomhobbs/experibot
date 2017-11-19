import World.Position

object Main extends App {
  
  println("Starting")
  
  val world = new World(0,
        Seq(
            new DebugableKilobot("SKIN", 1, Position(0,1)),
            new DebugableKilobot("PLASM", 2, Position(1,1)),
            new DebugableKilobot("PLASM", 3, Position(2,1)),
            new DebugableKilobot("PLASM", 4, Position(3,1)),
            new DebugableKilobot("SKIN", 5, Position(4,1))
            )
      )
  
  world.debug().tick().debug().tick()
  
  println("Stopped")
  
}