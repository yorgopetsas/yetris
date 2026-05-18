# Data Model: Android Tetris Clone

## Entity: GameSession
- **Purpose**: Represents one complete play run from start to game-over.
- **Fields**:
  - `sessionId` (string): Unique identifier for the run.
  - `status` (enum): `READY`, `RUNNING`, `PAUSED`, `GAME_OVER`.
  - `score` (integer): Current score (non-negative).
  - `linesCleared` (integer): Total lines cleared in this session.
  - `level` (integer): Current speed/level progression value.
  - `startedAt` (timestamp): Session start time.
  - `endedAt` (timestamp, nullable): Session end time when game-over occurs.
- **Validation Rules**:
  - `score >= 0`
  - `linesCleared >= 0`
  - `level >= 1`
  - `endedAt` must exist only when status is `GAME_OVER`.

## Entity: GameBoard
- **Purpose**: Grid representing occupied/empty playfield cells.
- **Fields**:
  - `width` (integer): Board width in cells.
  - `height` (integer): Board height in cells.
  - `cells` (matrix of cell state): Each cell is empty or occupied by a piece block.
- **Validation Rules**:
  - Dimensions are fixed during a session.
  - Cell coordinates must remain inside board boundaries.
  - Locking a piece cannot overwrite occupied cells.

## Entity: ActivePiece
- **Purpose**: Current falling piece controlled by the player.
- **Fields**:
  - `pieceType` (enum): One of the supported classic piece shapes.
  - `rotation` (enum/integer): Current orientation state.
  - `originX` (integer): Horizontal position on board.
  - `originY` (integer): Vertical position on board.
  - `blockOffsets` (list of coordinate offsets): Shape definition relative to origin.
- **Validation Rules**:
  - All transformed block coordinates must be valid board positions or trigger collision rejection.
  - Rotation/movement is applied only if resulting placement is valid.

## Entity: InputAction
- **Purpose**: Captures player actions affecting the current game state.
- **Fields**:
  - `actionType` (enum): `MOVE_LEFT`, `MOVE_RIGHT`, `ROTATE`, `SOFT_DROP`, `RESTART`, `PAUSE`, `RESUME`.
  - `issuedAtTick` (integer): Tick index when action was processed.
- **Validation Rules**:
  - Actions ignored when incompatible with current session status (for example move while game-over).
  - `RESTART` creates a fresh `GameSession` and clears prior board state.

## Entity: Personal Best Record (Persisted)
- **Purpose**: Stores the player’s best score and optional display name on device storage so it survives app restarts.
- **Fields**:
  - `bestScore` (integer): Highest score achieved.
  - `playerName` (string): Display name associated with the best score (may be empty before first named record).
  - `updatedAt` (timestamp, optional): Time best score last changed.
- **Validation Rules**:
  - `bestScore >= 0`
  - `playerName` trimmed to a maximum length (implementation-defined, e.g. 24 characters).
  - Update best score and name only when a completed session records a score strictly greater than stored `bestScore`.

## Entity: Saved Game Snapshot (Persisted)
- **Purpose**: Stores a pausable in-progress session for long-term resume.
- **Fields**:
  - `version` (integer): Schema version.
  - `savedAtEpochMillis` (long): When pause was saved.
  - `session` (embedded): `GameSession` at save time (status `PAUSED`).
  - `bagQueue` (list of piece types): Remaining spawn order from 7-bag.
- **Validation Rules**:
  - Saved only from `RUNNING` or `PAUSED` via explicit Pause control.
  - Cleared on new game, restart, or after game-over restart flow.

## Entity: Friends Leaderboard Entry (Remote, cached)
- **Purpose**: Display name and score for friends top-N list.
- **Fields**:
  - `name` (string): Display name.
  - `score` (integer): Best score for that name on the server.
- **Validation Rules**:
  - Server keeps highest score per name (case-insensitive).

## Relationships
- A `GameSession` contains one `GameBoard`.
- A `GameSession` has zero or one `ActivePiece` at any given moment.
- A `GameSession` processes many `InputAction` events over time.
- A `Personal Best Record` is global to the app and updated based on completed `GameSession` results when the score beats the prior best.

## State Transitions
- **Session status**:
  - `READY -> RUNNING` on game start.
  - `RUNNING -> PAUSED` on app interruption or pause action.
  - `PAUSED -> RUNNING` on resume.
  - `RUNNING -> GAME_OVER` when next piece spawn is invalid.
  - `GAME_OVER -> RUNNING` on restart (new session instance).
- **Piece lifecycle**:
  - `SPAWNED -> FALLING -> LOCKED -> CLEARED/SETTLED -> NEXT_SPAWN`.
