package learnyouakotlin.part3

import com.fasterxml.jackson.databind.JsonMappingException
import com.oneeyedmen.okeydoke.junit.ApprovalsRule
import learnyouakotlin.part1.Presenter
import learnyouakotlin.part1.Session
import learnyouakotlin.part1.Slots
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual.equalTo
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test

class JsonFormatTests {
    @Rule
    @JvmField
    val approval = ApprovalsRule.fileSystemRule("exercise")

    @Test
    fun session_to_json() {
        val session = Session(
            "Learn You a Kotlin For All The Good It Will Do You",
            null,
            Slots(1, 2),
            Presenter("Duncan McGregor"),
            Presenter("Nat Pryce"))

        val json = session.toJson()
        approval.assertApproved(json.toStableJsonString())
    }

    @Test
    fun session_with_subtitle_to_json() {
        val session = Session(
            "Scrapheap Challenge",
            "A Workshop in Postmodern Programming",
            Slots(3, 3),
            Presenter("Ivan Moore"))

        val json = session.toJson()
        approval.assertApproved(json.toStableJsonString())
    }

    @Test
    fun session_to_and_from_json() {
        val original = Session(
            "Working Effectively with Legacy Tests",
            null,
            Slots(4, 5),
            Presenter("Nat Pryce"),
            Presenter("Duncan McGregor"))

        val parsed = original.toJson().toSession()
        assertThat(parsed, equalTo(original))
    }

    @Test
    fun reading_throws_with_blank_subtitle() {
        val json = """{  
                "title" : "Has blank subtitle",
                "subtitle" : "",
                "slots" : { "first" : 3, "last" : 3 },
                "presenters" : [ { "name" : "Ivan Moore" } ]
            }"""
        try {
            stableMapper.readTree(json).toSession()
            fail()
        } catch (expected: JsonMappingException) {
            assertThat(expected.message, equalTo("missing or empty text"))
        }
    }

    @Test
    fun reading_throws_with_null_subtitle() {
        val json = """{  
                "title" : "Has blank subtitle",
                "subtitle" : null,
                "slots" : { "first" : 3, "last" : 3 },
                "presenters" : [ { "name" : "Ivan Moore" } ]
            }"""
        try {
            stableMapper.readTree(json).toSession()
            fail()
        } catch (expected: JsonMappingException) {
            assertThat(expected.message, equalTo("missing or empty text"))
        }
    }
}
