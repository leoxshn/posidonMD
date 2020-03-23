package posidon.markdown.compiler.code

import posidon.markdown.compiler.toHtml

class FallbackCodeAppender : CodeAppender() {
    override fun append(line: String) { builder.append(line.toHtml()).append("<br>") }
}