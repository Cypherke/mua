# Minecraft Ultrahardcore Admin (MUA)

A Java wrapper for Minecraft servers that adds chat commands and optional IRC integration.
MUA starts and manages the server process, reads console output in real-time,
and responds to player commands by writing back to the server.

## Features

- **Teleportation** (`!tp`) — save, list, delete and teleport to named locations or other players
- **Coordinate sharing** (`!sc`) — broadcast your current coordinates to all players
- **Time display** (`!time`) — show the in-game time
- **IRC bridge** — optionally relay chat between the Minecraft server and an IRC channel via named pipes

## Prerequisites

- Java 21 or later
- A Minecraft server jar

## Install

### Install Java

**Windows:** download and install your preferred JDK. See https://whichjdk.com

**Debian/Ubuntu:**
```
apt install default-jdk
```

### Build from source

```
./gradlew build
```

This produces `mua-HEAD-SNAPSHOT-all.jar` in `build/libs/`.

### Run

Copy the fat jar and `mua.xml` to your directory of choice, edit `mua.xml` to point to your
server jar and data files, then run:

```
java -jar mua-HEAD-SNAPSHOT-all.jar
```

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md).