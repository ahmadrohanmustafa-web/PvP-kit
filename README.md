# PvP-kit

Fabric client-side PvP companion for Minecraft Java Edition **26.3**. This upgrade preserves the original repository identity and expands it into a Gradle mod project.

## Feature core
The source includes state modules for animated crosshair options, combat HUD models, loadouts, keybind profiles, sword/shield/totem/potion/inventory helpers, animation timelines, cooldowns, target info, combo tracking, effects, layout presets, sound cues, performance budgets, combat history, durability, projectiles, movement, resources, friend markers, session stats, accessibility, warnings, training, privacy and notifications.

**Multiplayer:** client-side only. It does not grant items, change server rules, automate attacks or bypass anti-cheat. Server-authoritative behavior remains unchanged.

## Build
Install a Java 25 JDK and Minecraft 26.3 Fabric. Windows: `.\gradlew.bat build`; Linux/macOS: `./gradlew build`. The jar is produced in `build/libs/`. First build needs internet. Install Fabric API 0.164.0+26.3 in the game instance.

## Current status
This code drop contains a substantial feature-state core and Fabric client entrypoint. These state modules are not yet fully connected to live 26.3 HUD rendering and keyboard hooks. The build and in-game behavior have not been tested here; do not assume every effect or keybind is visible until it is wired and verified.
