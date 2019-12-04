package learnyouakotlin.part3

import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.JsonNode
import com.oneeyedmen.okeydoke.junit.ApprovalsRule
import learnyouakotlin.part1.Presenter
import learnyouakotlin.part1.Session
import learnyouakotlin.part1.Slots
import org.junit.Rule
import org.junit.Test

import java.io.IOException

import learnyouakotlin.part3.JsonFormat.sessionFromJson
import learnyouakotlin.part3.JsonFormat.sessionToJson
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual.equalTo
import org.intellij.lang.annotations.Language
import org.junit.Assert.fail

class JsonFormatTests {
    @Rule
    @JvmField
    val approval = ApprovalsRule.usualRule()

    @Test
    fun session_to_json() {
        val session = Session(
            "Learn You a Kotlin For All The Good It Will Do You",
            null,
            Slots(1, 2),
            Presenter("Duncan McGregor"),
            Presenter("Nat Pryce"))

        val json = sessionToJson(session)
        approval.assertApproved(Json.toStableJsonString(json))
    }

    @Test
    fun session_with_subtitle_to_json() {
        val session = Session(
            "Scrapheap Challenge",
            "A Workshop in Postmodern Programming",
            Slots(3, 3),
            Presenter("Ivan Moore"))

        val json = sessionToJson(session)
        approval.assertApproved(Json.toStableJsonString(json))
    }

    @Test
    fun session_to_and_from_json() {
        val original = Session(
            "Working Effectively with Legacy Tests", null,
            Slots(4, 5),
            Presenter("Nat Pryce"),
            Presenter("Duncan McGregor"))

        val parsed = sessionFromJson(sessionToJson(original))
        assertThat(parsed, equalTo(original))
    }

    @Test
    fun reading_throws_with_blank_subtitle() {
        @Language("JSON") val json = ("""{
              "title": "Has blank subtitle",
              "subtitle": "",
              "slots": {
                "first": 3,
                "last": 3
              },
              "presenters": [
                {
                  "name": "Ivan Moore"
                }
              ]
            }""")
        try {
            sessionFromJson(Json.stableMapper.readTree(json))
            fail()
        } catch (expected: JsonMappingException) {
            assertThat<String>(expected.message, equalTo("missing or empty text"))
        }

    }
}
