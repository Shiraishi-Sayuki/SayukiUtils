# LAN Feature — Internal Developer Docs

## File Structure

```
lan/
├── LanFeatureConfig.java        # Config (ngrok authtoken, enabled toggle)
├── LanServer.java               # Core publish/stop logic
├── LanServerValues.java         # Interface: tunnel type/text + raw MOTD on server
├── LanSettings.java             # Data class: all LAN parameters (port, gamemode, etc.)
├── LanState.java                # Per-world SavedData (persists LanSettings + whitelist flag)
├── PublishCommandArgumentValues.java  # Brigadier argument extractors
├── SetCommandsAllowed.java      # Interface: allowCommands setter for PrimaryLevelData
├── TunnelType.java              # Enum for tunnel backends (currently only NONE)
├── cmd/
│   └── argument/
│       ├── GameModeArgumentType.java  # Custom GameType argument parser
│       └── TunnelArgumentType.java    # Custom TunnelType argument parser
├── mixin/
│   ├── ShareToLanScreenMixin.java     # GUI: extended ShareToLanScreen
│   ├── PublishCommandMixin.java       # Command: replaces /publish with subcommands
│   ├── MinecraftServerMixin.java      # Implements LanServerValues on MinecraftServer
│   ├── PlayerListMixin.java           # Per-world ban/op/whitelist files
│   ├── LevelPropertiesMixin.java      # Implements SetCommandsAllowed on PrimaryLevelData
│   ├── IntegratedServerAccessor.java  # @Accessor for LanServerPinger, port, UUID
│   ├── PlayerListAccessor.java        # @Accessor for maxPlayers
│   ├── StoredUserListAccessor.java    # @Invoker for StoredUserList#contains
│   ├── GridWidgetAccessor.java        # @Accessor for GridLayout children
│   └── ButtonAccessor.java           # @Accessor for Button onPress
└── util/
    └── Utils.java                  # Helper: createLink component
```

## Mixin Organization

Defined in `common/src/main/resources/sayukiutils.lan.mixins.json`. Split into two
sides:

**client** — loaded only on the physical client:
- `ShareToLanScreenMixin` — injects into the vanilla `ShareToLanScreen` to add MOTD,
  max players, online mode, PvP, tunnel, per-world/global save/load/clear buttons.
- `IntegratedServerAccessor` — exposes `getLanPinger()`, `setLanPinger()`,
  `setPublishedPort()`, `getLocalPlayerUuid()`.
- `ButtonAccessor` — exposes `setOnPress()` (needed to swap button behaviour).
- `GridWidgetAccessor` — exposes GridLayout children list.

**server** — loaded on both integrated and dedicated server:
- `MinecraftServerMixin` — implements `LanServerValues` (stores tunnel type/text,
  raw MOTD on every `MinecraftServer` instance; cleans up tunnel on shutdown).
- `PlayerListMixin` — redirects ban/op/whitelist files to world-specific paths
  (per-world player data); intercepts op/deop/isOp to delegate to world settings
  for the host player; persists whitelist state via `LanState`.
- `LevelPropertiesMixin` — implements `SetCommandsAllowed` on `PrimaryLevelData`
  so that host-player op/deop toggles `allowCommands` in level settings.
- `PublishCommandMixin` — replaces the vanilla `publish` command with a rich
  subcommand tree: `publish`, `publish perworld`, `publish system`, `publish stop`,
  each accepting optional arguments.
- `PlayerListAccessor` — exposes `setMaxPlayers()`.
- `StoredUserListAccessor` — exposes `callContains()` via `@Invoker`.

## Key Classes and Their Roles

| Class | Role |
|---|---|
| `LanServer` | Static entry point; `startOrSaveLan()` handles first-publish and re-publish (port change, MOTD change, tunnel restart); `stopLan()` disconnects all non-host players, stops the listener + pinger + tunnel. |
| `LanServerValues` | Interface duck-typed onto `MinecraftServer` via `MinecraftServerMixin`. Carries the current tunnel type, tunnel status text, and raw (unprocessed) MOTD string. |
| `LanSettings` | Plain data class holding all publishable parameters: `gameType`, `onlineMode`, `pvpEnabled`, `tunnel`, `port`, `maxPlayers`, `motd`. Supports NBT serialization. |
| `LanState` | A `SavedData` stored in the overworld dimension. Holds the per-world `LanSettings` and the whitelist-enabled flag. Key: `sayukiutils_lan`. |
| `PublishCommandArgumentValues` | Captures the Brigadier argument extractors as lambdas so the same argument list can be re-used across `publish`, `publish perworld`, and `publish system` with different default providers. |
| `TunnelType` | Extensible enum for tunnel backends (e.g. ngrok, playit, etc.). Currently only `NONE`. Each value implements `start(MinecraftServer)` / `stop(MinecraftServer)`. |

## Platform Initialization Differences

### Fabric (`SayukiUtilsFabric`)

- Uses Fabric API `CommandRegistrationCallback` to register `SayukiUtilsCommand` and
  re-register vanilla commands (Op, DeOp, Ban, etc.) so the mixin registrations stick.
- Uses Fabric API `ArgumentTypeRegistry.registerArgumentType()` to register the custom
  `GameModeArgumentType` and `TunnelArgumentType` with the network protocol.
- Fabric handles mixin application automatically via `fabric-loader`. No connector needed.

### Forge (`SayukiUtilsForge`)

- Hooks `RegisterCommandsEvent` to register `SayukiUtilsCommand` and re-register
  vanilla commands (only for `CommandSelection.INTEGRATED` — singleplayer/LAN only).
- Custom argument types are **not** registered; Forge's mixin environment allows them
  to be used without network registration because the `/publish` command is
  integrated-only and never sent to a dedicated server.
- Forge requires `SayukiUtilsMixinConnector` (a dummy `IMixinConfigPlugin`) because
  Forge's Mixin bootstrap expects a plugin entry point in the mod's main class.

## Configuration via `LanFeatureConfig`

`LanFeatureConfig` extends `FeatureConfig` and is registered in `ModConfig` alongside
the other features. It adds one config property:

- `ngrokAuthtoken` (String) — stored in the mod config JSON file; reserved for
  future ngrok tunnel integration.

The config screen (`ConfigScreen.java`) builds a category "LAN" with an enable/disable
toggle and the ngrok authtoken text field.

## Per-World Player Data

When `PlayerListMixin` initialises, it redirects the constructor to load **ops.json**,
**banned-players.json**, **banned-ips.json**, and **whitelist.json** from the
*world-specific save directory* (`world/` subfolder) instead of the global
`saves/<world>/` root. This means each world keeps its own operator list, ban lists,
and whitelist — switching worlds does not carry over moderation data.

Additionally, `LanState` persists LAN settings (port, MOTD, etc.) and the
whitelist-enabled state as `SavedData` in the overworld dimension. These values
survive world reloads and are per-world, not global.
