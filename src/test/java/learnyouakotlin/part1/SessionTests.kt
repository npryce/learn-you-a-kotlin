package learnyouakotlin.part1

import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Test

class SessionTests {
    var original = Session("The Title", null, Slots(1, 2), Presenter("Alice"))
    @Test
    fun can_change_presenters() {
        MatcherAssert.assertThat(
            original.withPresenters(listOf(Presenter("Bob"), Presenter("Carol"))),
            CoreMatchers.equalTo(Session("The Title", null, Slots(1, 2), Presenter("Bob"), Presenter("Carol")))
        )
    }
    
    @Test
    fun can_change_title() {
        MatcherAssert.assertThat(
            original.withTitle("Another Title"),
            CoreMatchers.equalTo(Session("Another Title", null, Slots(1, 2), Presenter("Alice")))
        )
    }
    
    @Test
    fun can_change_subtitle() {
        MatcherAssert.assertThat(
            original.withSubtitle("The Subtitle"),
            CoreMatchers.equalTo(Session("The Title", "The Subtitle", Slots(1, 2), Presenter("Alice")))
        )
    }
    
    @Test
    fun can_remove_subtitle() {
        MatcherAssert.assertThat(
            original.withSubtitle("The Subtitle").withSubtitle(null),
            CoreMatchers.equalTo(Session("The Title", null, Slots(1, 2), Presenter("Alice")))
        )
    }
}