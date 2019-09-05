package learnyouakotlin.part2

import learnyouakotlin.part1.Session

val Session?.subtitle: String?
    get() = this?.subtitle

fun Session.subtitleOrPrompt(): String = subtitle ?: "click to enter subtitle"

fun findWithTitle(sessions: List<Session>, title: String): Session? =
    sessions.find { it.title.equals(title, ignoreCase = true) }
