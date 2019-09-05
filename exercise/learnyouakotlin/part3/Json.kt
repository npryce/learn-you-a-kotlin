package learnyouakotlin.part3

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT
import com.fasterxml.jackson.databind.SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS
import com.fasterxml.jackson.databind.node.*

private val nodes = JsonNodeFactory.instance
internal val stableMapper = ObjectMapper().enable(INDENT_OUTPUT, ORDER_MAP_ENTRIES_BY_KEYS)

fun prop(name: String, textValue: String): Pair<String, JsonNode> = prop(name, TextNode(textValue))

fun prop(name: String, intValue: Int): Pair<String, JsonNode> = prop(name, IntNode(intValue))

fun prop(name: String, value: JsonNode) = name to value

fun obj(props: Iterable<Pair<String, JsonNode>?>) = ObjectNode(nodes, props.filterNotNull().toMap())

fun obj(vararg props: Pair<String, JsonNode>?): ObjectNode = obj(props.asList())

fun array(elements: Iterable<JsonNode>) = ArrayNode(nodes, elements.toList())

fun <T> array(elements: List<T>, fn: (T) -> JsonNode) = array(elements.map(fn))

fun JsonNode.toStableJsonString(): String =
    try {
        stableMapper.writeValueAsString(this)
    } catch (e: JsonProcessingException) {
        throw IllegalArgumentException("failed to convert JsonNode to JSON string", e)
    }