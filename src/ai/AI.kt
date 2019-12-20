package ai

import Board
import Game
import Move
import Player
import Tile
import pieces.*
import shared.*
import kotlin.math.*

val pawntable = arrayOf(
    20, 20, 20, 20, 20, 20, 20, 20,
    70, 70, 70, 70, 70, 70, 70, 70,
    30, 30, 40, 50, 50, 40, 30, 30,
    25, 25, 30, 45, 45, 30, 25, 25,
    20, 20, 20, 40, 40, 20, 20, 20,
    25, 15, 10, 20, 20, 10, 15, 25,
    25, 30, 30, 0, 0, 30, 30, 25,
    20, 20, 20, 20, 20, 20, 20, 20
)

val knightstable = arrayOf(
    0, 10, 20, 20, 20, 20, 10, 0,
    10, 30, 50, 50, 50, 50, 30, 10,
    20, 50, 60, 65, 65, 60, 50, 20,
    20, 55, 65, 70, 70, 65, 55, 20,
    20, 50, 65, 70, 70, 65, 50, 20,
    20, 55, 60, 65, 65, 60, 55, 20,
    10, 30, 50, 55, 55, 50, 30, 10,
    0, 10, 20, 20, 20, 20, 10, 0
)

val bishopstable = arrayOf(
    0, 10, 10, 10, 10, 10, 10, 0,
    10, 20, 20, 20, 20, 20, 20, 10,
    10, 20, 25, 30, 30, 25, 20, 10,
    10, 25, 25, 30, 30, 25, 25, 10,
    10, 20, 30, 30, 30, 30, 20, 10,
    10, 30, 30, 30, 30, 30, 30, 10,
    10, 25, 20, 20, 20, 20, 25, 10,
    0, 10, 10, 10, 10, 10, 10, 18
)

val rookstable = arrayOf(
    5, 5, 5, 5, 5, 5, 5, 5,
    10, 15, 15, 15, 15, 15, 15,
    10, 0, 5, 5, 5, 5, 5, 5, 0,
    0, 5, 5, 5, 5, 5, 5, 0, 0, 5,
    5, 5, 5, 5, 5, 0, 0, 5, 5, 5,
    5, 5, 5, 0, 0, 5, 5, 5, 5, 5,
    5, 0, 5, 5, 5, 10, 10, 5, 5, 5
)

val queenstable = arrayOf(
    0, 10, 10, 15, 15, 10, 10, 0,
    10, 20, 20, 20, 20, 20, 20, 10,
    10, 20, 25, 25, 25, 25, 20, 10,
    15, 20, 25, 25, 25, 25, 20, 15,
    20, 20, 25, 25, 25, 25, 20, 15,
    10, 25, 25, 25, 25, 25, 20, 10,
    10, 20, 25, 20, 20, 20, 20, 10,
    0, 10, 10, 15, 15, 10, 10, 0
)

val kingstable = arrayOf(
    20, 10, 10, 0, 0, 10, 10, 20,
    20, 10, 10, 0, 0, 10, 10, 20,
    20, 10, 10, 0, 0, 10, 10, 20,
    20, 10, 10, 0, 0, 10, 10, 20,
    30, 20, 20, 10, 10, 20, 20, 30,
    40, 30, 30, 30, 30, 30, 30, 40,
    70, 70, 50, 50, 50, 50, 70, 70,
    70, 80, 60, 50, 50, 60, 80, 70
)

val blank = arrayOf(
    0, 0, 0, 0, 0, 0, 0, 0,
    0, 0, 0, 0, 0, 0, 0, 0,
    0, 0, 0, 0, 0, 0, 0, 0,
    0, 0, 0, 0, 0, 0, 0, 0,
    0, 0, 0, 0, 0, 0, 0, 0,
    0, 0, 0, 0, 0, 0, 0, 0,
    0, 0, 0, 0, 0, 0, 0, 0,
    0, 0, 0, 0, 0, 0, 0, 0
)

fun lookupAt(lookupFrom: Array<Int>, x: Int, y: Int): Int {
    return lookupFrom.getOrElse(8 * y + x) { 0 }
}


fun getWeight(tile: Tile, black: Boolean): Int {
    val lookupFrom = when (tile.piece) {
        is King -> kingstable
        is Queen -> queenstable
        is Pawn -> pawntable
        is Knight -> knightstable
        is Bishop -> bishopstable
        is Rook -> rookstable
        else -> blank
    }

    val positionResult = if (black) {
        // Flip lookup
        lookupAt(lookupFrom, tile.x, (7 - tile.y))
    } else lookupAt(lookupFrom, tile.x, tile.y)

    val weight = when (tile.piece) {
        is King -> KING_WEIGHT
        is Queen -> QUEEN_WEIGHT
        is Pawn -> PAWN_WEIGHT
        is Knight -> KNIGHT_WEIGHT
        is Bishop -> BISHOP_WEIGHT
        is Rook -> ROOK_WEIGHT
        else -> 1
    }

    return weight * positionResult
}

class AIPlayer(black: Boolean, val print: Boolean = false, val ply: Int = 3) : Player(black) {
    override fun determineNextMove(
        board: Board,
        onDetermined: (Move) -> Unit
    ) {
        onDetermined(determineMove(board, black, ply))
    }

    override fun sendMessage(umm: Any) {
        // Do nothing
        if (print) println(umm)
    }
}

const val KING_WEIGHT = 10 // Checkmate really handles king
const val QUEEN_WEIGHT = 90
const val BISHOP_WEIGHT = 30
const val ROOK_WEIGHT = 50
const val KNIGHT_WEIGHT = 30
const val PAWN_WEIGHT = 10

fun determineMove(board: Board, black: Boolean, ply: Int = 3): Move {
    debugai("Determine move for ${getColorText(black)}")
    var bestHeur = Integer.MIN_VALUE
    var result = Move(-1, -1, -1, -1)
    val validMoves = board.getAllNextValidMoves()
    val ab = AlphaBetaStore()
    for (i in validMoves.indices) {
        // TODO: See if I need the copy
        val boardState = board.getCopy()
        val move = validMoves[i]
        boardState.executeMove(move)
        val currHeur =
            alphabeta(boardState, ply, ab, heuristicPlayerBlack = black, maxPlayer = false)
        debugai("$currHeur, $move")
        if (currHeur > bestHeur) {
            bestHeur = currHeur
            result = move
        }
    }
    println("Result $result")
    return result
}


data class AlphaBetaStore(
    var alpha: Int = Integer.MIN_VALUE,
    var beta: Int = Integer.MAX_VALUE
)

fun alphabeta(board: Board, ply: Int, ab: AlphaBetaStore, heuristicPlayerBlack: Boolean, maxPlayer: Boolean): Int {
    if (ply == 0 || board.kingInCheckMate || board.stalemate
    ) {
        return heuristic(board, heuristicPlayerBlack)
    }
    if (maxPlayer) {
        var value = Integer.MIN_VALUE
        for ((index, move) in board.getAllNextValidMoves().withIndex()) {
            // For all possible moves for player,
            val childBoard = board.getCopy()
            childBoard.executeMove(move)
            value = max(value, alphabeta(childBoard, ply - 1, ab, heuristicPlayerBlack, false))
//            println("Info at $index of ${board.getAllNextValidMoves().size} at ply ${ply} abstore: ${ab} hash: ${System.identityHashCode(ab)}")
            ab.alpha = max(ab.alpha, value)
            if (ab.alpha >= ab.beta) {
                println("Max skipping at $index of ${board.getAllNextValidMoves().size} at ply ${ply} abstore: ${ab} hash: ${System.identityHashCode(ab)}")
                break
            }
        }
        return value
    } else {
        var value = Integer.MAX_VALUE
        for ((index, move) in board.getAllNextValidMoves().withIndex()) {
            val childBoard = board.getCopy()
            childBoard.executeMove(move)
            value = min(value, alphabeta(childBoard, ply - 1, ab, heuristicPlayerBlack, true))
//            println("Info at $index of ${board.getAllNextValidMoves().size} at ply ${ply} abstore: ${ab} hash: ${System.identityHashCode(ab)}")
            ab.beta = min(ab.beta, value)
            if (ab.alpha >= ab.beta) {
                println("Max skipping at $index of ${board.getAllNextValidMoves().size} at ply ${ply} abstore: ${ab} hash: ${System.identityHashCode(ab)}")
                break
            }
        }
        return value
    }
}

/**
 * The actual evaluation function
 */
fun heuristic(board: Board, black: Boolean): Int {
    var value = 0

    board.forAllPieceTiles(black) { tile, piece ->
        value += getWeight(tile, black)
    }

    board.forAllPieceTiles(!black) { tile, piece ->
        value -= getWeight(tile, !black)

    }

    val playerInCheckmate = board.kingInCheckMate && board.blackPlayerTurn == black
    val playerInCheck = board.kingInCheck && board.blackPlayerTurn == black

    val opponentInCheckmate = board.kingInCheckMate && board.blackPlayerTurn != black
    val opponentInCheck = board.kingInCheck && board.blackPlayerTurn != black


    if (playerInCheckmate) {
        debugai("${getColorText(black)} in checkmate")
        value -= 10000
    }
    if (opponentInCheckmate) {
        debugai("${getColorText(!black)} in checkmate")
        value += 10000
    }

    if (playerInCheck) {
        value -= 500
    }

    if (opponentInCheck) {
        value += 500
    }

    return value
}

fun main() {
    val game = Game(whitePlayer = AIPlayer(false, ply = 2), blackPlayer = AIPlayer(true, print = true, ply = 0))
    game.startGameLoop()
}