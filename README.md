# Android Tetris Clone

A personal, offline **block-stacking game** for Android—classic handheld-style play with touch controls, built with **Kotlin** and **Jetpack Compose**.

<p align="center">
  <strong>Stack · Fall · Clear · Repeat</strong>
</p>

---

## Spec-driven development

This project is developed **from specifications**, not only from ad-hoc coding:

- **Feature spec** — User stories, functional requirements, success criteria, and assumptions live in [`specs/001-android-tetris-clone/spec.md`](specs/001-android-tetris-clone/spec.md).
- **Implementation plan** — Technical context and structure: [`specs/001-android-tetris-clone/plan.md`](specs/001-android-tetris-clone/plan.md).
- **Tasks** — Dependency-ordered work items: [`specs/001-android-tetris-clone/tasks.md`](specs/001-android-tetris-clone/tasks.md).
- **Design artifacts** — Research notes, data model, and gameplay contracts under [`specs/001-android-tetris-clone/`](specs/001-android-tetris-clone/) (including [`contracts/gameplay-contract.md`](specs/001-android-tetris-clone/contracts/gameplay-contract.md)).

The repo root also contains **Spec Kit** tooling under [`.specify/`](.specify/) and Cursor rule pointers in [`.cursor/rules/specify-rules.mdc`](.cursor/rules/specify-rules.mdc), so AI-assisted workflows can stay aligned with the same documents.

**Workflow in practice**: describe the product → capture **user stories** and **requirements** in `spec.md` → plan and tasks → implement in `android/` → update specs when behavior changes.

---

## User stories (from the feature spec)

Stories are **prioritized** so the core game ships first; later stories refine UX and persistence.

| Story | Priority | Summary |
|--------|-----------|---------|
| **US1** — Core gameplay | P1 | Play sessions with falling pieces, move/rotate, line clears, game over. |
| **US2** — Scoring | P2 | In-run score that increases with line clears; visible through game over. |
| **US3** — Quick replay | P3 | Restart after game over without leaving the app; clean board on restart. |
| **US4** — Control layout | P2 | Move Left / Move Right toward screen edges; Rotate and Soft Drop grouped and centered between them. |
| **US5** — Personal best | P2 | Best score **persisted on device**; optional **display name** when you beat your previous best. |

Detailed acceptance scenarios are in [`spec.md`](specs/001-android-tetris-clone/spec.md).

---

## Piece sequence logic (tetromino RNG)

The app uses a **7-bag randomizer** (common in guideline-style Tetris):

1. Each **bag** contains **exactly one of each** of the seven tetromino types.
2. The bag is **shuffled** using a **per-session** random source (not a fixed global seed), so cold starts do not always produce the same opening sequence.
3. Pieces are drawn from the bag until it is empty, then a **new bag** is filled and shuffled again.

This keeps drops **fair** (no long droughts of one shape) and **varied** across launches. Speed and difficulty from **gravity / levels** are separate from this RNG (future tuning).

Implementation: [`PieceGenerator.kt`](android/app/src/main/java/com/yorgo/tetris/game/PieceGenerator.kt) (`resetSession()` on each new game / restart from [`RestartLogic.kt`](android/app/src/main/java/com/yorgo/tetris/game/RestartLogic.kt)).

---

## What this version implements (high level)

### Gameplay engine (`domain` + `game`)

- **Board grid** (10×20), **collision**, **movement / rotation**, **lock**, **line clear**, **scoring** (deterministic line-clear points), **game over** when the next piece cannot spawn.
- **Tick-driven gravity** with input between ticks; engine notifies UI on state changes.
- **Pause / resume** hooks respect lifecycle so startup is not stuck in an invalid “running without a session” state.

### UI (`ui`)

- **Jetpack Compose** board that scales to available height while keeping **10 columns × 20 rows**.
- **Controls**: one full-width row — **Left** at the start edge, **Right** at the end edge, **Rotate** and **Drop** centered in the middle ([`ControlsPanel.kt`](android/app/src/main/java/com/yorgo/tetris/ui/ControlsPanel.kt)).
- **Score panel** for current score and **best** (with **name** when saved).
- **Game over**: normal restart dialog, or **new personal best** flow with name entry / anonymous ([`GameOverDialog.kt`](android/app/src/main/java/com/yorgo/tetris/ui/GameOverDialog.kt)).

### Persistence

- **Personal best score + player name** stored in **app-private SharedPreferences** ([`SharedPreferencesBestScoreStore.kt`](android/app/src/main/java/com/yorgo/tetris/data/SharedPreferencesBestScoreStore.kt)) so best survives **process death** and long idle periods unlike an in-memory store.

### Improvements over the initial prototype

| Area | Earlier behavior | Current behavior |
|------|------------------|-------------------|
| **RNG** | Fixed-seed uniform random (same early pieces every launch) | **7-bag** + **per-session shuffle** |
| **Best score** | In-memory only (lost when app killed) | **Persisted** locally with optional **name** on new best |
| **Controls** | Single compact row | **Edge-aligned** left/right, **centered** rotate + drop |
| **UI / engine** | Occasional empty board / no motion edge cases | Lifecycle-safe **pause/resume**, engine **listener** updates UI on ticks |
| **Specs** | — | **User stories FR-013–FR-016**, contracts, data model updated |

---

## Tech stack

| Layer | Choice |
|--------|--------|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Min SDK | 29 (Android 10+) |
| Target / compile SDK | 35 |
| ViewModel | `AndroidViewModel` + `StateFlow` for UI state |
| Persistence | `SharedPreferences` (personal best) |

---

## Repository layout

```text
spec-kit/
├── android/                 ← Android Studio project (Gradle module `:app`)
│   ├── app/
│   ├── build.gradle.kts
│   ├── settings.gradle.kts
│   └── gradlew / gradlew.bat
├── specs/
│   └── 001-android-tetris-clone/   ← spec, plan, tasks, research, contracts, checklists
├── .specify/                ← Spec Kit tooling config (optional)
├── .cursor/                 ← Cursor rules / skills (optional)
└── README.md
```

Open **`android/`** in Android Studio as the Gradle project root.

---

## Build & run

### Prerequisites

- [Android Studio](https://developer.android.com/studio) (recommended: latest stable)
- A **JDK** compatible with the Android Gradle Plugin (often **17** or **21** depending on AGP; use the JDK bundled with Android Studio or match [`android/gradle/gradle-daemon-jvm.properties`](android/gradle/gradle-daemon-jvm.properties) if you use Gradle from the command line).
- Android SDK with API **35** if prompted.

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

Typical debug APK output:

`android/app/build/outputs/apk/debug/app-debug.apk`

---

## Controls (current version)

| Control | Action |
|---------|--------|
| **Left** | Move active piece left (start edge of control row) |
| **Right** | Move active piece right (end edge of control row) |
| **Rotate** | Rotate when placement is valid (center, with Drop) |
| **Drop** | Soft drop (center, with Rotate) |

---

## Contributing / evolving the spec

When you change gameplay or UX meaningfully:

1. Update [`spec.md`](specs/001-android-tetris-clone/spec.md) (and optionally `plan.md`, `contracts/`, `data-model.md`).
2. Adjust [`tasks.md`](specs/001-android-tetris-clone/tasks.md) or add follow-up tasks.
3. Implement in `android/` and keep this README in sync for major behavior changes.

---

## License

Personal project unless you add an explicit license file.  
If you open-source this repo, add a `LICENSE` (e.g. MIT) and update this section.

---

<p align="center">
  Made for quick sessions on a phone—no accounts, no servers, just blocks.
</p>
