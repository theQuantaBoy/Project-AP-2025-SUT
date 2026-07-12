# StardewValley

A Stardew Valley–inspired 2D farming/life-sim game built with [libGDX](https://libgdx.com/), made as a team project for the Advanced Programming course at Sharif University of Technology.

The game is client/server: a headless `GameServer` hosts the game state and multiple `lwjgl3` desktop clients connect to it over the network ([Kryonet](https://github.com/EsotericSoftware/kryonet)).

## Modules

- `core`: Shared game logic — model (world, animals, buildings, tools, resources, shops), controllers, screens/UI, and the client/server networking layer.
- `lwjgl3`: Desktop launcher for the game client (LWJGL3 backend).

## Running

This project uses [Gradle](https://gradle.org/) with the included wrapper (`./gradlew` / `gradlew.bat`).

**Start the server** (headless, no window): run the `main` method of `ap.project.network.server.GameServer` (e.g. from your IDE, or via a Gradle `application`/`JavaExec` task once one is added to `core`).

**Start a client**:

```
./gradlew lwjgl3:run
```

You can run multiple clients against the same server instance to play multiplayer locally.

Persistent data (user accounts, save games) is stored as local SQLite databases at runtime.

Useful Gradle tasks:

- `./gradlew build` — build all subprojects.
- `./gradlew clean` — remove build output.
- `./gradlew lwjgl3:run` — launch a game client.
- `./gradlew test` — run unit tests.
