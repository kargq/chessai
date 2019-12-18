package AI

import Board
import Game
import Player
import pieces.*
import shared.*
import kotlin.math.*

class AIPlayer(black: Boolean) : Player(black) {
    override fun determineNextMove(
        board: Board,
        onDetermined: (Move) -> Unit
    ) {
        onDetermined(determineMove(board, black))
    }

    override fun sendMessage(umm: Any) {
        // Do nothing
    }
}

const val KING_WEIGHT = 10
const val QUEEN_WEIGHT = 8
const val BISHOP_WEIGHT = 5
const val ROOK_WEIGHT = 5
const val PAWN_WEIGHT = 2
const val KNIGHT_WEIGHT = 6

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
                    debugln("Eliminated, ${ab.alpha} ${ab.beta}")
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
                    debugln("Eliminated, ${ab.alpha} ${ab.beta}")
                    return@forAllPieceTiles
                }
            }
        }
        return value
    }
}


fun heuristic(board: Board, black: Boolean): Int {

    debugai("Heuristic for ${getColorText(black)}")
    var value = 0

    board.forAllPieceTiles(black) { tile, piece ->
        when {
            piece is King -> value += KING_WEIGHT
            piece is Queen -> value += QUEEN_WEIGHT
            piece is Bishop -> value += BISHOP_WEIGHT
            piece is Knight -> value += KNIGHT_WEIGHT
            piece is Rook -> value += ROOK_WEIGHT
            piece is Pawn -> value += PAWN_WEIGHT
        }
    }

    board.forAllPieceTiles(!black) { tile, piece ->
        when {
            piece is King -> value -= KING_WEIGHT
            piece is Queen -> value -= QUEEN_WEIGHT
            piece is Bishop -> value -= BISHOP_WEIGHT
            piece is Knight -> value -= KNIGHT_WEIGHT
            piece is Rook -> value -= ROOK_WEIGHT
            piece is Pawn -> value -= PAWN_WEIGHT
        }
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

    return value
}

fun main() {
    val game = Game(blackPlayer = AIPlayer(true))
    game.startGameLoop()
}