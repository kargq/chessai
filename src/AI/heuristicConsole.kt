package AI

import Board
import java.util.*

val EXIT = listOf("EXIT", "exit")
fun main() {
    var currIn = ""
    val input = Scanner(System.`in`)
    while (currIn !in EXIT) {
//        currIn = input.next()
        val board = Board(System.`in`)

        println("Heuristic: ${heuristic(board, true)}")
        println("Check black:${board.isKingInCheckmate(true)}")
        println("Check white:${board.isKingInCheckmate(false)}")
        println("Checkmate black:${board.isKingInCheck(true)}")
        println("Checkmate white:${board.isKingInCheck(false)}")
    }
}
