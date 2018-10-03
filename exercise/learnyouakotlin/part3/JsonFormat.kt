package learnyouakotlin.part3

import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import learnyouakotlin.part1.Presenter
import learnyouakotlin.part1.Session
import learnyouakotlin.part1.Slots
import learnyouakotlin.part3.Json.*

object JsonFormat {
    fun sessionToJson(session: Session): JsonNode {
        return obj(
            prop("title", session.title),
            if (session.subtitle == null) null else prop("subtitle", session.subtitle),
            prop(
                "slots", obj(
                prop("first", session.slots.start),
                prop("last", session.slots.endInclusive)
            )),
            prop("presenters", array(session.presenters, ::presenterToJson)))
    }

    fun sessionFromJson(json: JsonNode): Session {
        val title = nonBlankText(json.path("title"))
        val subtitle = optionalNonBlankText(json.path("subtitle"))

        val authorsNode = json.path("presenters")
        val presenters =  authorsNode
            .map(this::presenterFromJson)
        return Session(title, subtitle, Slots(1, 2), presenters)
    }

    private fun presenterToJson(p: Presenter): ObjectNode {
        return obj(prop("name", p.name))
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
