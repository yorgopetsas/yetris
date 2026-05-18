/**
 * Google Apps Script for Tetris friends leaderboard.
 * 1. Create a Sheet with columns: name | score | updatedAt
 * 2. Extensions → Apps Script → paste this file
 * 3. Set LEADERBOARD_TOKEN below, Deploy → Web app (Execute as: Me, Anyone)
 * 4. Put deployment URL and token in android/local.properties
 */
const LEADERBOARD_TOKEN = 'change-me-to-a-random-secret';
const SHEET_NAME = 'Scores';

function doGet(e) {
  if (!authorized_(e)) return jsonResponse_({ error: 'unauthorized' }, 403);
  const limit = Math.min(parseInt(e.parameter.limit || '10', 10), 50);
  const rows = readScores_();
  const top = rows
    .sort((a, b) => b.score - a.score)
    .slice(0, limit)
    .map((r) => ({ name: r.name, score: r.score }));
  return jsonResponse_(top);
}

function doPost(e) {
  if (!authorized_(e)) return jsonResponse_({ error: 'unauthorized' }, 403);
  const body = JSON.parse(e.postData.contents || '{}');
  const name = String(body.name || '').trim();
  const score = parseInt(body.score, 10);
  if (!name || isNaN(score)) return jsonResponse_({ error: 'invalid' }, 400);
  upsertScore_(name, score);
  return jsonResponse_({ ok: true });
}

function authorized_(e) {
  return e && e.parameter && e.parameter.token === LEADERBOARD_TOKEN;
}

function upsertScore_(name, score) {
  const sheet = SpreadsheetApp.getActiveSpreadsheet().getSheetByName(SHEET_NAME);
  const data = sheet.getDataRange().getValues();
  const key = name.toLowerCase();
  let rowIndex = -1;
  for (let i = 1; i < data.length; i++) {
    if (String(data[i][0] || '').trim().toLowerCase() === key) {
      rowIndex = i + 1;
      break;
    }
  }
  const now = new Date().toISOString();
  if (rowIndex > 0) {
    const existing = parseInt(data[rowIndex - 1][1], 10) || 0;
    if (score <= existing) return;
    sheet.getRange(rowIndex, 1, rowIndex, 3).setValues([[name, score, now]]);
  } else {
    sheet.appendRow([name, score, now]);
  }
}

function readScores_() {
  const sheet = SpreadsheetApp.getActiveSpreadsheet().getSheetByName(SHEET_NAME);
  const data = sheet.getDataRange().getValues();
  const byName = {};
  for (let i = 1; i < data.length; i++) {
    const name = String(data[i][0] || '').trim();
    const score = parseInt(data[i][1], 10) || 0;
    if (!name) continue;
    const key = name.toLowerCase();
    if (!byName[key] || score > byName[key].score) {
      byName[key] = { name: name, score: score };
    }
  }
  return Object.keys(byName).map((k) => byName[k]);
}

function jsonResponse_(obj, code) {
  const output = ContentService.createTextOutput(JSON.stringify(obj));
  output.setMimeType(ContentService.MimeType.JSON);
  return output;
}
