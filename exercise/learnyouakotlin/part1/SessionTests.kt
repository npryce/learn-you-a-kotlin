package learnyouakotlin.part1

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import java.util.Arrays.asList

class SessionTests {

    private val original = Session("The Title", null, Slots(1, 2), Presenter("Alice"))

    @Test
    fun `can change presenters`() {
        assertThat(
            original.withPresenters(asList(Presenter("Bob"), Presenter("Carol"))),
            equalTo(Session("The Title", null, Slots(1, 2), Presenter("Bob"), Presenter("Carol"))))
    }

    @Test
    fun `can change title`() {
        assertThat(
            original.withTitle("Another Title"),
            equalTo(Session("Another Title", null, Slots(1, 2), Presenter("Alice"))))
    }

    @Test
    fun `can change subtitle`() {
        assertThat(
            original.withSubtitle("The Subtitle"),
            equalTo(Session("The Title", "The Subtitle", Slots(1, 2), Presenter("Alice"))))
    }

    @Test
    fun `can remove subtitle`() {
        assertThat(
            original.withSubtitle("The Subtitle").withSubtitle(null),
            equalTo(Session("The Title", null, Slots(1, 2), Presenter("Alice"))))
    }
}
