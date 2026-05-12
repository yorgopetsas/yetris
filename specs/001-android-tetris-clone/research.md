# Research: Android Tetris Clone

## Decision 1: App architecture for gameplay logic
- **Decision**: Use a deterministic, platform-independent game engine layer separated from UI rendering and input handlers.
- **Rationale**: This keeps piece movement, collision checks, line-clear logic, scoring, and game-over rules testable without device UI dependencies.
- **Alternatives considered**:
  - Embed all logic directly in UI state management (rejected: harder to test and maintain).
  - Use a third-party game engine framework (rejected: unnecessary complexity for single-mode personal app).

## Decision 2: UI and input approach
- **Decision**: Use touch-first controls with explicit actions for move left/right, rotate, soft drop, and restart.
- **Rationale**: Matches phone ergonomics and keeps controls understandable while preserving classic gameplay behavior.
- **Alternatives considered**:
  - Gesture-only controls (rejected: less precise for repeated rapid actions).
  - Hardware-key style mapping (rejected: inconsistent across Android devices).

## Decision 3: Tick/timing model
- **Decision**: Use a fixed-interval game tick with deterministic state updates, plus immediate processing for player input events.
- **Rationale**: Provides predictable gameplay speed progression and avoids inconsistent behavior caused by variable frame times.
- **Alternatives considered**:
  - Frame-time-driven simulation (rejected: increased complexity for timing drift compensation).
  - Turn-based step model (rejected: does not match real-time Tetris expectations).

## Decision 4: Data persistence scope
- **Decision**: Keep persistence minimal for v1: optional best score only; no cloud sync, no user account data.
- **Rationale**: Aligns with personal-use scope and avoids introducing network/system dependencies outside core gameplay.
- **Alternatives considered**:
  - Full session history persistence (rejected: lower value than gameplay polish for initial release).
  - No persistence at all (considered acceptable fallback if time-constrained).

## Decision 5: Testing strategy
- **Decision**: Prioritize automated domain tests for gameplay correctness and add focused device/instrumentation tests for input and lifecycle behavior.
- **Rationale**: Most risk lies in game-rule correctness (line clears, rotation constraints, lock behavior, game over) and interruption/restart handling.
- **Alternatives considered**:
  - UI-heavy manual-only testing (rejected: slower regression detection).
  - Full end-to-end only strategy (rejected: slower feedback cycle and harder fault isolation).
