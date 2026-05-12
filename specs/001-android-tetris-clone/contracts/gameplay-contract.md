# Gameplay Contract: Android Tetris Clone

## Purpose
Define the expected behavior contract between input handling, game engine updates, and UI rendering for the single-player Tetris experience.

## Session Lifecycle Contract
- The system MUST expose actions to `start`, `pause`, `resume`, and `restart` a game session.
- Starting or restarting a session MUST initialize a clean board, reset active-piece state, and set score-related counters to initial values.
- The system MUST expose a read-only session state for UI display (`READY`, `RUNNING`, `PAUSED`, `GAME_OVER`).

## Input Contract
- Supported gameplay actions:
  - `MOVE_LEFT`
  - `MOVE_RIGHT`
  - `ROTATE`
  - `SOFT_DROP`
  - `RESTART`
- Invalid actions for the current session state MUST be ignored safely (no crash, no corrupted state).
- Input processing MUST be deterministic: same prior state + same action sequence -> same resulting state.

## Tick Update Contract
- The game engine MUST process periodic ticks while the session is `RUNNING`.
- On each tick, the engine MUST either:
  - move the active piece down one step when valid, or
  - lock the piece, clear complete lines, update score, and spawn next piece.
- If next-piece spawn is invalid, engine MUST transition session to `GAME_OVER`.

## Board and Collision Contract
- All piece placements MUST remain within board boundaries.
- A piece MUST NOT overlap occupied cells.
- Completed horizontal lines MUST be detected and removed in the same update cycle that locks a piece.
- Blocks above cleared lines MUST settle downward consistently.

## Scoring Contract
- Score MUST be visible during active gameplay and after game-over.
- Score updates MUST happen only through defined scoring events (line clears and any explicitly defined bonuses).
- Score progression MUST be deterministic and reproducible for the same gameplay event sequence.

## Personal Best Contract
- The system MUST persist the highest achieved score locally across launches.
- When a session ends with a score greater than the stored best, the player MUST be able to submit a short display name before the new best is recorded (or an equivalent default label if skipped).
- The score panel MUST surface both stored best score and stored display name when available.

## Piece Sequence Contract
- Tetromino draws MUST follow a 7-bag randomizer: each bag contains exactly one of each piece type; the bag is shuffled using per-session entropy and drawn until empty before refilling.
- Piece sequence MUST NOT depend on a fixed global seed identical on every cold app launch.

## Rendering Contract
- UI MUST reflect the latest committed engine state after each processed tick or input action.
- Restart MUST render a new clean board before gameplay continues.
- Game-over state MUST provide clear visual indication and restart affordance.

## Error-Handling Contract
- Runtime interruptions (backgrounding/screen off) MUST not corrupt current session state.
- If recovery from interruption is unsupported in a context, the app MUST fail gracefully to a safe state (paused or restart prompt), never an undefined board state.
