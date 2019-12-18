package pieces

import Board
import kotlin.math.abs


class King(black: Boolean = false, var hasMoved: Boolean = false) : Piece(black) {
    /*
    Castling: Can only happen once, and only if king and rook did not move at all throughout the game.
    The king has not previously moved;
    Your chosen rook has not previously moved;
    There must be no pieces between the king and the chosen rook;
    The king is not currently in check;
    Your king must not pass through a square that is under attack by enemy pieces;
    The king must not end up in check.
     */
    override fun validMove(board: Board, start: Tile, end: Tile): Boolean {
        if (checkCastling(board, start, end)) return true
        if (!validStartEnd(start, end)) return false
        return if (checkValidShift(start, end)) {
            if (end.piece != null) {
                opppnents(start, end)
            } else {
                true
            }
        } else false
    }

    fun checkCastling(board: Board, start: Tile, end: Tile): Boolean {
        return if (end.piece is Rook) {
            val endPiece = end.piece as Rook
            return (!hasMoved && !endPiece.hasMoved && !opppnents(
                start,
                end
            ) && checkHorizontalUnblocked(
                board,
                start,
                end
            ))
        } else false
    }


    private fun checkValidShift(start: Tile, end: Tile): Boolean {
        return abs(start.x - end.x) == 1 || abs(start.y - end.y) == 1
    }

    override fun toString(): String {
        return if (black) {
            "KIB"
        } else {
            "KIW"
        }
    }
}