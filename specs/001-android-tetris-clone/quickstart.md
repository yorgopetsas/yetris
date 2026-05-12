# Quickstart: Android Tetris Clone

## Goal
Implement and verify the Android personal-use Tetris clone defined in `spec.md` and planned in `plan.md`.

## 1) Prepare Environment
1. Install Android Studio (stable channel) with Android SDK for API level matching Android 10+.
2. Ensure JDK version required by the Android Gradle plugin is installed.
3. Open the repository in Android Studio.

## 2) Create Project Skeleton
1. Add Android app structure under `android/app`.
2. Set up package namespaces for:
   - `domain` (entities and rules)
   - `game` (game loop and state transitions)
   - `ui` (rendering and touch controls)
3. Configure test source sets:
   - `android/app/src/test`
   - `android/app/src/androidTest`

## 3) Implement Core Gameplay (MVP)
1. Implement board representation and piece models.
2. Implement deterministic update loop:
   - gravity tick
   - collision checks
   - piece lock
   - line clear
   - spawn next piece
3. Implement score updates and game-over detection.
4. Add restart flow from game-over to fresh session.

## 4) Implement Android UI and Controls
1. Render board, active piece, and score.
2. Add touch controls for left/right/rotate/soft drop.
3. Add start/restart actions and game-over presentation.
4. Handle interruption states (pause/resume) safely.

## 5) Verification Checklist
1. Run unit tests for:
   - line-clear correctness
   - rotation and boundary behavior
   - collision and lock rules
   - deterministic score updates
2. Run instrumentation tests for:
   - touch input responsiveness
   - restart flow from game-over
   - interruption handling (background/resume)
3. Manually validate:
   - game starts within target startup window
   - controls feel responsive
   - three full sessions complete without crash

## 6) Ready for Task Generation
After this plan, run `/speckit.tasks` to generate dependency-ordered implementation tasks.

## 7) Implementation Notes (Current Status)
1. Android project skeleton has been created under `android/app`.
2. Core engine files for collision, piece movement, locking, line clear, scoring, game-over, and restart are implemented.
3. Compose UI files for game board, controls, score, and game-over restart flow are implemented.
4. Lifecycle pause/resume observer and single-screen app bootstrap are implemented.
