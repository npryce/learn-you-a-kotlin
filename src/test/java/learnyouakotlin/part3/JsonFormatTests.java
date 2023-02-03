package learnyouakotlin.part3;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.oneeyedmen.okeydoke.junit.ApprovalsRule;
import learnyouakotlin.part1.Presenter;
import learnyouakotlin.part1.Session;
import learnyouakotlin.part1.Slots;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

import static learnyouakotlin.part3.JsonFormat.sessionFromJson;
import static learnyouakotlin.part3.JsonFormat.sessionToJson;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.fail;

public class JsonFormatTests {
    @Rule
    public final ApprovalsRule approval = ApprovalsRule.usualRule();

    @Test
    public void session_to_json() {
        Session session = new Session(
                "Learn You a Kotlin For All The Good It Will Do You",
                null,
                new Slots(1, 2),
                new Presenter("Duncan McGregor"),
                new Presenter("Nat Pryce"));

        JsonNode json = sessionToJson(session);
        approval.assertApproved(JsonKt.toStableJsonString(json));
    }

    @Test
    public void session_with_subtitle_to_json() {
        Session session = new Session(
                "Scrapheap Challenge",
                "A Workshop in Postmodern Programming",
                new Slots(3, 3),
                new Presenter("Ivan Moore"));

        JsonNode json = sessionToJson(session);
        approval.assertApproved(JsonKt.toStableJsonString(json));
    }

    @Test
    public void session_to_and_from_json() throws JsonMappingException {
        Session original = new Session(
                "Working Effectively with Legacy Tests",
                null,
                new Slots(4, 5),
                new Presenter("Nat Pryce"),
                new Presenter("Duncan McGregor"));

        Session parsed = sessionFromJson(sessionToJson(original));
        assertThat(parsed, equalTo(original));
    }

    @Test
    public void reading_throws_with_blank_subtitle() throws IOException {
        String json = ("{" +
            "  'title' : 'Has blank subtitle'," +
            "  'subtitle' : ''," +
            "  'slots' : { 'first' : 3, 'last' : 3  }," +
            "  'presenters' : [ {    'name' : 'Ivan Moore'  } ]\n" +
            "}").replace("'", "\"");
        try {
            sessionFromJson(JsonKt.stableMapper.readTree(json));
            fail();
        } catch (JsonMappingException expected) {
            assertThat(expected.getMessage(), equalTo("missing or empty text"));
        }
    }
}
