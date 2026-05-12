# Android Tetris Clone

A personal, offline **block-stacking game** for Android—classic handheld-style play with touch controls, built with **Kotlin** and **Jetpack Compose**.

<p align="center">
  <strong>Stack · Fall · Clear · Repeat</strong>
</p>

---

## Highlights

- **Single-player** gameplay: move, rotate, soft drop, line clears, scoring, game over, restart  
- **Compose UI**: responsive **10×20** grid that scales with your screen  
- **Deterministic game rules** in a small engine layer (`domain` / `game`) separate from UI  
- **Spec-driven workflow**: feature docs live under `specs/` (Spec Kit–friendly layout)

---

## Tech stack

| Layer | Choice |
|--------|--------|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Min SDK | 29 (Android 10+) |
| Target / compile SDK | 35 |

---

## Repository layout

This repo contains both the **Android app** and **feature specification / planning** artifacts:

```text
spec-kit/
├── android/                 ← Android Studio project (Gradle module `:app`)
│   ├── app/
│   ├── build.gradle.kts
│   ├── settings.gradle.kts
│   └── gradlew / gradlew.bat
├── specs/
│   └── 001-android-tetris-clone/   ← spec, plan, tasks, research, contracts
├── .specify/                ← Spec Kit tooling config (optional for contributors)
└── README.md
```

Open **`android/`** in Android Studio as the Gradle project root.

---

## Build & run

### Prerequisites

- [Android Studio](https://developer.android.com/studio) (recommended: latest stable)
- JDK **17** (bundled with Android Studio is fine)
- Android SDK with API **35** (Android Studio will prompt if missing)

### Run on a device or emulator

1. Open the folder **`android`** in Android Studio.  
2. Let **Gradle sync** finish.  
3. Select a device or emulator.  
4. Click **Run**.

### Command line (from `android/`)

```bash
# Windows
.\gradlew.bat assembleDebug

# macOS / Linux
./gradlew assembleDebug
```

Debug APK output (typical path):

`android/app/build/outputs/apk/debug/app-debug.apk`

---

## Controls (current version)

Touch actions map to in-game moves (layout may evolve in future releases):

- **Left / Right** — move the active piece  
- **Rotate** — rotate when the placement is valid  
- **Drop** — soft drop (extra step downward)

---

## Roadmap (planned improvements)

Documented in feature specs; upcoming themes include **persistent high scores**, **fairer piece randomness (7-bag)**, and **control layout refinements**. See `specs/001-android-tetris-clone/`.

---

## Contributing / specs

If you use Spec Kit commands (optional), design artifacts live under `specs/001-android-tetris-clone/`. The implementation plan reference is noted in `.cursor/rules/specify-rules.mdc`.

---

## License

Personal project unless you add an explicit license file.  
If you open-source this repo, add a `LICENSE` (e.g. MIT) and update this section.

---

<p align="center">
  Made for quick sessions on a phone—no accounts, no servers, just blocks.
</p>
