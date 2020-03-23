package posidon.markdown.compiler

import posidon.markdown.compiler.code.CodeAppender
import java.io.File
import java.nio.file.Files

val MainBuilder = StringBuilder("<!DOCTYPE HTML><html lang=\"en\"><head>" +
        "<meta charset=\"utf-8\">" +
        "<link rel=\"stylesheet\" href=\"style.css\">" +
        "<link rel=\"icon\" href=\"img/favicon.png\">" +
        "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, shrink-to-fit=no\">" +
        "<meta name=\"theme-color\" content=\"#000000\">" +
        "<meta name=\"twitter:card\" content=\"summary_large_image\">")

var curCodeBlock = CodeBlock.Head
var currentLine = 0
var lastCodeLine = 0
var lastCodeClassI = 0

var urlBuilder: UrlBuilder? = null
val curTextStyle = TextStyle()

lateinit var title: String
lateinit var fileName: String
lateinit var codeAppender: CodeAppender

fun main(args: Array<String>) {
    println("Loading...")
    fileName = args[0]
    val file = File(fileName)

    if (!file.exists()) {
        val similarFiles = file.absoluteFile.parentFile.listFiles{f->simplifyForSearch(f.nameWithoutExtension)==simplifyForSearch(file.nameWithoutExtension)&&f.isFile&&(!f.name.contains('.')||f.name.endsWith(".md")||f.name.endsWith(".txt"))}
        if (similarFiles.isEmpty()) exitWithError(ErrorType.IO, "\"${file.path}\" isn't a valid file or directory")
        else exitWithError(ErrorType.IO, "\"${file.path}\" isn't a valid file or directory, did you mean \"${similarFiles[0].relativeToOrSelf(File(System.getProperty("user.dir")))}\"?")
    }

    if (file.isDirectory) {
        for (child in file.list {f,s -> f.isFile && s.endsWith(".md")}) main(arrayOf(child))
        return
    }

    println("Compiling...")
    file.forEachLine { when (curCodeBlock) {
        CodeBlock.Head -> when {
            it.startsWith("tabTitle") -> {
                val eqI = it.indexOf('=')
                if (eqI == -1) exitWithError(ErrorType.Header, "'=' expected after \"tabTitle\"")
                MainBuilder.append("<title>").append(it.substring(eqI + 1).trim()).append("</title>")
            }
            it.startsWith("title") -> {
                val eqI = it.indexOf('=')
                if (eqI == -1) exitWithError(ErrorType.Header, "'=' expected after \"title\"")
                title = it.substring(eqI + 1).trim()
                MainBuilder.append("<meta property=\"og:title\" content=\"").append(title).append("\">")
            }
            it.startsWith("description") -> {
                val eqI = it.indexOf('=')
                if (eqI == -1) exitWithError(ErrorType.Header, "'=' expected after \"keywords\"")
                val description = it.substring(eqI + 1).trim()
                MainBuilder.append("<meta name=\"description\" content=\"").append(description).append("\">")
                MainBuilder.append("<meta property=\"og:description\" content=\"").append(description).append("\">")
            }
            it.startsWith("author") -> {
                val eqI = it.indexOf('=')
                if (eqI == -1) exitWithError(ErrorType.Header, "'=' expected after \"author\"")
                MainBuilder.append("<meta name=\"author\" content=\"").append(it.substring(eqI + 1).trim()).append("\">")
            }
            it.startsWith("keywords") -> {
                val eqI = it.indexOf('=')
                if (eqI == -1) exitWithError(ErrorType.Header, "'=' expected after \"keywords\"")
                MainBuilder.append("<meta name=\"keywords\" content=\"").append(it.substring(eqI + 1).trim()).append("\">")
            }
            it.trim() == "@body:" -> {
                MainBuilder.append("</head><body><h1>").append(title).append("</h1>")
                curCodeBlock = CodeBlock.Body
            }
            !(it.startsWith(" ") || it.startsWith("")) -> exitWithError(ErrorType.Header, "$it doesn't mean anything")
        }
        CodeBlock.Body -> {
            if (it.trim().startsWith("@code:")) {
                if (currentLine - lastCodeLine == 1) {
                    if (MainBuilder.substring(lastCodeClassI-6, lastCodeClassI) == "bottom") {
                        MainBuilder.delete(lastCodeClassI - 6, lastCodeClassI)
                        MainBuilder.insert(lastCodeClassI - 6, " center")
                    } else MainBuilder.insert(lastCodeClassI, " top")
                    lastCodeClassI = MainBuilder.length + 23
                    MainBuilder.append("<div class=\"code bottom\">")
                } else {
                    lastCodeClassI = MainBuilder.length + 16
                    MainBuilder.append("<div class=\"code\">")
                }
                codeAppender = CodeAppender[it.substring(6)]
                curCodeBlock = CodeBlock.Code
                return@forEachLine
            }
            var i = 0
            while (i != it.length) {
                when (val char = it[i]) {
                    '"' -> normalAppend("<span class=\"q\">\"</span>")
                    '*' -> if (urlBuilder?.atUrl != true) if (curTextStyle.bold) {
                        normalAppend("</b>")
                        curTextStyle.bold = false
                    } else {
                        normalAppend("<b>")
                        curTextStyle.bold = true
                    }
                    '_' -> if (urlBuilder?.atUrl != true) if (curTextStyle.italic) {
                        normalAppend("</i>")
                        curTextStyle.italic = false
                    } else {
                        normalAppend("<i>")
                        curTextStyle.italic = true
                    }
                    '|' -> if (urlBuilder?.atUrl != true) if (curTextStyle.gibberish) {
                        normalAppend("</span>")
                        curTextStyle.gibberish = false
                    } else {
                        normalAppend("<span class=\"gibberish\">")
                        curTextStyle.gibberish = true
                    }
                    '!' -> if (i != it.length && it[i+1] == '[') {
                        urlBuilder = UrlBuilder().apply { isLoad = true }
                        i++
                    }
                    '[' -> {
                        urlBuilder = UrlBuilder()
                    }
                    ']' -> urlBuilder?.let { urlBuilder ->
                        urlBuilder.jumpToUrl()
                        do { i++; if (it[i] != ' ' && it[i] != '(') exitWithError(ErrorType.Url, "'" + it[i] + "' isn't allowed between [<text>] and (<url>)") }
                        while (it[i] != '(' && i != it.length)
                    }
                    ')' -> if (urlBuilder != null) {
                        MainBuilder.append(urlBuilder.toString())
                        urlBuilder = null
                    }
                    else -> normalAppend(char)
                }
                i++
            }
            MainBuilder.append("<br>")
        }
        CodeBlock.Code -> {
            when {
                it.trim() == "@body:" -> {
                    curCodeBlock = CodeBlock.Body
                    codeAppender.appendToText()
                    MainBuilder.append("</div>")
                    lastCodeLine = currentLine
                }
                else -> codeAppender.append(it)
            }
        }
    }; currentLine++ }

    MainBuilder.append("</body><script type=\"text/javascript\" src=\"gibberishText.js\"></script></html>")

    Files.writeString(File(file.absoluteFile.parent + '/' + file.nameWithoutExtension + ".html").toPath(), MainBuilder.toString())

    println("Done!")
}

fun normalAppend(char: Char) = urlBuilder?.append(char) ?: MainBuilder.append(char)
fun normalAppend(string: String) = urlBuilder?.append(string) ?: MainBuilder.append(string)