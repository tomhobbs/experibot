# Basic Kilobot

Serves to imitate basic Kilobot communications behaviour.  No movement or 
other sensing.  This is a playground, just to experiment with basic and simple
comms protocols without the rest of the infrastructure getting in the way.

The idea here is to have an interface for the any given hardware platform that matches the real one as closely as possible.  On top of that, there are so called "Debugable" interfaces that provide addition information that wouldn't ordinarily be available when running as a physical experiment.  For example;

- DebugableKilobot has both an ID and knowledge of its Position within the world
- DebuggableKilobotMessage has a built in sender field and also an ID.  Later,it might have a traceroute type history as well.

The premise is that debugging a swarm algorithm is hard when the programmer has only the same limited information as the physical robot (assuming a simple hard ware platform).  By creating the debugable interfaces we're able to isolate the core algorithm from the tools necessary to correct the program.

This simulator sacrifices real-world physics and speed for swarm scale processing.  Later enhancements will include streaming the swarm behaviour through a database and/or other analytics., the usual config and logging libraries, and a user interface.

As it stands, this code has not been tested or run in anger, so no guarantee can be made about its speed, accuracy or correctness.
