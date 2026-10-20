# Manual test plan

These are checks to run after building; they are not tests already passed.

1. Build with Java 25 and launch a clean Fabric 26.3 instance with Fabric API 0.164.0+26.3.
2. Confirm the log reports that PvP-kit initialized and all feature modules were registered.
3. Check each module's defaults, unknown-option rejection, reset behavior and text import/export.
4. Check CooldownBook never returns negative time and CombatHistory evicts oldest entries at its limit.
5. Check AnimationTimeline loop/easing behavior and cleanup of completed tracks.
6. Check Loadout slot bounds and SettingsStore range validation.
7. Test singleplayer and a multiplayer server where client mods are allowed; verify no server rules change.
8. Verify privacy and reduced-motion preferences before streaming.
