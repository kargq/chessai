package tests

import Board
import Game
import java.io.FileInputStream

fun main() {
//    val board = Board(FileInputStream("tests/inp1"))
//    print(board)
//    println(board.isKingInCheck(false))
//    println(board.isKingInCheckmate(false))
//    println("======")
//    val board2 = Board(FileInputStream("tests/checktest1"))
//    println(board2)
//    println(board2.isKingInCheck(false))
//    println("======")
//    val board3 = Board(FileInputStream("tests/stalemate"), blackKingMoved = true, blackRookMoved = true)
//    println(board3)
//    println(board3.isKingInStalemate(true))
//    println("======")
//    val board4 = Board(FileInputStream("tests/queenwplease"), blackKingMoved = true, blackRookMoved = true)
//    println(board4)
//    println(board4.isKingInCheck(true))
//    println(board4.isKingInCheckmate(true))
//    println(board4.isKingInStalemate(true))

//    val game = Game(board = Board(FileInputStream("tests/promotion")))
//    game.startGameLoop()

    val game = Game(board = Board(FileInputStream("tests/knighttest")), blackTurn = true)
    game.startGameLoop()
}