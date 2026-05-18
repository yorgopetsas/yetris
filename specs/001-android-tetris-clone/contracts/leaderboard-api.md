# Friends leaderboard API contract

Base URL: Google Apps Script web app deployment URL (ends with `/exec`).

All requests require query parameter `token` matching server `LEADERBOARD_TOKEN`.

## GET — fetch top scores

`GET {baseUrl}?token={token}&limit={n}`

- `limit` optional, default 10, max 50.

**Response** `200 application/json`:

```json
[
  { "name": "Alex", "score": 12000 },
  { "name": "Sam", "score": 9500 }
]
```

## GET — submit score (recommended)

Browsers block many `POST` calls to Apps Script; use GET:

`GET {baseUrl}?token={token}&action=submit&name={name}&score={score}`

**Response** `200 application/json`:

```json
{ "ok": true }
```

`POST` with JSON body is still supported by the script for non-browser clients but GET is preferred.

Server keeps one row per player name (case-insensitive); updates only if new score is strictly greater.

## Android configuration

In `android/local.properties` (not committed):

```properties
LEADERBOARD_URL=https://script.google.com/macros/s/XXXX/exec
LEADERBOARD_TOKEN=your-random-secret
```

If unset, the app skips network calls and shows cached or empty leaderboard.
