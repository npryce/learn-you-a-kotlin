package learnyouakotlin.part1;

import org.junit.Test;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class SessionTests {

    Session original = new Session("The Title", null, new Slots(1, 2), new Presenter("Alice"));

    @Test
    public void can_change_presenters() {
        assertThat(
            original.withPresenters(asList(new Presenter("Bob"), new Presenter("Carol"))),
            equalTo(new Session("The Title", null, new Slots(1, 2), new Presenter("Bob"), new Presenter("Carol"))));
    }

    @Test
    public void can_change_title() {
        assertThat(
            original.withTitle("Another Title"),
            equalTo(new Session("Another Title", null, new Slots(1, 2), new Presenter("Alice"))));
    }

    @Test
    public void can_change_subtitle() {
        assertThat(
            original.withSubtitle("The Subtitle"),
            equalTo(new Session("The Title", "The Subtitle", new Slots(1, 2), new Presenter("Alice"))));
    }

    @Test
    public void can_remove_subtitle() {
        assertThat(
            original.withSubtitle("The Subtitle").withSubtitle(null),
            equalTo(new Session("The Title", null, new Slots(1, 2), new Presenter("Alice"))));
    }
}
