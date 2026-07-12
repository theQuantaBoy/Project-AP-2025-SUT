# Stardew Valley (AP Course Project)

A 2D farming/life-sim game inspired by [Stardew Valley](https://www.stardewvalley.net/) by ConcernedApe, built with [libGDX](https://libgdx.com/). This was our final project for the **Advanced Programming** course, Department of Computer Engineering, Sharif University of Technology — Spring 2025.

Grow crops, raise animals, fish, cook, craft, run a farm through the seasons, and befriend (or marry) the townsfolk — solo or with friends over a local client/server multiplayer session.

## Download & play (Linux)

No Java installation needed — the release bundles its own runtime.

1. Grab the latest client from the [Releases page](https://github.com/theQuantaBoy/Project-AP-2025-SUT/releases).
2. Extract it and run `./StardewValley`.
3. *(Optional)* Run `./install-desktop-entry.sh` once to add a proper icon and app-menu entry. `./uninstall-desktop-entry.sh` removes it again. Neither script is required just to play.

Windows builds aren't published yet.

### Hosting a local server

Want to play with friends? One person runs the server, everyone else connects to their address as a client.

1. Grab `StardewValleyServer` from the same [Releases page](https://github.com/theQuantaBoy/Project-AP-2025-SUT/releases).
2. Extract it and run `./StardewValleyServer` from a terminal (it's headless — no window, just server logs; keep the terminal open while people are playing).
3. Have players connect to your machine's address from their client.

## Features

- Farming: tilling, planting, watering, seasonal crops, foraging, fishing (with a minigame)
- Animals: raising, feeding, and collecting products from farm animals
- Crafting & cooking: turn raw materials into tools, upgrades, and higher-value goods
- Shops & economy: buy/sell goods and tools around town
- NPCs: friendships, gifting, and marriage, with their own daily schedules
- Day/season cycle and weather (rain, storms, lightning)
- Multiplayer: client/server architecture, play solo or with others on your network
- Persistent saves via local SQLite databases

## Tech stack

- [libGDX](https://libgdx.com/) (LWJGL3 desktop backend)
- [Kryonet](https://github.com/EsotericSoftware/kryonet) for client/server networking
- SQLite (via the `sqlite-jdbc` driver) for save data
- Jackson / Gson for JSON (de)serialization

## Project layout

- `core` — shared game logic: model (world, animals, buildings, tools, resources, shops), controllers, screens/UI, and the client/server networking layer. Both the desktop client and the headless server are built from this module.
- `lwjgl3` — desktop launcher for the game client (LWJGL3 backend).
- `packaging` — scripts that build the self-contained Linux releases published on the Releases page.

## Building from source

This project uses [Gradle](https://gradle.org/) with the included wrapper (`./gradlew` / `gradlew.bat`). Requires a JDK (21+).

**Run a client**:

```
./gradlew lwjgl3:run
```

You can run multiple clients against the same server instance to play multiplayer locally.

**Run the server** (headless, no window): run `ap.project.network.server.GameServer`'s `main` method from your IDE, or build a runnable package with `./packaging/build-server-app.sh` (see below).

Useful Gradle tasks:

- `./gradlew build` — build all subprojects.
- `./gradlew clean` — remove build output.
- `./gradlew lwjgl3:run` — launch a game client.
- `./gradlew test` — run unit tests.

### Building the Linux release packages yourself

```
./packaging/build-linux-app.sh    # client -> packaging/dist/StardewValley/
./packaging/build-server-app.sh   # server -> packaging/dist/StardewValleyServer/
```

Both produce a portable, self-contained folder (bundled JRE, no separate Java install needed) plus a matching `.tar.gz`. See the comments at the top of each script for details and requirements (mainly `jpackage`, which comes bundled with JDK 14+).

## Team

- [Mohsen Salah](https://github.com/theQuantaBoy)
- [Arash Akbari](https://github.com/BrokeArash)
- [Moshtagh Motazedian](https://github.com/Moshtagh03)
