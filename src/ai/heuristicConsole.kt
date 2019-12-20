package ai

import constructBoardFromInput
import java.util.*

val EXIT = listOf("EXIT")
fun main() {
    var currIn = ""
    val input = Scanner(System.`in`)
    while (currIn !in EXIT) {
        val board = constructBoardFromInput(System.`in`)
        println("Heuristic: ${heuristic(board, true)}")
        println("Checkmate :${board.kingInCheckMate}")
        println("Check :${board.kingInCheck}")
        println("Stalemate :${board.stalemate}")
        currIn = input.next().trim().toUpperCase()
    }
}
