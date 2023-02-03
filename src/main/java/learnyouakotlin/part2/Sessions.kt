@file:JvmName("Sessions")
package learnyouakotlin.part2

import learnyouakotlin.part1.Session

fun subtitleOf(session: Session?): String? =
    session?.subtitle

fun subtitleOrPrompt(session: Session): String =
    session.subtitle ?: "click to enter subtitle"

fun findWithTitle(sessions: List<Session?>, title: String?): Session? =
    sessions.stream()
        .filter { session: Session? -> session!!.title.equals(title, ignoreCase = true) }
        .findFirst()
        .orElse(null)