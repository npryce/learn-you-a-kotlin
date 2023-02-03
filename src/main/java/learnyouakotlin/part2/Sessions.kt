@file:JvmName("Sessions")
package learnyouakotlin.part2

import learnyouakotlin.part1.Session

fun Session?.subtitleOf(): String? =
    this?.subtitle

fun Session.subtitleOrPrompt(): String =
    subtitle ?: "click to enter subtitle"

fun findWithTitle(sessions: List<Session?>, title: String?): Session? =
    sessions.stream()
        .filter { session: Session? -> session!!.title.equals(title, ignoreCase = true) }
        .findFirst()
        .orElse(null)