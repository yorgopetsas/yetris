package com.yorgo.tetris.leaderboard

import com.yorgo.tetris.jsConsoleError
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

suspend fun submitScore(name: String, score: Int): Boolean {
    if (!WebLeaderboardConfig.isConfigured()) return false
    val fullUrl =
        "${WebLeaderboardConfig.url()}?token=${encodeUriComponent(WebLeaderboardConfig.token())}" +
            "&action=submit&name=${encodeUriComponent(name)}&score=$score"
    return suspendCoroutine { cont ->
        window.asDynamic().fetch(fullUrl).then { response: dynamic ->
            val status = response.status as Int
            if (status !in 200..299) {
                jsConsoleError("Leaderboard submit failed: HTTP $status")
                cont.resume(false)
                return@then undefined
            }
            response.json().then { json: dynamic ->
                val ok = json.ok == true
                if (!ok) jsConsoleError("Leaderboard submit rejected: $json")
                cont.resume(ok)
                undefined
            }.catch { _ ->
                jsConsoleError("Leaderboard submit parse error")
                cont.resume(false)
                undefined
            }
            undefined
        }.catch { _ ->
            jsConsoleError("Leaderboard submit network error")
            cont.resume(false)
            undefined
        }
    }
}
