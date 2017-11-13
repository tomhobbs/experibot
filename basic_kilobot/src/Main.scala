import Experiment1.Skin
import World.Position
import Experiment1.Plasm


object Main extends App {
  
  println("Starting")
  
  val world = new World(
        Seq(
            new Skin(1, Position(0,1)),
            new Plasm(2, Position(1,1)),
            new Plasm(3, Position(2,1)),
            new Plasm(4, Position(3,1)),
            new Skin(5, Position(4,1))
            )
      )
  
  world.debug()
  
  println("Stopped")
  
}