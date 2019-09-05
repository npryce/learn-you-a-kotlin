package learnyouakotlin.part3

import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.JsonNode
import learnyouakotlin.part1.Presenter
import learnyouakotlin.part1.Session
import learnyouakotlin.part1.Slots
import learnyouakotlin.part3.Json.*


fun Session.toJson(): JsonNode {
    return obj(
        prop("title", title),
        if (subtitle == null) null else prop("subtitle", subtitle),
        prop("slots", obj(
            prop("first", slots.start),
            prop("last", slots.endInclusive)
        )),
        prop("presenters", array(presenters) { it.toJson() }))
}

fun JsonNode.toSession(): Session {
    val title = nonBlankText(path("title"))
    val subtitle = optionalNonBlankText(path("subtitle"))

    val authorsNode = path("presenters")
    val presenters = authorsNode.map { it.toPresenter() }
    val slots = Slots(at("/slots/first").intValue(), at("/slots/last").intValue())

    return Session(title!!, subtitle, slots, presenters)
}

private fun Presenter.toJson(): JsonNode {
    return obj(prop("name", name))
}

private fun JsonNode.toPresenter(): Presenter {
    return Presenter(path("name").asText())
}

private fun optionalNonBlankText(node: JsonNode): String? {
    return if (node.isMissingNode) {
        null
    } else {
        nonBlankText(node)
    }
}

private fun nonBlankText(node: JsonNode): String? {
    val text = node.asText()
    return if (node.isNull || text == "") {
        throw JsonMappingException(null, "missing or empty text")
    } else {
        text
    }
}