import java.util.*

var currIn = ""
val EXIT = listOf("EXIT", "exit")


//class Move(val startX, val endX, val startY, val ) {
//
//}

fun main() {
    val input = Scanner(System.`in`)

    val game = Game().startGameLoop()
//    while (currIn !in EXIT) {
//        println(game.board.toString())
//        currIn = input.next()
//        when (currIn) {
//            "MOVE" -> {
//                try {
//                    println("Start X")
//                    val startX = input.nextInt()
//                    println("Start Y")
//                    val startY = input.nextInt()
//
//                    println("End X")
//                    val endX = input.nextInt()
//                    println("End Y")
//                    val endY = input.nextInt()
//
//                    println("Sure ok")
//                    game.makeMove(startX, startY, endX, endY)
////                    println(game.board.toString())
//
//
//                } catch (e: Exception) {
//                    print("You fucked up")
//                }
//            }
//        }
//    }
}