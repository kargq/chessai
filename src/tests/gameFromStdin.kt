package tests

import Game
import constructBoardFromInput

fun main() {
    val game = Game(board = constructBoardFromInput(System.`in`))
    game.startGameLoop()
}