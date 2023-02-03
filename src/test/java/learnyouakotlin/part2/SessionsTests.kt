package learnyouakotlin.part2

import learnyouakotlin.part1.Session
import learnyouakotlin.part1.Slots
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SessionsTests {

    companion object {
        val learnYouAKotlin = Session("Learn you a kotlin", "for all the good it will do you", Slots(1, 1))
        val refactoringToStreams = Session("Refactoring to Streams", null, Slots(2, 2))
    }

    val sessions = listOf(learnYouAKotlin, refactoringToStreams)

    @Test
    fun `nulls and flow typing`() {
        val session: Session = findWithTitle(sessions, "learn you a kotlin") !!
    
        // Uncomment to see that this can't compile
        //session.subtitle

        assertEquals("for all the good it will do you", session.subtitle)
        assertEquals("for all the good it will do you", session.subtitle)
    }

    @Test
    fun `null safe access`() {
        assertEquals("for all the good it will do you", learnYouAKotlin.subtitleOf())
        assertNull(null.subtitleOf())
    }

    @Test
    fun subtitleOrPrompt() {
        assertEquals("for all the good it will do you", learnYouAKotlin.subtitleOrPrompt)
        assertEquals("click to enter subtitle", refactoringToStreams.subtitleOrPrompt)
    }

    @Test
    fun find() {
        assertEquals(refactoringToStreams, findWithTitle(sessions, "refactoring to streams"))
        assertNull(findWithTitle(sessions, "nosuch"))
    }
}
