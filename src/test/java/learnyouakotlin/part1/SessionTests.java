package learnyouakotlin.part1;

import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SessionTests {

    Session original = new Session("The Title", null, new Slots(1, 2), new Presenter("Alice"));

    @Test
    public void can_change_presenters() {
        assertEquals(
            new Session("The Title", null, new Slots(1, 2), new Presenter("Bob"), new Presenter("Carol")),
            original.withPresenters(asList(new Presenter("Bob"), new Presenter("Carol"))));
    }

    @Test
    public void can_change_title() {
        assertEquals(
            new Session("Another Title", null, new Slots(1, 2), new Presenter("Alice")),
            original.withTitle("Another Title"));
    }

    @Test
    public void can_change_subtitle() {
        assertEquals(
            new Session("The Title", "The Subtitle", new Slots(1, 2), new Presenter("Alice")),
            original.withSubtitle("The Subtitle"));
    }

    @Test
    public void can_remove_subtitle() {
        assertEquals(
            new Session("The Title", null, new Slots(1, 2), new Presenter("Alice")),
            original.withSubtitle("The Subtitle").withSubtitle(null));
    }
}
