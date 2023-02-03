@file:JvmName("Sessions")
package learnyouakotlin.part2

import learnyouakotlin.part1.Session

fun Session?.subtitleOf(): String? =
    this?.subtitle

val Session.subtitleOrPrompt: String
    get() = subtitle ?: "click to enter subtitle"

fun List<Session>.findWithTitle(title: String): Session? =
    firstOrNull { it.title.equals(title, ignoreCase = true) }