import World.Position

class Skin(log: (String) => Unit) extends Kilobot(log) {

  def in(m: KilobotMessage): Kilobot = {
    // TODO
    this
  }
  
  def out(): Option[KilobotMessage] = {
    // TODO
    None
  }

  override def loop(): Kilobot = {
    log("Looping")
    new Skin(log)
  }
  
}

class Plasm(log: (String) => Unit) extends Kilobot(log) {
  
  def in(m: KilobotMessage): Kilobot = {
    // TODO
    this
  }
  
  def out(): Option[KilobotMessage] = {
    // TODO
    None
  }
  
  override def loop(): Kilobot = {
    log("Looping")
    new Plasm(log)
  }
  
}

