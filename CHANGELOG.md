# Changelog

## [0.3-beta] - 2026-05-09

### Feat
- **ui:** Add testing section to app settings for setup flow.
- **ui:** Refactor setup screen into a multi-step onboarding flow.
- **update:** Enhance update feature with expressive UI and changelogs.
- **ui:** Add app settings screen and auto-update toggle.
- **update:** Implement in-app "Check for Updates" feature.
- **Floating Overlay:** Implement CPU and Battery temperature monitoring.
- **battery:** Reset min/max stats and graph on charging status change.
- **cpu:** Add real-time temperature monitoring for CPU and individual cores.
- **battery:** Display negative values for discharging speed.
- **Floating Overlay:** Support 0.5s increments for update interval.

### Fix
- **ui:** Resolve peak frequency showing 0.0 GHz on CPU screen.
- **display:** Modernize APIs and fix context compatibility.

### Perf
- **kernel:** Optimize cpu.rs sysfs polling pipeline.
- **cpu:** Optimize hardware polling with JNI batching and thermal caching.

### Refactor
- **rust:** Optimize memory info parsing using pattern matching.
- **ui:** Modularize generic components and cleanup dead code.
- **display:** Use modern Android 14+ APIs for display and HDR capabilities.

### Docs
- Overhaul README and add contributing guidelines.
- Add initial 0.2-beta changelog.

### Build
- Bump AGP to 9.2.1 and core dependencies.

## [0.2-beta] - 2026-05-03

### Features
- **battery:** Add animation to status text.
- **battery:** Make battery graph history duration reactive.
- **display:** Determine DPI badge color depending on HDR support.
- **display:** Add HDR capabilities detection.
- **vulkan:** Add Vulkan version detection and display.
- **settings:** Implement adjustable vibration intensity.
- **haptics:** Implement system-wide haptic feedback support.
- **ui:** Implement data source help bottom sheet in HomeScreen.
- **ui:** Implement visual customization options in Floating Overlay Settings.

### Performance
- Persist battery history across screen sessions while pausing updates when inactive.
- Ensure battery data streams pause when the screen is inactive to save resources.
- Optimize battery screen performance and fix graph scaling issues.
- Optimize JNI data bridge and Compose recomposition for smoother UI.

### UI/UX Enhancements
- Set minimum 1000mA scale for battery speed graph for better readability.
- Change battery status animation to a smoother horizontal slide.
- **BatteryScreen:** Implement real-time charging speed graph.
- **BatteryScreen:** Animate Power Source text transitions.
- **BatteryScreen:** Implement dynamic charging speeds and animations.
- **Theme:** Adopt Material 3 motion scheme for color animations.
- **Settings:** Update snap animations and Floating Overlay transitions to use standard motion specs.
- **CPUScreen:** Redesign CoreDetailCard for better information density.

### Bug Fixes
- **ui:** Fix BottomNavBar colors in dark mode.
- **ui:** Fix color accent bugs on certain Custom ROMs.
- **ui:** Eliminate white blink effect on Layout cards in Floating Overlay Settings.

### Refactoring & Cleanup
- Extract reusable UI components and reorganize haptic package.
- Update JNI array manipulation methods for better safety.
- Use `EnvUnowned` for more robust JNI handling in Rust.
- Reorganize generic UI components into a shared directory.
- Optimize imports and format native code.
- Standardize typography by removing monospace font from charging speed display.

### Build & CI
- Bump `jni` crate from 0.21.1 to 0.22.4.
- Configure Rust release profile and add necessary dependencies.
- Add Cargo ecosystem support to Dependabot.
- Configure Gradle variants for side-by-side installation of debug and release builds.

### Documentation
- Comprehensive update of project documentation.
- Add KDoc documentation to all major UI components.
- Update README with latest features and build instructions.
