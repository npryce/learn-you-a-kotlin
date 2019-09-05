package learnyouakotlin.part1

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class SessionTests {

    private val original = Session("The Title", null, Slots(1, 2), Presenter("Alice"))

    @Test
    fun `can change presenters`() {
        assertThat(
            original.withPresenters(Presenter("Bob"), Presenter("Carol")),
            equalTo(Session("The Title", null, Slots(1, 2), Presenter("Bob"), Presenter("Carol"))))
    }
}
