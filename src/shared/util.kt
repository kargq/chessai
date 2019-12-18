package shared

var enabled = true

fun debug(umm: Any = "") {
    if (enabled)
        print("DEBUG:/ $umm")
}

fun debugln(umm: Any = "") {
    if (enabled)
        println("DEBUG:/ $umm")
}

var aienabled = true


fun debugai(umm: Any = "") {
    if (aienabled)
    println("DEBUGAI:/ $umm")
}

fun getColorText(black: Boolean): String {
    return if (black) "Black" else "White"
}