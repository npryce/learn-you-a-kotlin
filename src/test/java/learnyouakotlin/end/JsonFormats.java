package learnyouakotlin.end;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Spliterator.ORDERED;
import static java.util.Spliterators.spliterator;
import static java.util.stream.StreamSupport.stream;
import static learnyouakotlin.end.Json.object;
import static learnyouakotlin.end.Json.prop;

public class JsonFormats {
    public static JsonNode asJson(Session session) {
        return object(
                prop("code", session.code.toString()),
                prop("title", session.title),
                prop("authors", Json.mapToJsonArray(session.presenters,
                        author -> object(prop("name", author.name)))));
    }

    public static Session bookFromJson(JsonNode json) throws JsonMappingException {
        try {
            SessionCode code = SessionCode.parse(json.path("code").asText());
            String title = json.path("title").asText();

            JsonNode authorsNode = json.path("authors");
            List<Presenter> authors =  stream(spliterator(authorsNode.elements(), authorsNode.size(), ORDERED), false)
                    .map(JsonFormats::personFromJson).
                    collect(Collectors.toList());

            return new Session(code, title, authors);

        } catch (ParseException e) {
            throw new JsonMappingException(null, "failed to parse Book from JSON", e);
        }
    }

    private static Presenter personFromJson(JsonNode authorNode) {
        return new Presenter(authorNode.path("name").asText());
    }
}
