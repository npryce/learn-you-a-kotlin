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
    fun `example of extension method`() {
        val session = Session("The Title", null, Slots(1, 2), Presenter("Bob"), Presenter("Carol"))
        assertThat(
            original.withPresenters(Presenter("Bob"), Presenter("Carol")),
            equalTo(session)
        )
    }
}

fun Session.withPresenters(vararg presenters: Presenter): Session =
    copy(presenters = presenters.toList())

