package shared

var enabled = false

fun debug(umm: Any = "") {
    if (enabled)
        print("DEBUG:/ $umm")
}

fun debugln(umm: Any = "") {
    if (enabled)
        println("DEBUG:/ $umm")
}

var aienabled = false

fun debugai(umm: Any = "") {
    if (aienabled)
        println("DEBUGAI:/ $umm")
}

fun getColorText(black: Boolean): String {
    return if (black) "Black" else "White"
}