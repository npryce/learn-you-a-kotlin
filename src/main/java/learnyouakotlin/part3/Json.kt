package learnyouakotlin.part3

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT
import com.fasterxml.jackson.databind.SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.IntNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import java.util.function.Consumer
import java.util.stream.Collectors

private val nodes = JsonNodeFactory.instance

@JvmField
val stableMapper: ObjectMapper = ObjectMapper().enable(INDENT_OUTPUT, ORDER_MAP_ENTRIES_BY_KEYS)

infix fun String.of(
    value: JsonNode
) = this to value

infix fun String.of(
    textValue: String?
) = this of TextNode(textValue)

fun prop(name: String, intValue: Int): Pair<String, JsonNode> = name of IntNode(intValue)
fun obj(props: Iterable<Pair<String, JsonNode>?>): ObjectNode {
    val objectNode = ObjectNode(nodes)
    props.forEach { p: Pair<String, JsonNode>? ->
        if (p != null) {
            objectNode.set<JsonNode>(p.first, p.second)
        }
    }
    return objectNode
}

fun obj(vararg props: Pair<String, JsonNode>?): ObjectNode =
    obj(props.asList())

fun array(elements: Iterable<JsonNode?>): ArrayNode {
    val array = ArrayNode(nodes)
    elements.forEach(Consumer { value: JsonNode? -> array.add(value) })
    return array
}

fun toStableJsonString(n: JsonNode?): String {
    return try {
        stableMapper.writeValueAsString(n)
    } catch (e: JsonProcessingException) {
        throw IllegalArgumentException("failed to convert JsonNode to JSON string", e)
    }
}

fun <T> array(elements: List<T>, fn: (T) -> JsonNode): ArrayNode =
    array(elements.stream().map(fn).collect(Collectors.toList()))