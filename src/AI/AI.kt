package AI

import Board
import Game
import Player
import pieces.*
import shared.*
import kotlin.math.*

val pawntable = arrayOf(
    0, 0, 0, 0, 0, 0, 0, 0,
    50, 50, 50, 50, 50, 50, 50, 50,
    10, 10, 20, 30, 30, 20, 10, 10,
    5, 5, 10, 25, 25, 10, 5, 5,
    0, 0, 0, 20, 20, 0, 0, 0,
    5, -5, -10, 0, 0, -10, -5, 5,
    5, 10, 10, -20, -20, 10, 10, 5,
    0, 0, 0, 0, 0, 0, 0, 0
)

val knightstable = arrayOf(
    -50, -40, -30, -30, -30, -30, -40, -50,
    -40, -20, 0, 0, 0, 0, -20, -40,
    -30, 0, 10, 15, 15, 10, 0, -30,
    -30, 5, 15, 20, 20, 15, 5, -30,
    -30, 0, 15, 20, 20, 15, 0, -30,
    -30, 5, 10, 15, 15, 10, 5, -30,
    -40, -20, 0, 5, 5, 0, -20, -40,
    -50, -40, -30, -30, -30, -30, -40, -50
)

val bishopstable = arrayOf(
    -20, -10, -10, -10, -10, -10, -10, -20,
    -10, 0, 0, 0, 0, 0, 0, -10,
    -10, 0, 5, 10, 10, 5, 0, -10,
    -10, 5, 5, 10, 10, 5, 5, -10,
    -10, 0, 10, 10, 10, 10, 0, -10,
    -10, 10, 10, 10, 10, 10, 10, -10,
    -10, 5, 0, 0, 0, 0, 5, -10,
    -20, -10, -10, -10, -10, -10, -10, -20
)

val rookstable = arrayOf(
    0, 0, 0, 0, 0, 0, 0, 0,
    5, 10, 10, 10, 10, 10, 10, 5,
    -5, 0, 0, 0, 0, 0, 0, -5,
    -5, 0, 0, 0, 0, 0, 0, -5,
    -5, 0, 0, 0, 0, 0, 0, -5,
    -5, 0, 0, 0, 0, 0, 0, -5,
    -5, 0, 0, 0, 0, 0, 0, -5,
    0, 0, 0, 5, 5, 0, 0, 0
)

val queenstable = arrayOf(
    -20, -10, -10, -5, -5, -10, -10, -20,
    -10, 0, 0, 0, 0, 0, 0, -10,
    -10, 0, 5, 5, 5, 5, 0, -10,
    -5, 0, 5, 5, 5, 5, 0, -5,
    0, 0, 5, 5, 5, 5, 0, -5,
    -10, 5, 5, 5, 5, 5, 0, -10,
    -10, 0, 5, 0, 0, 0, 0, -10,
    -20, -10, -10, -5, -5, -10, -10, -20
)

val kingstable = arrayOf(
    -30, -40, -40, -50, -50, -40, -40, -30,
    -30, -40, -40, -50, -50, -40, -40, -30,
    -30, -40, -40, -50, -50, -40, -40, -30,
    -30, -40, -40, -50, -50, -40, -40, -30,
    -20, -30, -30, -40, -40, -30, -30, -20,
    -10, -20, -20, -20, -20, -20, -20, -10,
    20, 20, 0, 0, 0, 0, 20, 20,
    20, 30, 10, 0, 0, 10, 30, 20
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

    return if (black) {
        // Flip lookup
        lookupAt(lookupFrom, tile.x, (7 - tile.y))
    } else lookupAt(lookupFrom, tile.x, tile.y)
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

const val KING_WEIGHT = 10
const val QUEEN_WEIGHT = 90
const val BISHOP_WEIGHT = 30
const val ROOK_WEIGHT = 50
const val KNIGHT_WEIGHT = 30
const val PAWN_WEIGHT = 10

fun determineMove(board: Board, black: Boolean, ply: Int = 3): Move {
    debugai("Determine move for ${getColorText(black)}")
    var bestHeur = Integer.MIN_VALUE
    var result: Move = Move(-1, -1, -1, -1)
    board.forAllPieceTiles(black) { tile, piece ->
        for (move in piece.generateAllValidMoves(board, tile)) {
            val boardState = board.getCopy()
            boardState.executeMove(move)
            val currHeur =
                alphabeta(boardState, ply, AlphaBetaStore(), black, black, true)
            debugai("$currHeur, $move")
            if (currHeur > bestHeur) {
                bestHeur = currHeur
                result = move
            }
        }
    }

    return result
}


data class AlphaBetaStore(
    var alpha: Int = Integer.MIN_VALUE,
    var beta: Int = Integer.MAX_VALUE
)

fun alphabeta(board: Board, ply: Int, ab: AlphaBetaStore, black: Boolean, blackMove: Boolean, maxPlayer: Boolean): Int {
    if (ply == 0 || board.isKingInCheckmate(false) ||
        board.isKingInCheckmate(true) || board.isKingInStalemate()
    ) {
        return heuristic(board, black)
    }
    if (maxPlayer) {
        var value = Integer.MIN_VALUE
        board.forAllPieceTiles(blackMove) { tile, piece ->
            for (move in piece.generateAllValidMoves(board, tile)) {
                // For all possible moves for player,
                val childBoard = board.getCopy()
                childBoard.executeMove(move)
                value = max(value, alphabeta(childBoard, ply - 1, ab, black, !blackMove, false))
                ab.alpha = max(ab.alpha, value)
                if (ab.alpha >= ab.beta) {
//                    println("Eliminated, ${ab.alpha} ${ab.beta}")
                    return@forAllPieceTiles
                }
            }
        }
        return value
    } else {
        var value = Integer.MAX_VALUE
        board.forAllPieceTiles(blackMove) { tile, piece ->
            piece.generateAllValidMoves(board, tile).forEach { move ->
                val childBoard = board.getCopy()
                childBoard.executeMove(move)
                value = min(value, alphabeta(childBoard, ply - 1, ab, black, !blackMove, true))
                ab.beta = min(ab.beta, value)
                if (ab.alpha >= ab.beta) {
//                    println("Eliminated, ${ab.alpha} ${ab.beta}")
                    return@forAllPieceTiles
                }
            }
        }
        return value
    }
}


fun heuristic(board: Board, black: Boolean): Int {

    var value = 0

    board.forAllPieceTiles(black) { tile, piece ->
        //        when {
//            piece is King -> value += KING_WEIGHT
//            piece is Queen -> value += QUEEN_WEIGHT
//            piece is Bishop -> value += BISHOP_WEIGHT
//            piece is Knight -> value += KNIGHT_WEIGHT
//            piece is Rook -> value += ROOK_WEIGHT
//            piece is Pawn -> value += PAWN_WEIGHT
//        }
        value += getWeight(tile, black)
    }

    board.forAllPieceTiles(!black) { tile, piece ->
        //        when {
//            piece is King -> value -= KING_WEIGHT
//            piece is Queen -> value -= QUEEN_WEIGHT
//            piece is Bishop -> value -= BISHOP_WEIGHT
//            piece is Knight -> value -= KNIGHT_WEIGHT
//            piece is Rook -> value -= ROOK_WEIGHT
//            piece is Pawn -> value -= PAWN_WEIGHT
//        }
        value -= getWeight(tile, black)

    }

    val playerInCheckmate = board.isKingInCheckmate(black)

    val opponentInCheckmate = board.isKingInCheckmate(!black)


    if (playerInCheckmate) {
        debugai("${getColorText(black)} in checkmate")
        value -= 1000
    }
    if (opponentInCheckmate) {
        debugai("${getColorText(black)} in checkmate")
        value += 1000
    }

//    debugai("Heuristic for ${getColorText(black)} is ${value}")
    return value
}

fun main() {
    val game = Game(whitePlayer = AIPlayer(false, ply = 1), blackPlayer = AIPlayer(true, print = true, ply = 3))
//    val game = Game(whitePlayer = AIPlayer(false))
//    val game = Game(blackPlayer = AIPlayer(true))
    game.startGameLoop()
}