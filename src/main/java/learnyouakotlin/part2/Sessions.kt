package learnyouakotlin.part2

import learnyouakotlin.part1.Session

internal object Sessions {
    fun subtitleOf(session: Session?): String? {
        return session?.subtitle
    }
    
    fun subtitleOrPrompt(session: Session): String {
        return session.subtitle ?: "click to enter subtitle"
    }
    
    fun findWithTitle(sessions: List<Session?>, title: String?): Session? {
        return sessions.stream().filter { session: Session? -> session!!.title.equals(title, ignoreCase = true) }
            .findFirst().orElse(null)
    }
}