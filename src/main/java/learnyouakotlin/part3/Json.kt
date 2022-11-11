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
val stableMapper = ObjectMapper().apply{
    enable(INDENT_OUTPUT, ORDER_MAP_ENTRIES_BY_KEYS)
}

fun prop(name: String, textValue: String?): Pair<String, JsonNode?> {
    return prop(name, TextNode(textValue))
}

fun prop(name: String, intValue: Int): Pair<String, JsonNode> {
    return prop(name, IntNode(intValue))
}

fun prop(name: String, value: JsonNode): Pair<String, JsonNode> {
    return name to value
}

fun obj(props: Iterable<Pair<String, JsonNode?>?>): ObjectNode {
    val o = ObjectNode(nodes)
    props.forEach(Consumer { p: Pair<String, JsonNode?>? ->
        // p can be null, but no way to annotate the Map.Entry within the Iterable
        if (p != null) {
            o.set<JsonNode>(p.first, p.second)
        }
    })
    return o
}

fun obj(vararg props: Pair<String, JsonNode?>?): ObjectNode {
    return obj(props.asList())
}

fun array(elements: Iterable<JsonNode?>): ArrayNode {
    val array = ArrayNode(nodes)
    elements.forEach(Consumer { value: JsonNode? -> array.add(value) })
    return array
}

fun <T> array(elements: List<T>, fn: (T) -> JsonNode?): ArrayNode =
    array(elements.stream().map(fn).collect(Collectors.toList()))

fun toStableJsonString(n: JsonNode): String {
    return try {
        stableMapper.writeValueAsString(n)
    } catch (e: JsonProcessingException) {
        throw IllegalArgumentException("failed to convert JsonNode to JSON string", e)
    }
}