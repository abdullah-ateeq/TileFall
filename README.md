# TileFall 🎮

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0+-blue.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-orange.svg)](https://developer.android.com/jetpack/compose)
[![Material 3](https://img.shields.io/badge/Design-Material%203-purple.svg)](https://m3.material.io)

**TileFall** is a feature-rich, modern Android arcade puzzle game built entirely with **Jetpack Compose** and **Material 3**. Designed with clean architecture, smooth animations, immersive haptics, and robust state management, TileFall delivers an engaging mobile gaming experience.

---

## ✨ Features

- **Modern UI / UX**: Built 100% with Jetpack Compose and Material 3 design principles, featuring dynamic color themes, fluid transitions, and polished animations.
- **Robust Game Engine**: Custom-engineered game loop and physics logic (`GameEngine`) handling tile movement, collision detection, scoring, and level progression.
- **Multiple Difficulty Modes**: Tailored settings (`Easy`, `Medium`, `Hard`) to suit casual players and arcade veterans alike.
- **Persistent Preferences & State**: Powered by Jetpack DataStore to save high scores, user preferences, sound/haptic settings, and game state across sessions.
- **Audio & Haptic Feedback**: Integrated sound effects and tactile haptic responses (`SoundHapticsManager`) for a deeply satisfying tactile gaming feel.
- **Comprehensive Navigation**: Seamless multi-screen flow (Splash, Home, Game, Result, Settings, About) implemented via Jetpack Navigation Compose.
- **Thoroughly Tested**: Unit tests and instrumentation tests ensuring reliable gameplay logic and robust performance.

---

## 🛠️ Tech Stack & Architecture

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) & Material 3
- **Architecture**: MVVM (Model-View-ViewModel) with unidirectional data flow (`StateFlow`)
- **Navigation**: Navigation Compose
- **Persistence**: Jetpack DataStore (Preferences)
- **Concurrency**: Kotlin Coroutines & Flow
- **Testing**: JUnit, Turbine, AndroidX Test Runner, Compose UI Testing

---

## 📁 Project Structure

```tree
app/src/main/java/com/example/tilefall/
├── data/
│   └── GamePreferences.kt       # DataStore preferences management
├── engine/
│   └── GameEngine.kt            # Core game logic, physics & state updates
├── model/
│   ├── Difficulty.kt            # Game difficulty definitions
│   ├── GameState.kt             # Current game state data structures
│   └── Tile.kt                  # Tile entity models
├── sound/
│   └── SoundHapticsManager.kt   # Audio playback & haptic feedback controller
├── ui/
│   ├── components/              # Reusable Compose UI components & Canvas rendering
│   ├── navigation/              # NavGraph and Screen definitions
│   ├── screens/                 # Splash, Home, Game, Result, Settings, About screens
│   └── theme/                   # Material 3 Color, Type, and Theme setups
└── viewmodel/
    ├── GameViewModel.kt         # Game state & action handling ViewModel
    └── SettingsViewModel.kt     # App preferences ViewModel
```

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio** (Arctic Fox / Ladybug or newer recommended)
- **JDK 11** or higher
- Android SDK with **Compile SDK 37** (Min SDK 24)

### Building & Running
1. Clone the repository:
   ```bash
   git clone https://github.com/abdullah-ateeq/TileFall.git
   ```
2. Open the project in Android Studio.
3. Sync Gradle and run the `:app` module on an emulator or physical Android device (API 24+).

---

## 🧪 Running Tests

You can run unit and instrumentation tests via Gradle:

- **Unit Tests**:
  ```bash
  ./gradlew test
  ```
- **Instrumentation Tests**:
  ```bash
  ./gradlew connectedAndroidTest
  ```

---

## 📄 License

This project is open-source and available under the [MIT License](LICENSE).
