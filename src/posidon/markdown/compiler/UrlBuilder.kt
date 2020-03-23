package posidon.markdown.compiler

class UrlBuilder {

    private var url = StringBuilder()
    private var name = StringBuilder()
    private var activeBuilder = name

    var atUrl = false
        private set

    var isLoad = false

    override fun toString() = if (isLoad) when {
        url.endsWith(".png") ||
        url.endsWith(".jpg") ||
        url.endsWith(".svg") ||
        url.endsWith(".jpeg") ||
        url.endsWith(".tiff") ||
        url.endsWith(".webp") -> "<img src=\"" + url.append("\"alt=\"").append(name.toString()).append("\"/>").toString()
        else -> "<iframe src=\"" + url.append("\">").append(name.toString()).append("</iframe>").toString()
    } else "<a href=\"" + url.append("\">").append(name.toString()).append("</a>").toString()

    fun jumpToUrl() {
        atUrl = true
        activeBuilder = url
    }

    fun append(char: Char) = activeBuilder.append(char)
    fun append(string: String) = activeBuilder.append(string)
}