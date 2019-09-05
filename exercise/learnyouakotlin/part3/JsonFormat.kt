package learnyouakotlin.part3

import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import learnyouakotlin.part1.Presenter
import learnyouakotlin.part1.Session
import learnyouakotlin.part1.Slots

fun Session.toJson() =
    obj(
        "title" `=` title,
        subtitle?.let { "subtitle" `=` it },
        "slots" `=` obj(
            "first" `=` slots.start,
            "last" `=` slots.endInclusive
        ),
        "presenters" `=` array(presenters) { it.toJson() }
    )

fun JsonNode.toSession() = Session(
    title = nonBlankText(path("title")),
    subtitle = optionalNonBlankText(path("subtitle")),
    slots = Slots(at("/slots/first").intValue(), at("/slots/last").intValue()),
    presenters = path("presenters").map { it.toPresenter() }
)

private fun Presenter.toJson(): ObjectNode = obj("name" `=` name)

private fun JsonNode.toPresenter() = Presenter(path("name").asText())

private fun optionalNonBlankText(node: JsonNode): String? =
    when {
        node.isMissingNode -> null
        else -> nonBlankText(node)
    }

private fun nonBlankText(node: JsonNode): String {
    val text = node.asText()
    return if (node.isNull || text == "") {
        throw JsonMappingException(null, "missing or empty text")
    } else {
        text
    }
}