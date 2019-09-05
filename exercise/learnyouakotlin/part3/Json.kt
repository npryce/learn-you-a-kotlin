package learnyouakotlin.part3

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT
import com.fasterxml.jackson.databind.SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS
import com.fasterxml.jackson.databind.node.*

private val nodeFactory = JsonNodeFactory.instance
internal val stableMapper = ObjectMapper().enable(INDENT_OUTPUT, ORDER_MAP_ENTRIES_BY_KEYS)

infix fun String.`=`(textValue: String): Pair<String, JsonNode> = this.`=`(TextNode(textValue))

infix fun String.`=`(intValue: Int): Pair<String, JsonNode> = this.`=`(IntNode(intValue))

infix fun String.`=`(value: JsonNode) = this to value

fun obj(props: Iterable<Pair<String, JsonNode>?>) = ObjectNode(nodeFactory, props.filterNotNull().toMap())

fun obj(vararg props: Pair<String, JsonNode>?): ObjectNode = obj(props.asList())

fun array(elements: Iterable<JsonNode>) = ArrayNode(nodeFactory, elements.toList())

fun <T> array(elements: List<T>, fn: (T) -> JsonNode) = array(elements.map(fn))

fun JsonNode.toStableJsonString(): String =
    try {
        stableMapper.writeValueAsString(this)
    } catch (e: JsonProcessingException) {
        throw IllegalArgumentException("failed to convert JsonNode to JSON string", e)
    }