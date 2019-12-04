package learnyouakotlin.part3

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.*

import com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT
import com.fasterxml.jackson.databind.SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS
import java.util.Arrays.asList
import java.util.function.Consumer
import java.util.stream.Collectors.toList


private val nodes = JsonNodeFactory.instance
internal val stableMapper = ObjectMapper().enable(INDENT_OUTPUT, ORDER_MAP_ENTRIES_BY_KEYS)

infix fun String.of(textValue: String) = this.of(TextNode(textValue))

infix fun String.of(intValue: Int) = this.of(IntNode(intValue))

infix fun String.of(value: JsonNode) = this to value

fun obj(props: Iterable<Pair<String, JsonNode>?>): ObjectNode {
    val node = ObjectNode(nodes)
    props.forEach { p ->
        // p can be null, but no way to annotate the Map.Entry within the Iterable
        if (p != null) {
            node.set<JsonNode>(p.first, p.second)
        }
    }
    return node
}

@SafeVarargs
fun obj(vararg props: Pair<String, JsonNode>?): ObjectNode =// Elements of props may be null, but there's no way to use annotations to indicate that. Annotating the
    // props parameter with @Nullable means that the whole array may be null
    obj(asList(*props))

fun array(elements: Iterable<JsonNode>): ArrayNode {
    val array = ArrayNode(nodes)
    elements.forEach(Consumer<JsonNode> { array.add(it) })
    return array
}

fun <T> array(elements: List<T>, fn: (T) -> JsonNode ): ArrayNode = array(elements.stream().map(fn).collect(toList()))

fun toStableJsonString(n: JsonNode): String {
    try {
        return stableMapper.writeValueAsString(n)
    } catch (e: JsonProcessingException) {
        throw IllegalArgumentException("failed to convert JsonNode to JSON string", e)
    }

}

