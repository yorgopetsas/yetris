# Feature Specification: Android Tetris Clone

**Feature Branch**: `001-android-tetris-clone`  
**Created**: 2026-04-27  
**Status**: Draft  
**Input**: User description: "I want to make a app for my Android phone, only for personal use. The app has to be duiplicate of the old tetris game that used to be sold a an electronic game in a format like a gameboy."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Play core block-stacking game (Priority: P1)

As the player, I can start a game session and continuously place falling block pieces to clear lines and keep playing until the board is full.

**Why this priority**: This is the core value of the feature; without playable gameplay there is no usable product.

**Independent Test**: Can be fully tested by launching the app, starting a game, moving and rotating pieces, clearing at least one line, and reaching game over.

**Acceptance Scenarios**:

1. **Given** the player opens the app, **When** they start a new game, **Then** a game board appears and falling pieces begin.
2. **Given** a piece is falling, **When** the player moves or rotates it, **Then** the piece updates position/orientation within valid board boundaries.
3. **Given** a horizontal row is fully occupied, **When** the active piece locks, **Then** that row is cleared and the stack above shifts down.
4. **Given** the board reaches a state where a new piece cannot be placed, **When** the next piece is due, **Then** the game ends and the session is marked game over.

---

### User Story 2 - Keep score like classic handheld play (Priority: P2)

As the player, I can see an in-game score that increases as I clear lines so I can track performance during each run.

**Why this priority**: Score feedback is essential to the classic game feel and motivates replay, but the game remains playable without advanced score features.

**Independent Test**: Can be tested by playing a session, clearing lines, and verifying score increases in response to line clears.

**Acceptance Scenarios**:

1. **Given** a new game starts, **When** no lines have been cleared, **Then** score is shown at its starting value.
2. **Given** the player clears one or more lines, **When** the board updates, **Then** score increases according to the defined scoring rules.
3. **Given** the player reaches game over, **When** the game ends, **Then** the final score remains visible until the player starts over or exits.

---

### User Story 3 - Replay quickly on a phone (Priority: P3)

As the player, I can restart immediately after game over so I can do repeated quick sessions on my Android phone.

**Why this priority**: Fast restart improves playability for personal repeated use, but it is secondary to core gameplay and scoring.

**Independent Test**: Can be tested by playing to game over and starting another session without closing the app.

**Acceptance Scenarios**:

1. **Given** the game is over, **When** the player chooses to restart, **Then** a fresh game board loads and gameplay resumes from initial state.
2. **Given** a fresh game starts after restart, **When** the first piece appears, **Then** prior board state and prior in-session progress are cleared.

---

### Edge Cases

- What happens when the player provides rapid repeated input while a piece is locking?
- How does the game handle situations where multiple lines are completed at once?
- What happens if the app is interrupted (for example minimized or phone screen turned off) during an active session?
- How does the system behave when the player tries to rotate a piece against a wall or stacked blocks?

### User Story 4 - Controls layout on phone (Priority: P2)

As the player, I want clearly separated controls so left/right sit at the screen edges and rotate/drop sit centered for comfortable thumb reach.

**Independent Test**: Open game controls strip and verify positions match layout rules without overlapping the board incorrectly.

**Acceptance Scenarios**:

1. **Given** gameplay controls are visible, **When** the player views the control row, **Then** Move Left is aligned toward the start edge and Move Right toward the end edge of the row.
2. **Given** gameplay controls are visible, **When** the player views the center region, **Then** Rotate and Drop appear grouped and horizontally centered between the outer buttons.

---

### User Story 5 - Persistent best score and optional name (Priority: P2)

As the player, I want my best score to persist across app restarts and optionally record my name when I beat my previous best.

**Independent Test**: Achieve a high score, restart the device or force-close the app, reopen and confirm best score and label remain.

**Acceptance Scenarios**:

1. **Given** the stored best score exists, **When** the player launches the app after a long idle period or cold start, **Then** the best score (and associated display name if saved) still appears.
2. **Given** game over occurs with a score strictly greater than the stored best, **When** the player completes the end-game prompt, **Then** they may enter a short display name and the new best is saved locally.

---

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The system MUST provide a single-player block-stacking game loop where pieces fall continuously until game over.
- **FR-002**: The system MUST allow the player to move active pieces left and right while falling.
- **FR-003**: The system MUST allow the player to rotate active pieces and prevent invalid placements outside the board or inside occupied cells.
- **FR-004**: The system MUST place active pieces into the board when they can no longer descend.
- **FR-005**: The system MUST detect and clear any completed horizontal lines after piece placement.
- **FR-006**: The system MUST update the board state after line clears so remaining blocks settle into valid positions.
- **FR-007**: The system MUST track and display score throughout each game session.
- **FR-008**: The system MUST define deterministic score increases for line clears and apply them consistently.
- **FR-009**: The system MUST detect game-over state when a new piece cannot be spawned in a valid location.
- **FR-010**: The system MUST provide a restart action that begins a new session from a clean initial state after game over.
- **FR-011**: The system MUST support interaction via Android touch controls appropriate for move, rotate, and drop actions.
- **FR-012**: The system MUST run as a standalone personal-use mobile app and does not require user accounts, sharing, or online multiplayer features.
- **FR-013**: The system MUST lay out primary gameplay controls in one row such that Move Left is toward the screen start edge, Move Right toward the screen end edge, and Rotate with Soft Drop grouped and visually centered between them.
- **FR-014**: The system MUST persist the personal best score on device storage so it survives process death and long idle periods.
- **FR-015**: When a session ends with a score strictly greater than the stored best, the system MUST offer entry of a short display name (trimmed, length-limited) before treating the new score as the recorded best.
- **FR-016**: The piece sequence SHALL use a guideline-style **7-bag** randomizer with **per-session** shuffling; it MUST NOT rely on a fixed global random seed across app launches.

### Key Entities *(include if feature involves data)*

- **Game Session**: A single run from game start to game over; includes current board, active piece, score, and state (active or ended).
- **Game Board**: A fixed-size grid that stores occupied and empty cells and is used to determine collisions and line completion.
- **Piece**: A falling shape with type, orientation, and position that can be moved/rotated until locked.
- **Score Record**: Numeric value representing player performance for the current session based on cleared lines.
- **Personal Best Record**: Locally persisted best score paired with an optional display name for presentation on the score panel.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Player can start a game from app launch and begin active gameplay in under 10 seconds.
- **SC-002**: In 95% of test sessions, player inputs for movement/rotation result in visible game response within 0.2 seconds.
- **SC-003**: 100% of completed lines are removed correctly during validation tests, with no missed or false line clears.
- **SC-004**: In end-to-end testing, at least 9 out of 10 sessions can be restarted successfully from game-over state without relaunching the app.
- **SC-005**: Player can complete at least three consecutive full game sessions without crashes or forced restarts.

## Assumptions

- The app targets a single personal player and does not need multi-user profiles or cloud synchronization.
- The intended experience mirrors classic handheld gameplay behavior rather than modern variants with extra modes.
- Audio, advanced animations, and visual theming are optional enhancements and are out of initial scope unless needed for basic usability.
- The app is expected to work in portrait orientation on a typical Android phone screen.
