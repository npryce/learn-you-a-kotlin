package learnyouakotlin.part3

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT
import com.fasterxml.jackson.databind.SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS
import com.fasterxml.jackson.databind.node.*
import java.util.Arrays.asList
import java.util.stream.Collectors.toList

private val nodes = JsonNodeFactory.instance
internal val stableMapper = ObjectMapper().enable(INDENT_OUTPUT, ORDER_MAP_ENTRIES_BY_KEYS)

fun prop(name: String, textValue: String): Pair<String, JsonNode> {
    return prop(name, TextNode(textValue))
}

fun prop(name: String, intValue: Int): Pair<String, JsonNode> {
    return prop(name, IntNode(intValue))
}

fun prop(name: String, value: JsonNode): Pair<String, JsonNode> {
    return Pair(name, value)
}

fun obj(props: Iterable<Pair<String, JsonNode>?>): ObjectNode {
    val `object` = ObjectNode(nodes)
    props.forEach { p ->
        // p can be null, but no way to annotate the Map.Pair within the Iterable
        if (p != null) {
            `object`.set(p.first, p.second)
        }
    }
    return `object`
}

@SafeVarargs
fun obj(vararg props: Pair<String, JsonNode>?): ObjectNode {
    // Elements of props may be null, but there's no way to use annotations to indicate that. Annotating the
    // props parameter with @Nullable means that the whole array may be null
    return obj(asList(*props))
}

fun array(elements: Iterable<JsonNode>): ArrayNode {
    val array = ArrayNode(nodes)
    elements.forEach({ array.add(it) })
    return array
}

fun <T> array(elements: List<T>, fn: (T) -> JsonNode): ArrayNode {
    return array(elements.stream().map(fn).collect(toList()))
}

fun toStableJsonString(n: JsonNode): String {
    try {
        return stableMapper.writeValueAsString(n)
    } catch (e: JsonProcessingException) {
        throw IllegalArgumentException("failed to convert JsonNode to JSON string", e)
    }
}
