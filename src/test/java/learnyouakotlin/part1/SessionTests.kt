package learnyouakotlin.part1

import org.junit.Test

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat

class SessionTests {

    private val original = Session("The Title", null, Slots(1, 2), Presenter("Alice"))

    @Test
    fun `can change presenters`() {
        assertThat(
            original.withPresenters(Presenter("Bob"), Presenter("Carol")),
            equalTo(Session("The Title", null, Slots(1, 2), Presenter("Bob"), Presenter("Carol"))))
    }

}

fun Session.withPresenters(vararg newLineUp: Presenter) = copy(presenters = newLineUp.toList())
