package learnyouakotlin.part3

import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.JsonNode
import learnyouakotlin.part1.Presenter
import learnyouakotlin.part1.Session
import learnyouakotlin.part1.Slots

fun Session.toJson(): JsonNode = obj(
    "title" of title,
    subtitle?.let { "subtitle" of subtitle },
    "slots" of obj(
        "first" of slots.start,
        "last" of slots.endInclusive
    ),
    "presenters" of array(presenters, Presenter::toJson)
)

fun Presenter.toJson(): JsonNode = obj("name" of name)

fun JsonNode.toSession(): Session {
    val title = path("title").nonBlankText()
    val subtitle = path("subtitle").optionalNonBlankText()
    val authorsNode = path("presenters")
    val presenters = authorsNode.map { it.toPresenter() }
    val slots = Slots(at("/slots/first").intValue(), at("/slots/last").intValue())
    return Session(title, subtitle, slots, presenters)
}

fun JsonNode.toPresenter(): Presenter = Presenter(path("name").asText())

fun JsonNode.optionalNonBlankText(): String? = if (isMissingNode) null else this.nonBlankText()

fun JsonNode.nonBlankText(): String = asText().also {
    if (isNull || it == "") {
        throw JsonMappingException(null, "missing or empty text")
    }
}