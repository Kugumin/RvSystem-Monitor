# 🚀 RvSystem Monitor

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Rust](https://img.shields.io/badge/Backend-Rust-000000?logo=rust&logoColor=white)](https://www.rust-lang.org)
[![Latest Release](https://img.shields.io/github/v/release/Rve27/RvSystem-Monitor)](https://github.com/Rve27/RvSystem-Monitor/releases)
[![License](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](LICENSE)
[![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/compose)
[![Downloads](https://img.shields.io/github/downloads/Rve27/RvSystem-Monitor/total?logo=github&color=FF69B4)](https://github.com/Rve27/RvSystem-Monitor/releases)

**RvSystem Monitor** is a high-performance system monitoring solution for Android, merging the expressive power of **Jetpack Compose** with the raw efficiency of **Rust**. It provides low-level hardware insights while maintaining a modern, buttery-smooth user experience.

---

## 📖 Table of Contents
- [🚀 Overview](#-overview)
- [📸 Screenshots](#-screenshots)
- [✨ Key Features](#-key-features)
- [🛠️ Tech Stack](#-tech-stack)
- [📂 Project Structure](#-project-structure)
- [🏗️ Architecture](#-architecture)
- [⚙️ Getting Started](#-getting-started)
- [🤝 Contributing](#-contributing)
- [📜 License](#-license)

---

## 🚀 Overview
RvSystem Monitor bridges the gap between high-level UI frameworks and low-level system APIs. By utilizing a Rust-based backend, it minimizes the performance overhead typically associated with frequent polling of kernel files like `/proc` and `/sys`. This hybrid approach allows for real-time monitoring of CPU frequencies, GPU drivers, battery health, and memory usage without compromising the device's responsiveness.

Built with **Material 3 Expressive**, the application offers a visually rich experience with adaptive layouts and sophisticated transitions, making system diagnostics both powerful and beautiful.

---

## 📸 Screenshots

<p align="center">
  <img src="fastlane/metadata/android/en-US/images/phoneScreenshots/screenshot_1.jpg" width="32%" />
  <img src="fastlane/metadata/android/en-US/images/phoneScreenshots/screenshot_2.jpg" width="32%" />
  <img src="fastlane/metadata/android/en-US/images/phoneScreenshots/screenshot_3.jpg" width="32%" />
</p>
<p align="center">
  <img src="fastlane/metadata/android/en-US/images/phoneScreenshots/screenshot_4.jpg" width="32%" />
  <img src="fastlane/metadata/android/en-US/images/phoneScreenshots/screenshot_5.jpg" width="32%" />
  <img src="fastlane/metadata/android/en-US/images/phoneScreenshots/screenshot_6.jpg" width="32%" />
</p>

---

## ✨ Key Features

- **🔋 Battery Intelligence**: Live tracking of Wattage (W), cycle counts (Android 14+), health percentage, and precise Deep Sleep vs. Uptime metrics.
- **🖥️ System Overlay**: A draggable, low-overhead floating monitor for real-time FPS and RAM metrics. Fully customizable update intervals.
- **🎮 GPU & Graphics**: Retrieval of GPU renderer, vendor, and supported OpenGL ES & Vulkan versions directly through the EGL context and native drivers.
- **⚙️ CPU Dynamics**: Detailed per-core monitoring including current, minimum, and maximum frequencies and scaling governors.
- **🧠 Memory & ZRAM**: High-precision tracking of RAM and ZRAM usage, including cached, buffers, and kernel slab memory.
- **⚡ Native Performance**: Optimized Rust backend that parses kernel files and interacts with hardware drivers directly with efficient JNI batching.
- **🎨 Expressive UI**: Built with Material 3 Expressive, featuring adaptive layouts, sophisticated screen transitions, and optimized recomposition.

---

## 🛠️ Tech Stack

### Frontend (Android)
- **Language**: Kotlin 2.3.21
- **UI Framework**: Jetpack Compose (Material 3 Expressive, BOM 2026.05.00)
- **Dependency Injection**: Hilt 2.59.2
- **Concurrency**: Coroutines & Flow
- **Persistence**: Jetpack DataStore (Preferences)

### Backend (Native)
- **Language**: Rust (Edition 2024)
- **Bridge**: JNI (Java Native Interface) via `jni-rs`
- **Native APIs**: `libc`, Vulkan, EGL
- **Optimization**: `once_cell` for efficient kernel file descriptor caching

### Infrastructure
- **Build System**: Gradle Kotlin DSL + Cargo NDK
- **Distribution**: Fastlane
- **Formatting**: Spotless (Kotlin) & Cargo Fmt (Rust)

---

## 📂 Project Structure
```text
RvSystem-Monitor/
├── app/                  # Main Android application module (Kotlin)
│   ├── src/main/java/    # UI, ViewModels, and JNI bridge
│   ├── src/main/res/     # Resources and assets
│   ├── build.gradle.kts  # App-level build configuration
│   └── proguard-rules.pro # R8/Proguard optimization rules
├── rust/                 # Native monitoring backend (Rust)
│   ├── src/              # Kernel parsing and JNI implementation
│   ├── Cargo.toml        # Rust package metadata
│   └── README.md         # Native-specific documentation
├── gradle/               # Build system configurations
│   └── libs.versions.toml # Centralized dependency management
├── fastlane/             # Distribution metadata and screenshots
├── LICENSE               # GNU GPL v3.0
└── README.md             # This file
```

---

## 🏗️ Architecture

The project adheres to **Clean Architecture** principles, ensuring a strict separation of concerns and high maintainability.

### The Hybrid Core
- **UI Orchestration**: The Kotlin layer manages the application lifecycle and UI state. It uses ViewModels to expose data streams from repositories.
- **Native Data Source**: The Rust layer handles the "heavy lifting". It parses system files and interacts with hardware drivers. By mirroring the Linux kernel's structure (`kernel/` for CPU, `mm/` for Memory, and `drivers/` for GPU), it provides an idiomatic and high-performance data source.
- **Optimized JNI Bridge**: Instead of frequent fine-grained calls, the bridge is designed for **batch data retrieval**. Single calls fetch complete data sets (e.g., all CPU metrics at once), significantly reducing context-switching overhead between the JVM and Native code.

---

## ⚙️ Getting Started

### Prerequisites
- **Android Studio** (Ladybug 2024.2.1 or newer)
- **Rust Toolchain** ([rustup.rs](https://rustup.rs/))
- **Android NDK** (Version `30.0.14904198` recommended)
- **cargo-ndk**: `cargo install cargo-ndk`

### Installation & Build
1. **Clone the repository**:
   ```bash
   git clone https://github.com/Rve27/RvSystem-Monitor.git
   cd RvSystem-Monitor
   ```
2. **Build Native Libraries**:
   ```bash
   ./gradlew :app:buildRustLibraries
   ```
3. **Run the application**:
   Connect an Android device and run:
   ```bash
   ./gradlew installDebug
   ```

---

## 🤝 Contributing
We welcome contributions from the community! Whether you are fixing a bug, adding a feature, or improving documentation, please read our [CONTRIBUTING.md](CONTRIBUTING.md) to get started.

## 📜 License
This project is licensed under the **GNU General Public License v3.0**. See the [LICENSE](LICENSE) file for details.

---
<p align="center">
  Built with ❤️ for the Android Community.
</p>
