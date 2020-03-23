package posidon.markdown.compiler

import kotlin.system.exitProcess

fun exitWithError(type: ErrorType, extraMessage: String) {
    System.err.println(type.text + "($fileName:$currentLine): $extraMessage")
    exitProcess(1)
}


enum class ErrorType(val text: String) {
    Header("HeaderError"),
    IO("IOError"),
    Url("UrlError")
}