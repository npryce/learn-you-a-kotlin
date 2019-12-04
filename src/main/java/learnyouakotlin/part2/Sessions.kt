package learnyouakotlin.part2

import learnyouakotlin.part1.Session

fun subtitleOf(session: Session?): String? {
    return session?.subtitle
}

fun subtitleOrPrompt(session: Session): String {
    return session.subtitle ?: "click to enter subtitle"
}

fun findWithTitle(sessions: List<Session>, title: String): Session? {
    return sessions.stream()
        .filter { (title1) -> title1.equals(title, ignoreCase = true) }
        .findFirst()
        .orElse(null)
}