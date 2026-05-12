# Tasks: Android Tetris Clone

**Input**: Design documents from `specs/001-android-tetris-clone/`  
**Prerequisites**: `plan.md`, `spec.md`, `research.md`, `data-model.md`, `contracts/gameplay-contract.md`, `quickstart.md`

**Tests**: Not explicitly requested in the feature spec, so this task list focuses on implementation tasks only.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Every task includes an exact file path

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Initialize Android project skeleton and baseline app structure.

- [x] T001 Create Android project and module files in `android/settings.gradle.kts`, `android/build.gradle.kts`, and `android/app/build.gradle.kts`
- [x] T002 Create app manifest and base resources in `android/app/src/main/AndroidManifest.xml` and `android/app/src/main/res/values/strings.xml`
- [x] T003 [P] Create package structure placeholders in `android/app/src/main/java/com/yorgo/tetris/domain/.gitkeep`, `android/app/src/main/java/com/yorgo/tetris/game/.gitkeep`, and `android/app/src/main/java/com/yorgo/tetris/ui/.gitkeep`
- [x] T004 [P] Configure unit and instrumentation source sets in `android/app/src/test/.gitkeep` and `android/app/src/androidTest/.gitkeep`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Build core game engine primitives required by all user stories.

**⚠️ CRITICAL**: No user story work can begin until this phase is complete.

- [x] T005 Define core enums and constants for session status, actions, and board dimensions in `android/app/src/main/java/com/yorgo/tetris/domain/GameTypes.kt`
- [x] T006 Implement base domain models for board cells, points, and piece definitions in `android/app/src/main/java/com/yorgo/tetris/domain/GameModels.kt`
- [x] T007 Implement game state container and reducer entry points in `android/app/src/main/java/com/yorgo/tetris/game/GameState.kt`
- [x] T008 Implement deterministic tick scheduler abstraction in `android/app/src/main/java/com/yorgo/tetris/game/GameClock.kt`
- [x] T009 [P] Implement random piece provider abstraction with deterministic seed support in `android/app/src/main/java/com/yorgo/tetris/game/PieceGenerator.kt`
- [x] T010 Implement root game engine coordinator wiring clock, reducer, and piece generator in `android/app/src/main/java/com/yorgo/tetris/game/GameEngine.kt`

**Checkpoint**: Foundation ready - user story implementation can now begin.

---

## Phase 3: User Story 1 - Play core block-stacking game (Priority: P1) 🎯 MVP

**Goal**: Deliver complete playable gameplay loop with movement, rotation, locking, line clear, and game-over handling.

**Independent Test**: Launch app, start game, move/rotate pieces, clear at least one line, and reach game over.

### Implementation for User Story 1

- [x] T011 [P] [US1] Implement collision and boundary validation rules in `android/app/src/main/java/com/yorgo/tetris/game/CollisionRules.kt`
- [x] T012 [P] [US1] Implement movement and rotation transforms for active piece in `android/app/src/main/java/com/yorgo/tetris/game/PieceController.kt`
- [x] T013 [US1] Implement piece lock and board merge behavior in `android/app/src/main/java/com/yorgo/tetris/game/LockingLogic.kt`
- [x] T014 [US1] Implement line detection and row compaction logic in `android/app/src/main/java/com/yorgo/tetris/game/LineClearLogic.kt`
- [x] T015 [US1] Implement game-over detection on piece spawn failure in `android/app/src/main/java/com/yorgo/tetris/game/GameOverRules.kt`
- [x] T016 [US1] Integrate tick update pipeline into engine reducer flow in `android/app/src/main/java/com/yorgo/tetris/game/GameEngine.kt`
- [x] T017 [US1] Create game screen state holder and start action handling in `android/app/src/main/java/com/yorgo/tetris/ui/GameViewModel.kt`
- [x] T018 [US1] Render board grid and active piece preview in `android/app/src/main/java/com/yorgo/tetris/ui/GameScreen.kt`
- [x] T019 [US1] Add touch controls for move left, move right, rotate, and soft drop in `android/app/src/main/java/com/yorgo/tetris/ui/ControlsPanel.kt`

**Checkpoint**: User Story 1 is fully playable and independently testable.

---

## Phase 4: User Story 2 - Keep score like classic handheld play (Priority: P2)

**Goal**: Add deterministic scoring during gameplay and display score through active and game-over states.

**Independent Test**: Play a session, clear lines, verify score increases correctly, then verify final score remains visible at game over.

### Implementation for User Story 2

- [x] T020 [P] [US2] Implement score calculation policy based on cleared lines in `android/app/src/main/java/com/yorgo/tetris/game/ScoreCalculator.kt`
- [x] T021 [US2] Integrate scoring updates into lock-and-clear pipeline in `android/app/src/main/java/com/yorgo/tetris/game/GameEngine.kt`
- [x] T022 [US2] Extend view model with score state projection in `android/app/src/main/java/com/yorgo/tetris/ui/GameViewModel.kt`
- [x] T023 [US2] Add score HUD and final score panel rendering in `android/app/src/main/java/com/yorgo/tetris/ui/ScorePanel.kt`
- [x] T024 [US2] Add optional best score persistence storage and retrieval in `android/app/src/main/java/com/yorgo/tetris/domain/BestScoreStore.kt`

**Checkpoint**: User Stories 1 and 2 both work independently with visible deterministic scoring.

---

## Phase 5: User Story 3 - Replay quickly on a phone (Priority: P3)

**Goal**: Provide immediate restart flow after game over with full state reset and smooth replay loop.

**Independent Test**: Reach game-over and restart without app relaunch; verify fresh board and reset in-session progress.

### Implementation for User Story 3

- [x] T025 [US3] Implement restart action reset logic for session, board, and active piece in `android/app/src/main/java/com/yorgo/tetris/game/RestartLogic.kt`
- [x] T026 [US3] Wire restart event through engine and view model command handling in `android/app/src/main/java/com/yorgo/tetris/ui/GameViewModel.kt`
- [x] T027 [US3] Add game-over restart affordance and interaction handling in `android/app/src/main/java/com/yorgo/tetris/ui/GameOverDialog.kt`
- [x] T028 [US3] Ensure restart reinitializes tick flow without stale state leaks in `android/app/src/main/java/com/yorgo/tetris/game/GameClock.kt`

**Checkpoint**: All three user stories are independently functional.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Improve overall reliability and readiness across all stories.

- [x] T029 [P] Refine pause/resume interruption handling for app lifecycle transitions in `android/app/src/main/java/com/yorgo/tetris/ui/AppLifecycleObserver.kt`
- [x] T030 [P] Tune input responsiveness and drop timing constants in `android/app/src/main/java/com/yorgo/tetris/game/GameTuning.kt`
- [x] T031 Add app entry wiring and navigation-free single-screen bootstrap in `android/app/src/main/java/com/yorgo/tetris/MainActivity.kt`
- [x] T032 Validate quickstart flow and update implementation notes in `specs/001-android-tetris-clone/quickstart.md`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Phase 1 (Setup)**: No dependencies, start immediately.
- **Phase 2 (Foundational)**: Depends on Phase 1 completion and blocks all user stories.
- **Phase 3 (US1)**: Depends on Phase 2 completion.
- **Phase 4 (US2)**: Depends on Phase 2 completion and integrates with US1 engine pipeline.
- **Phase 5 (US3)**: Depends on Phase 2 completion and reuses US1 game-over/session flow.
- **Phase 6 (Polish)**: Depends on completion of desired user stories.

### User Story Dependencies

- **US1 (P1)**: No dependency on other user stories; true MVP slice.
- **US2 (P2)**: Depends on gameplay events produced by US1 but remains independently testable once integrated.
- **US3 (P3)**: Depends on game-over/session model from US1; independently testable as restart-focused behavior.

### Within Each User Story

- Engine/domain logic before UI binding.
- UI rendering before control polish.
- Story checkpoint validation before moving to next priority.

### Parallel Opportunities

- Setup tasks `T003` and `T004` can run in parallel.
- Foundational tasks `T009` can run in parallel with `T008` after `T005` and `T006`.
- In US1, `T011` and `T012` can run in parallel.
- In US2, `T020` and `T024` can run in parallel.
- In Polish, `T029` and `T030` can run in parallel.

---

## Parallel Example: User Story 1

```bash
Task: "Implement collision and boundary validation rules in android/app/src/main/java/com/yorgo/tetris/game/CollisionRules.kt"
Task: "Implement movement and rotation transforms for active piece in android/app/src/main/java/com/yorgo/tetris/game/PieceController.kt"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1 setup.
2. Complete Phase 2 foundational engine tasks.
3. Complete Phase 3 (US1) playable loop.
4. Validate US1 independently before expanding scope.

### Incremental Delivery

1. Deliver US1 for core gameplay.
2. Add US2 for scoring while preserving US1 behavior.
3. Add US3 for rapid replay flow.
4. Finish with Phase 6 polish tasks.

### Parallel Team Strategy

1. One developer handles setup/foundation.
2. After foundation, split work:
   - Developer A: US1 gameplay logic integration
   - Developer B: US2 scoring and display
   - Developer C: US3 restart and replay UX
3. Merge at phase checkpoints and run independent story validation.

---

## Notes

- `[P]` tasks are safe parallel candidates because they target different files and do not require incomplete predecessors.
- Each story phase is scoped to remain independently testable.
- Suggested MVP scope: **Phase 1 + Phase 2 + Phase 3 (US1)**.
