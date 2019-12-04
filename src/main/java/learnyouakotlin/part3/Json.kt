package learnyouakotlin.part3;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import kotlin.Pair;

import java.util.List;
import java.util.function.Function;

import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static com.fasterxml.jackson.databind.SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

public class Json {
    private static final JsonNodeFactory nodes = JsonNodeFactory.instance;
    static final ObjectMapper stableMapper = new ObjectMapper().enable(INDENT_OUTPUT, ORDER_MAP_ENTRIES_BY_KEYS);

    public static Pair<String, JsonNode> prop(String name, String textValue) {
        return prop(name, new TextNode(textValue));
    }

    public static Pair<String, JsonNode> prop(String name, int intValue) {
        return prop(name, new IntNode(intValue));
    }

    public static Pair<String, JsonNode> prop(String name, JsonNode value) {
        return new Pair<>(name, value);
    }

    public static ObjectNode obj(Iterable<Pair<String, JsonNode>> props) {
        ObjectNode object = new ObjectNode(nodes);
        props.forEach(p -> {
            // p can be null, but no way to annotate the Map.Entry within the Iterable
            if (p != null) {
                object.set(p.getFirst(), p.getSecond());
            }
        });
        return object;
    }

    @SafeVarargs
    public static ObjectNode obj(Pair<String, JsonNode>... props) {
        // Elements of props may be null, but there's no way to use annotations to indicate that. Annotating the
        // props parameter with @Nullable means that the whole array may be null
        return obj(asList(props));
    }

    public static ArrayNode array(Iterable<JsonNode> elements) {
        ArrayNode array = new ArrayNode(nodes);
        elements.forEach(array::add);
        return array;
    }

    public static <T> ArrayNode array(List<T> elements, Function<T, JsonNode> fn) {
        return array(elements.stream().map(fn).collect(toList()));
    }

    public static String toStableJsonString(JsonNode n) {
        try {
            return stableMapper.writeValueAsString(n);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("failed to convert JsonNode to JSON string", e);
        }
    }
}
