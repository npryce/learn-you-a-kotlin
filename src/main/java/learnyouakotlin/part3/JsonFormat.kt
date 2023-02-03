package learnyouakotlin.part3

import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import learnyouakotlin.part1.Presenter
import learnyouakotlin.part1.Session
import learnyouakotlin.part1.Slots
import java.util.Spliterator

object JsonFormat {
    @JvmStatic
    fun sessionToJson(session: Session): JsonNode {
        return obj(
            "title" of session.title,
            if (session.subtitle == null) null else "subtitle" of session.subtitle,
            "slots" of obj(
                prop("first", session.slots.start),
                prop("last", session.slots.endInclusive)
            ),
            "presenters" of array(session.presenters, ::presenterToJson)
        )
    }
    
    @JvmStatic
    fun sessionFromJson(json: JsonNode): Session {
        val title = nonBlankText(json.path("title"))
        val subtitle = optionalNonBlankText(json.path("subtitle"))
        val authorsNode = json.path("presenters")
        val presenters = authorsNode
            .map(::presenterFromJson)
        val slots = Slots(json.at("/slots/first").intValue(), json.at("/slots/last").intValue())
        return Session(title, subtitle, slots, presenters)
    }
    
    private fun spliterator(elements: Iterable<JsonNode>): Spliterator<JsonNode> {
        return elements.spliterator()
    }
    
    private fun presenterToJson(p: Presenter): ObjectNode {
        return obj("name" of p.name)
    }
    
    private fun presenterFromJson(authorNode: JsonNode): Presenter {
        return Presenter(authorNode.path("name").asText())
    }
    
    private fun optionalNonBlankText(node: JsonNode): String? {
        return if (node.isMissingNode) {
            null
        } else {
            nonBlankText(node)
        }
    }
    
    private fun nonBlankText(node: JsonNode): String {
        val text = node.asText()
        return if (node.isNull || text == "") {
            throw JsonMappingException(null, "missing or empty text")
        } else {
            text
        }
    }
}