package learnyouakotlin.part3

import com.fasterxml.jackson.databind.JsonMappingException
import com.oneeyedmen.okeydoke.junit.ApprovalsRule
import learnyouakotlin.part1.Presenter
import learnyouakotlin.part1.Session
import learnyouakotlin.part1.Slots
import learnyouakotlin.part3.Json.toStableJsonString
import org.hamcrest.MatcherAssert
import org.hamcrest.core.IsEqual
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class JsonFormatTests {
    @Rule
    @JvmField
    val approval: ApprovalsRule = ApprovalsRule.usualRule()
    @Test
    fun session_to_json() {
        val session = Session(
            "Learn You a Kotlin For All The Good It Will Do You",
            null,
            Slots(1, 2),
            Presenter("Duncan McGregor"),
            Presenter("Nat Pryce")
        )
        val json = session.toJson()
        approval.assertApproved(toStableJsonString(json))
    }
    
    @Test
    fun session_with_subtitle_to_json() {
        val session = Session(
            "Scrapheap Challenge",
            "A Workshop in Postmodern Programming",
            Slots(3, 3),
            Presenter("Ivan Moore")
        )
        val json = session.toJson()
        approval.assertApproved(toStableJsonString(json))
    }
    
    @Test
    fun session_to_and_from_json() {
        val original = Session(
            "Working Effectively with Legacy Tests",
            null,
            Slots(4, 5),
            Presenter("Nat Pryce"),
            Presenter("Duncan McGregor")
        )
        val parsed = original.toJson().toSession()
        MatcherAssert.assertThat(parsed, IsEqual.equalTo(original))
    }
    
    @Test
    fun reading_throws_with_blank_subtitle() {
        val json =
            """{  'title' : 'Has blank subtitle',  'subtitle' : '',  'slots' : { 'first' : 3, 'last' : 3  },  'presenters' : [ {    'name' : 'Ivan Moore'  } ]
}""".replace("'", "\"")
        try {
            Json.stableMapper.readTree(json).toSession()
            Assert.fail()
        } catch (expected: JsonMappingException) {
            MatcherAssert.assertThat(expected.message, IsEqual.equalTo("missing or empty text"))
        }
    }
}