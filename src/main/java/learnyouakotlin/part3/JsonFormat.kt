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

fun JsonNode.toSession() =
    Session(
        title = nonBlankText(path("title")),
        subtitle = optionalNonBlankText(path("subtitle")),
        slots = Slots(
            start = at("/slots/first").intValue(),
            endInclusive = at("/slots/last").intValue()
        ),
        presenters = path("presenters").map { it.toPresenter() }
    )


private fun Presenter.toJson(): ObjectNode = obj("name" of name)

private fun JsonNode.toPresenter(): Presenter = Presenter(path("name").asText())

private fun optionalNonBlankText(node: JsonNode): String? =
    if (node.isMissingNode) {
        null
    } else {
        nonBlankText(node)
    }

private fun nonBlankText(node: JsonNode): String =
    node.asText().takeIf { !it.isNullOrEmpty() }
        ?: throw JsonMappingException(null, "missing or empty text")

