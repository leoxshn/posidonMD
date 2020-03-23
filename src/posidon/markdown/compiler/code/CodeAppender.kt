package posidon.markdown.compiler.code

import posidon.markdown.compiler.MainBuilder

abstract class CodeAppender {

    protected val builder = StringBuilder()

    abstract fun append(line: String)

    fun appendToText() { MainBuilder.append(builder.toString()) }

    companion object {
        operator fun get(langName: String) = when (langName.toLowerCase().replace(Regex("[ .,_]"), "")) {
            "kotlin" -> KotlinCodeAppender()
            "pmd" -> PosidonMDCodeAppender()
            else -> FallbackCodeAppender()
        }
    }
}