package com.yorgo.tetris.leaderboard

import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlinx.browser.window

data class WebLeaderboardEntry(
    val name: String,
    val score: Int
)

object WebLeaderboardConfig {
    fun url(): String =
        (window.asDynamic().YETRIS_LEADERBOARD_URL as? String).orEmpty().trim()

    fun token(): String =
        (window.asDynamic().YETRIS_LEADERBOARD_TOKEN as? String).orEmpty().trim()

    fun isConfigured(): Boolean = url().isNotEmpty() && token().isNotEmpty()
}

@Suppress("UNUSED_PARAMETER")
private fun encodeUriComponent(value: String): String =
    js("encodeURIComponent")(value) as String

suspend fun fetchTopScores(limit: Int = 10): List<WebLeaderboardEntry> {
    if (!WebLeaderboardConfig.isConfigured()) return emptyList()
    val fullUrl =
        "${WebLeaderboardConfig.url()}?token=${encodeUriComponent(WebLeaderboardConfig.token())}&limit=$limit"
    return suspendCoroutine { cont ->
        window.asDynamic().fetch(fullUrl).then { response: dynamic ->
            val status = response.status as Int
            if (status !in 200..299) {
                cont.resume(emptyList())
                return@then undefined
            }
            response.json().then { json: dynamic ->
                val raw = json as? Array<dynamic> ?: run {
                    cont.resume(emptyList())
                    return@then undefined
                }
                cont.resume(
                    raw.map { row ->
                        WebLeaderboardEntry(
                            name = row.name as String,
                            score = (row.score as Number).toInt()
                        )
                    }
                )
                undefined
            }.catch {
                cont.resume(emptyList())
                undefined
            }
            undefined
        }.catch {
            cont.resume(emptyList())
            undefined
        }
    }
}

suspend fun submitScore(name: String, score: Int) {
    if (!WebLeaderboardConfig.isConfigured()) return
    val fullUrl =
        "${WebLeaderboardConfig.url()}?token=${encodeUriComponent(WebLeaderboardConfig.token())}"
    val body = """{"name":${jsonString(name)},"score":$score}"""
    suspendCoroutine { cont ->
        val opts = js("{}")
        opts.method = "POST"
        val headers = js("{}")
        headers["Content-Type"] = "application/json"
        opts.headers = headers
        opts.body = body
        window.asDynamic().fetch(fullUrl, opts).then {
            cont.resume(Unit)
            undefined
        }.catch {
            cont.resume(Unit)
            undefined
        }
    }
}

private fun jsonString(value: String): String =
    "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\""
