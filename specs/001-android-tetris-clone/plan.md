# Implementation Plan: Android Tetris Clone

**Branch**: `001-android-tetris-clone` | **Date**: 2026-04-27 | **Spec**: `specs/001-android-tetris-clone/spec.md`  
**Input**: Feature specification from `specs/001-android-tetris-clone/spec.md`

## Summary

Build a single-player Android game that reproduces classic handheld Tetris gameplay: falling pieces, movement/rotation, line clear logic, scoring, game-over detection, and immediate replay. The implementation centers on a deterministic game engine separated from rendering and touch input so core gameplay behavior is testable and stable across sessions.

## Technical Context

**Language/Version**: Kotlin 2.x (Android stable toolchain)  
**Primary Dependencies**: Android SDK, Jetpack Compose (UI), Kotlin coroutines (game loop timing)  
**Storage**: Local on-device preferences/file storage for optional best score persistence  
**Testing**: JUnit for domain logic, Android instrumentation tests for UI/input flows  
**Target Platform**: Android 10+ phones  
**Project Type**: Mobile app (single-module Android application for v1)  
**Performance Goals**: Maintain smooth interaction and visual updates equivalent to 60 frames per second during gameplay  
**Constraints**: Offline-first, single-player only, no backend services, responsive controls under 200ms perceived latency  
**Scale/Scope**: One personal-use app, single gameplay mode, one active session at a time

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

The current constitution file is a template with placeholders and defines no enforceable gates yet.  
Result: **PASS (provisional)** before research and **PASS (provisional)** after design.

## Project Structure

### Documentation (this feature)

```text
specs/001-android-tetris-clone/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
│   └── gameplay-contract.md
└── tasks.md
```

### Source Code (repository root)

```text
android/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/.../tetris/
│   │   │   │   ├── domain/
│   │   │   │   ├── game/
│   │   │   │   └── ui/
│   │   │   └── res/
│   │   ├── test/
│   │   └── androidTest/
│   └── build.gradle.kts
└── settings.gradle.kts
```

**Structure Decision**: Use a mobile-only project layout rooted at `android/app` with clear separation between domain logic (`domain/`, `game/`) and presentation/input (`ui/`). This matches the feature scope and avoids unnecessary backend or multi-platform complexity.

## Complexity Tracking

No constitution violations currently defined; no complexity exemptions required.

## Recent implementation alignment

Aligned with feature updates:

- **Controls**: single full-width row — Move Left at start edge, Move Right at end edge, Rotate and Soft Drop centered between them (`ControlsPanel`).
- **Personal best**: `SharedPreferences`-backed store for score + optional display name; name captured when a run beats the stored best (`SharedPreferencesBestScoreStore`, `GameOverDialog`).
- **Piece RNG**: 7-bag randomizer with per-session shuffle (`PieceGenerator.resetSession` from each new game/restart).
