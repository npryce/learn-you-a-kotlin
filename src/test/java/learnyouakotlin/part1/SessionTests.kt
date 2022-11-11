package learnyouakotlin.part1

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class SessionTests {
    private val original = Session(
        "The Title",
        null,
        Slots(1, 2),
        Presenter("Alice")
    )
    @Test
    fun can_change_presenters() {
        assertThat(
            original.withPresenters(listOf(Presenter("Bob"), Presenter("Carol"))),
            equalTo(Session("The Title", null, Slots(1, 2), Presenter("Bob"), Presenter("Carol")))
        )
    }
    
    @Test
    fun can_change_title() {
        assertThat(
            original.withTitle("Another Title"),
            equalTo(Session("Another Title", null, Slots(1, 2), Presenter("Alice")))
        )
    }
    
    @Test
    fun can_change_subtitle() {
        assertThat(
            original.withSubtitle("The Subtitle"),
            equalTo(Session("The Title", "The Subtitle", Slots(1, 2), Presenter("Alice")))
        )
    }
    
    @Test
    fun can_remove_subtitle() {
        assertThat(
            original.withSubtitle("The Subtitle").withSubtitle(null),
            equalTo(Session("The Title", null, Slots(1, 2), Presenter("Alice")))
        )
    }
}