package learnyouakotlin.part3

import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import learnyouakotlin.part1.Presenter
import learnyouakotlin.part1.Session
import learnyouakotlin.part1.Slots
import java.util.Objects
import java.util.Spliterator
import java.util.stream.Collectors
import java.util.function.Function

import java.util.stream.StreamSupport.stream
import learnyouakotlin.part3.Json.*

object JsonFormat {

    fun sessionToJson(session: Session): JsonNode {
        return obj(
            prop("title", session.title),
            if (session.subtitle == null) null else prop("subtitle", session.subtitle),
            prop("slots", obj(
                prop("first", session.slots.start),
                prop("last", session.slots.endInclusive)
            )),
            prop("presenters", array(session.presenters, Function<Presenter, JsonNode> { presenterToJson(it) })))
    }

    @Throws(JsonMappingException::class)
    fun sessionFromJson(json: JsonNode): Session {
        val title = nonBlankText(json.path("title"))
        val subtitle = optionalNonBlankText(json.path("subtitle"))

        val authorsNode = json.path("presenters")
        val presenters = stream(spliterator(Iterable { authorsNode.elements() }), false)
            .map<Presenter>(Function<JsonNode, Presenter> { presenterFromJson(it) })
            .collect(Collectors.toList())
        val slots = Slots(json.at("/slots/first").intValue(), json.at("/slots/last").intValue())

        return Session(title!!, subtitle, slots, presenters)
    }

    private fun spliterator(elements: Iterable<JsonNode>): Spliterator<JsonNode> {
        return elements.spliterator()
    }

    private fun presenterToJson(p: Presenter): ObjectNode {
        return obj(prop("name", p.name))
    }

    private fun presenterFromJson(authorNode: JsonNode): Presenter {
        return Presenter(authorNode.path("name").asText())
    }

    @Throws(JsonMappingException::class)
    private fun optionalNonBlankText(node: JsonNode): String? {
        return if (node.isMissingNode) {
            null
        } else {
            nonBlankText(node)
        }
    }

    @Throws(JsonMappingException::class)
    private fun nonBlankText(node: JsonNode): String? {
        val text = node.asText()
        return if (node.isNull || text == "") {
            throw JsonMappingException(null, "missing or empty text")
        } else {
            text
        }
    }
}
