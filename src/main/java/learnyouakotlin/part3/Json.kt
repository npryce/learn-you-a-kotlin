package learnyouakotlin.part3

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.*

import com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT
import com.fasterxml.jackson.databind.SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS
import java.util.Arrays.asList
import java.util.stream.Collectors.toList


private val nodes = JsonNodeFactory.instance
internal val stableMapper = ObjectMapper().enable(INDENT_OUTPUT, ORDER_MAP_ENTRIES_BY_KEYS)

infix fun String.of(textValue: String) = this.of(TextNode(textValue))

infix fun String.of(intValue: Int) = this.of(IntNode(intValue))

infix fun String.of(value: JsonNode) = this to value

fun obj(props: Iterable<Pair<String, JsonNode>?>): ObjectNode =
    ObjectNode(nodes, props.filterNotNull().toMap())

fun obj(vararg props: Pair<String, JsonNode>?): ObjectNode =
    obj(props.asList())

fun array(elements: Iterable<JsonNode>): ArrayNode =
    ArrayNode(nodes).apply {
        elements.forEach { add(it) }
    }

fun <T> array(elements: List<T>, fn: (T) -> JsonNode): ArrayNode
    = array(elements.map(fn))

fun toStableJsonString(n: JsonNode): String = try {
    stableMapper.writeValueAsString(n)
} catch (e: JsonProcessingException) {
    throw IllegalArgumentException("failed to convert JsonNode to JSON string", e)
}

