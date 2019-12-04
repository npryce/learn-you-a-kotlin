package learnyouakotlin.part2

import learnyouakotlin.part1.Session

val Session.subtitleOrPrompt: String
    get() = this.subtitle ?: "click to enter subtitle"

fun findWithTitle(sessions: List<Session>, title: String): Session? {
    return sessions.firstOrNull { (title1) -> title1.equals(title, ignoreCase = true) }
}