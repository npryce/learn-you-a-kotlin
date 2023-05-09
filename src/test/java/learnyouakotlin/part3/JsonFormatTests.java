package learnyouakotlin.part3;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.oneeyedmen.okeydoke.Approver;
import com.oneeyedmen.okeydoke.junit5.ApprovalsExtension;
import learnyouakotlin.part1.Presenter;
import learnyouakotlin.part1.Session;
import learnyouakotlin.part1.Slots;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;

import static learnyouakotlin.part3.JsonFormat.sessionFromJson;
import static learnyouakotlin.part3.JsonFormat.sessionToJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(ApprovalsExtension.class)
public class JsonFormatTests {
    @Test
    public void session_to_json(Approver approval) {
        Session session = new Session(
                "Learn You a Kotlin For All The Good It Will Do You",
                null,
                new Slots(1, 2),
                new Presenter("Duncan McGregor"),
                new Presenter("Nat Pryce"));

        JsonNode json = sessionToJson(session);
        approval.assertApproved(Json.toStableJsonString(json));
    }

    @Test
    public void session_with_subtitle_to_json(Approver approval) {
        Session session = new Session(
                "Scrapheap Challenge",
                "A Workshop in Postmodern Programming",
                new Slots(3, 3),
                new Presenter("Ivan Moore"));

        JsonNode json = sessionToJson(session);
        approval.assertApproved(Json.toStableJsonString(json));
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
        assertEquals(original, parsed);
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
            sessionFromJson(Json.stableMapper.readTree(json));
            fail();
        } catch (JsonMappingException expected) {
            assertEquals("missing or empty text", expected.getMessage());
        }
    }
}
