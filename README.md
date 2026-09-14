
Installation information
=======

This template repository can be directly cloned to get you started with a new
mod. Simply create a new repository cloned from this one, by following the
instructions provided by [GitHub](https://docs.github.com/en/repositories/creating-and-managing-repositories/creating-a-repository-from-a-template).

Once you have your clone, simply open the repository in the IDE of your choice. The usual recommendation for an IDE is either IntelliJ IDEA or Eclipse.

If at any point you are missing libraries in your IDE, or you've run into problems you can
run `gradlew --refresh-dependencies` to refresh the local cache. `gradlew clean` to reset everything 
{this does not affect your code} and then start the process again.

Mapping Names:
============
By default, the MDK is configured to use the official mapping names from Mojang for methods and fields 
in the Minecraft codebase. These names are covered by a specific license. All modders should be aware of this
license. For the latest license text, refer to the mapping file itself, or the reference copy here:
https://github.com/NeoForged/NeoForm/blob/main/Mojang.md

Additional Resources: 
==========
Community Documentation: https://docs.neoforged.net/  
NeoForged Discord: https://discord.neoforged.net/

# Catface Coding Test Mod -- Camera Keyframe & Playback:
==========
A lightweight NeoForge mod for Minecraft 1.21.1 that lets a player capture named camera keyframes and play them back as a smooth, 
interpolated cinematic move — the kind of tool a Production Associate would use to line up a camera pan before a recording session.

## Build & Run:
==========
Requirements: 
- `JDK 21` installed and configured in your environment.

```
# Build a distributable jar (output in build/libs/)
.\gradlew build

# Launch a dev client with the mod loaded
.\gradlew runClient
```
The client run also starts the integrated (singleplayer) server automatically, 
which is all this mod needs — multiplayer/dedicated-server sync is explicitly out of scope for this task.

## Commands:
==========

| Command | Effect | Example |
| --- | --- | --- |
| `/camkey create <name>` | Creates a new keyframe sequence and makes it the active one. | `/camkey create intro` |
| `/camkey add <name>` | Captures your current position/rotation as a keyframe on the active sequence. | `/camkey add start` |
| `/camkey list` | Shows the active sequence's name and keyframe count. | `/camkey list` |
| `/camkey play <name> <seconds>` | Smoothly interpolates the camera through the named sequence over the given duration. | `/camkey play intro 10` |
| `/camkey stop` | Cancels playback early and returns control to the player. | `/camkey stop` |

Sequences and their keyframes persist to disk automatically and reload the next time the world starts.

## Key architectural decisions:
==========

**Capture / storage / playback are separate, single-responsibility classes**, per the brief:

```
com.catface.codingtest
├── command/
│   └── CameraCommand.java         # Brigadier syntax tree & player interaction
├── manager/
│   ├── KeyframeManager.java       # In-memory session state & sequence management
│   └── KeyframeStorage.java       # Disk persistence (JSON/DTO serializer utility)
├── model/
│   ├── CameraKeyframe.java        # Immutable record for (x, y, z, yaw, pitch)
│   └── KeyframeSequence.java      # Named list of camera keyframes
└── playback/
    ├── CameraController.java      # Low-level client-side camera positioning
    └── CameraPlayback.java        # Non-blocking tick-driven state machine
```

This split is why adding, say, rotation easing or multiple simultaneous camera "tracks" later shouldn't require touching the command layer or the storage layer at all — it's isolated to CameraPlayback.

**Persistence uses JSON with an explicit DTO(Data Transfer Object) layer, not raw NBT(Named Binary Tag) or direct serialization of the domain classes.** `KeyframeStorage` maps `CameraKeyframe`/`KeyframeSequence` to small `record` DTOs before writing them out with Gson. 
That's slightly more code than just pointing Gson at the domain objects directly, but it means a future refactor of the domain model (renaming a field, adding rotation easing metadata, etc.) can't silently corrupt or break old save files — the on-disk shape is decoupled from the in-memory shape.

**Save files live under the world's own save folder** 
(`<world>/catfacecodingtestmod/camkey_sequences.json`), not a global config directory. A camera sequence is almost always specific to a particular build/set, so tying it to the world it was recorded in seemed like the more useful default. 
Sequences are saved immediately after every `create`/`add` (not just on world close), so a crash mid-session doesn't lose captured keyframes.

**Playback temporarily switches the player to spectator mode.** See Limitations below for why — this was originally the source of a "Dev moved wrongly!" server-side rejection loop.

## Known limitations / what I'd do differently with more time:
==========

- **The camera is the player, not a separate render-only camera.** `CameraController` moves the actual player entity (`Minecraft.getCameraEntity()`), 
because that's the simplest way to reposition the view with the client-side APIs available without adding a Mixin dependency to the project. 
The catch: the client automatically reports the player's position to the server every tick as ordinary movement, and vanilla's anti-cheat movement validation was rejecting the large/through-terrain jumps a cinematic pan produces, 
snapping the player back and spamming "Dev moved wrongly!" in the log. 
The fix here is to flip the player to spectator mode for the duration of playback (spectators are noClip, so that validation is skipped) and restore their previous gamemode afterward. It works, but it's a real, 
visible gamemode change rather than a fully invisible camera layer.
- **No `/camkey delete <name>`** command exposed yet, even though KeyframeManager already supports removing a sequence internally. Left out to stay inside the time box.
- **Single active sequence at a time.** There's no way to have two playback tracks running simultaneously, or to preview a sequence without it being "current." The manager/playback split should make that additive rather than a rewrite, if it's needed later.
- **No in-game feedback while a sequence is playing** (e.g., a progress indicator or action-bar countdown) — you only find out playback ended via the "stopped" style chat message. Minor polish, but relevant for someone actually directing a shot.
- **Minecraft/Neo versions**: Minecraft 1.21.1 / NeoForge 21.1.217.


## If I had another week:

I'd replace the spectator-mode workaround with a proper Mixin-based render camera (decoupling the "camera" fully from the player entity, 
so the player's actual position/gamemode/collision state is never touched during playback), and expose a /camkey delete 
and /camkey list all for managing multiple saved sequences from in-game rather than only editing the JSON file by hand.

## AI-usage notes
==========

- Tools Used: `ChatGPT` and `Claude`.

- Workflow Split:

`ChatGPT`: Used for generating initial API scaffolding (Brigadier command tree syntax, NeoForge event signatures).

`Claude`: Used for Gson TypeToken generics and to downgrade and resolve version mapping discrepancies after accidentally targeting Minecraft 1.21.11 instead of 1.21.1.

- AI Correction Highlight:

`Monolithic Package Structure`: AI initially generated all classes flatly inside a single root package with tightly coupled responsibilities. 
Manually refactored the codebase into distinct functional packages (command, manager, model, playback) to enforce clean boundaries.