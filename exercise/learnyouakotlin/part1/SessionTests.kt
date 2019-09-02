package learnyouakotlin.part1

import org.junit.Test

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat

class SessionTests {

    private val original = Session("The Title", null, Slots(1, 2), Presenter("Alice"))

    @Test
    fun `illustrate convenience extension methods`() {
        assertThat(
            original.withPresenters(Presenter("Bob"), Presenter("Carol")),
            equalTo(Session("The Title", null, Slots(1, 2), Presenter("Bob"), Presenter("Carol"))))
    }
}

private fun Session.withPresenters(vararg newLineUp: Presenter) = copy(presenters = newLineUp.toList())

