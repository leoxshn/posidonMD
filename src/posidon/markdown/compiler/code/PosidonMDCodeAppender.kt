package posidon.markdown.compiler.code

import posidon.markdown.compiler.toHtml

class PosidonMDCodeAppender : CodeAppender() {
    override fun append(line: String) {
        when {
            line.trim() == "\\@body:" -> builder.append("<span style=\"color:#ddff00;\">@body:</span>").append("<br>")
            line.trim().startsWith("\\@code:") -> builder.append("<span style=\"color:#ddff00;\">@body:</span>").append("<br>")
            else -> builder.append(line.toHtml()
                .replace("&#34;", "<span style=\"color:#33ddff;\">\"</span>")
                .replace("*", "<span style=\"color:#33ddff;\">*</span>")
                .replace("_", "<span style=\"color:#33ddff;\">_</span>")
                .replace("|", "<span style=\"color:#33ddff;\">|</span>")).append("<br>")
        }
    }
}