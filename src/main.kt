import AI.AIPlayer
import java.lang.Exception
import java.util.*

fun main() {
    // Normal game
    // Enter game state

    // Select white player
    // Select black player

    // Start

    val input = Scanner(System.`in`)

    var currInput = ""


    while (currInput != "EXIT") {
        try {
            var board = Board()
            println("1. Normal game 2. Enter custom game state")
            val type = input.nextInt()
            when (type) {
                1 -> {
                }
                2 -> {
                    println("Will construct board form standard in. Sample input: ")
                    board = constructBoardFromInput(System.`in`)
                }
            }
            println("Select white player type")
            var whitePlayer = selectPlayer(input, false)
            println("Select black player type")
            var blackPlayer = selectPlayer(input, true, whitePlayer)

            val game = Game(board, blackPlayer, whitePlayer)
            game.startGameLoop()

        } catch (e: Exception) {
            println("Invalid input, please try again.")
        }
        currInput = input.next()
    }
}

fun selectPlayer(input: Scanner, black: Boolean, player1: Player? = null): Player {
    println("1. AI 2. Console")
    val option = input.nextInt()
    when (option) {
        1 -> {
            println("Select ply: ")
            val ply = input.nextInt()
            return AIPlayer(black, print = player1 is AIPlayer, ply = ply)
        }
        2 -> {
            println("You will be able to enter player moves in console.")
            return ConsolePlayer(black)
        }
    }
    return ConsolePlayer(black)
}