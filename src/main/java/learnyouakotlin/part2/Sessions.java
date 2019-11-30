package learnyouakotlin.part2;

import learnyouakotlin.part1.Session;

import javax.annotation.Nullable;
import java.util.List;

class Sessions {

    public static @Nullable String subtitleOf(@Nullable Session session) {
        if (session == null)
            return null;
        else
            return session.subtitle;
    }

    public static String subtitleOrPrompt(Session session) {
        if (session.subtitle == null)
            return "click to enter subtitle";
        else
            return session.subtitle;
    }

    public static @Nullable Session findWithTitle(List<Session> sessions, String title) {
        return sessions.stream().filter(session -> session.title.equalsIgnoreCase(title)).findFirst().orElse(null);
    }

}
