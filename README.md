# posidonMD
A special custom version of markdown.

Supports code highlighting for Kotlin.

#### Supported features
- **Bold** text: \*text\*
- _Italic_ text: \_text\_
- Gibberish/obscured text: |text|
- Code fields:
    ```markdown
    @body:
    blablabla
    *blabla*bla
    
    @code: kotlin
    fun someFunction(p: Any) {
        println("some function got $p passed to it")
    }
    @body:
    this code blablabla
    
    blabla blablabla
    ```
    - Supported languages:
        - Kotlin
        - pMD (this language)
- Urls: \[name](url)
- Images/iframes: !\[name](url)
- Quotes: "text"