# Saved game snapshot contract

Persisted locally in Android `SharedPreferences` as JSON (schema version 1).

## Fields

| Field | Type | Description |
|-------|------|-------------|
| `version` | int | Must be `1` |
| `savedAt` | long | Epoch millis when pause was saved |
| `status` | string | `SessionStatus` name; always `PAUSED` on save |
| `score` | int | Current session score |
| `linesCleared` | int | Lines cleared this session |
| `level` | int | Speed level |
| `tick` | long | Game tick counter |
| `board` | bool[][] | 20×10 occupied cells |
| `bagQueue` | string[] | Remaining `PieceType` names in spawn order |
| `activePiece` | object? | `{ type, originX, originY, rotation }` or null |

## Behavior

- Written only when the player taps **Pause** or **Save pause** (not on app background).
- Cleared on **New game**, **Restart**, or successful new-game start after game over.
- On cold start, if a snapshot exists, show **Continue** / **New game**; Continue restores `PAUSED` state.

## Migration

Increment `SavedGameSnapshot.CURRENT_VERSION` and add decode branch when schema changes.
