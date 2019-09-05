package learnyouakotlin.part3

import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import learnyouakotlin.part1.Presenter
import learnyouakotlin.part1.Session
import learnyouakotlin.part1.Slots

fun Session.toJson(): JsonNode {
    return obj(
        "title" `=` title,
        if (subtitle == null) null else "subtitle" `=` subtitle,
        "slots" `=` obj(
            "first" `=` slots.start,
            "last" `=` slots.endInclusive
        ),
        "presenters" `=` array(presenters) { it.toJson() })
}

fun JsonNode.toSession(): Session {
    val title = nonBlankText(path("title"))
    val subtitle = optionalNonBlankText(path("subtitle"))

    val authorsNode = path("presenters")
    val presenters = authorsNode.map { it.toPresenter() }
    val slots = Slots(at("/slots/first").intValue(), at("/slots/last").intValue())

    return Session(title!!, subtitle, slots, presenters)
}

private fun Presenter.toJson(): ObjectNode = obj("name".`=`(name))

private fun JsonNode.toPresenter() = Presenter(path("name").asText())

private fun optionalNonBlankText(node: JsonNode): String? =
    when {
        node.isMissingNode -> null
        else -> nonBlankText(node)
    }

private fun nonBlankText(node: JsonNode): String? {
    val text = node.asText()
    return if (node.isNull || text == "") {
        throw JsonMappingException(null, "missing or empty text")
    } else {
        text
    }
}