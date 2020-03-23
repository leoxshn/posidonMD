package posidon.markdown.compiler.code

import posidon.markdown.compiler.toHtml

class KotlinCodeAppender : CodeAppender() {

    override fun append(line: String) {
        var i = 0
        var lastStrB = StringBuilder()
        var strStack = ArrayList<String>()
        var startSpaces = 0
        var startSpacesInterrumped = false
        var inString = false
        var inChar = false
        while (i != line.length) {
            val char = line[i]
            when {
                char == '"' -> {
                    if (inString) {
                        lastStrB.append('"')
                        var j = i - 1
                        while (j != -1)
                            if (line[j] == '\\') j--
                            else break
                        if ((i - j) % 2 == 1) {
                            inString = false
                            builder.append("<span style=\"color:#17C25B;\">").append(lastStrB).append("</span>")
                            lastStrB = StringBuilder()
                        }
                    } else if (!inChar) {
                        inString = true
                        builder.append(lastStrB)
                        lastStrB = StringBuilder("\"")
                    }
                }
                inString || inChar -> lastStrB.append(char.toHtml())
                char == ')' ||
                char == '[' ||
                char == ']' ||
                char == '{' ||
                char == '}' -> {
                    startSpacesInterrumped = true
                    builder.append(lastStrB.toString())
                    lastStrB = StringBuilder()
                    builder.append("<span style=\"color:var(--bracket-color);\">$char</span>")
                }
                char == '(' -> {
                    startSpacesInterrumped = true
                    val token = lastStrB.toString()
                    if (strStack.lastOrNull() == "fun") {
                        builder.append("<span style=\"color:#f8d055;\">$token</span>")
                        strStack.removeAt(strStack.lastIndex)
                    } else builder.append(token)
                    lastStrB = StringBuilder()
                    builder.append("<span style=\"color:var(--bracket-color);\">(</span>")
                }
                char == '\t' -> {
                    startSpaces += 4
                    builder.append("<span class=\"s\">")
                    builder.append("<span class=\"s\">")
                    builder.append("<span class=\"s\">")
                    builder.append("<span class=\"s\">")
                }
                char == ' ' || (i == line.lastIndex).also { if (it) lastStrB.append(char) } -> {
                    if (startSpacesInterrumped) {
                        when (val token = lastStrB.toString()) {
                            "override",
                            "private",
                            "internal",
                            "abstract",
                            "final",
                            "operator",
                            "tailrec",
                            "suspend",
                            "in",
                            "is",
                            "when",
                            "while",
                            "if",
                            "else",
                            "for",
                            "class",
                            "constructor",
                            "object",
                            "interface",
                            "package",
                            "import",
                            "val",
                            "var",
                            "const" -> builder.append("<span style=\"color:orange;\">$token</span>")
                            "this", "null", "true", "false" -> builder.append("<span style=\"color:light-blue;\">$token</span>")
                            "fun" -> {
                                builder.append("<span style=\"color:orange;\">$token</span>")
                                strStack.add("fun")
                            }
                            else -> builder.append(token)
                        }
                        lastStrB = StringBuilder()
                        if (i != line.lastIndex) builder.append(' ')
                    } else {
                        builder.append(lastStrB)
                        startSpaces++
                        builder.append("<span class=\"s\">")
                    }
                }
                else -> { startSpacesInterrumped = true; lastStrB.append(char.toHtml()) }
            }
            i++
        }
        for (_s in 0..startSpaces) builder.append("</span>")
        builder.append("<br>")
    }
}