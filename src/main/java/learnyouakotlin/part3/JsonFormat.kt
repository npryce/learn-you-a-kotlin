package learnyouakotlin.part3

import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import learnyouakotlin.part1.Presenter
import learnyouakotlin.part1.Session
import learnyouakotlin.part1.Slots

fun Session.toJson(): JsonNode = obj(
    "title" of title,
    if (subtitle == null) null else "subtitle" of subtitle,
    "slots" of obj(
        "first" of slots.start,
        "last" of slots.endInclusive
    ),
    "presenters" of array(presenters) { it.toJson() })

fun JsonNode.toSession(): Session {
    val title = nonBlankText(path("title"))
    val subtitle = optionalNonBlankText(path("subtitle"))

    val authorsNode: JsonNode = path("presenters")
    val presenters = authorsNode
        .map { it.toPresenter() }
    val slots = Slots(at("/slots/first").intValue(), at("/slots/last").intValue())

    return Session(title, subtitle, slots, presenters)
}


private fun Presenter.toJson(): ObjectNode = obj("name" of name)

private fun JsonNode.toPresenter(): Presenter = Presenter(path("name").asText())

private fun optionalNonBlankText(node: JsonNode): String? =
    if (node.isMissingNode) {
        null
    } else {
        nonBlankText(node)
    }

private fun nonBlankText(node: JsonNode): String =
    node.asText().takeUnless { it.isNullOrEmpty() }
        ?: throw JsonMappingException(null, "missing or empty text")

