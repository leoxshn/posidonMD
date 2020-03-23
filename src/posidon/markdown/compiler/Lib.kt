package posidon.markdown.compiler

enum class CodeBlock { Head, Body, Code }

class TextStyle(var bold: Boolean = false, var italic: Boolean = false, var gibberish: Boolean = false)

fun simplifyForSearch(string: String) = string.toLowerCase()
    .replace(Regex("[ ,.\"_h!&/()]"), "")
    .replace("ee", "i")
    .replace("qu", "c")
    .replace('e', '3')
    .replace('q', 'c')
    .replace('k', 'c')
    .replace('z', 's')
    .replace('m', 'n')
    .replace("th", "s")
    .replace("ts", "s")
    .replace("ss", "s")
    .replace("ll", "l")
    .replace("ai", "i")

inline fun CharSequence.toHtml() = replace(Regex("&"), "&#38;")
    .replace("<", "&#60;")
    .replace(">", "&#62;")
    .replace("'", "&#39;")
    .replace("\"", "&#34;")

inline fun Char.toHtml() = when (this) {
    '&' -> "&#38;"
    '<' -> "&#60;"
    '>' -> "&#62;"
    '\'' -> "&#39;"
    '"' -> "&#34;"
    else -> this
}