package pieces

import Board
import Move
import Tile

class Rook(black: Boolean = false) : Piece(black) {

    override fun checkPieceMoveConstraints(board: Board, move: Move): Boolean {
        val start = board.getTile(move.getStart())
        val end = board.getTile(move.getEnd())
        if (validStartEnd(start, end)) return when {
            onlyHorizontal(
                start,
                end
            ) -> checkHorizontalUnblocked(board, start, end)
            onlyVertical(
                start,
                end
            ) -> checkVerticalUnblocked(board, start, end)
            else -> false
        } else {
            // Handle castling case
            return checkRookCastling(board, start, end)
        }
    }

    fun checkRookCastling(board: Board, start: Tile, end: Tile): Boolean {
        if (!opppnents(start, end)) {
            if (end.piece != null) {
                val endKing = end.piece!!
                if (endKing is King) {
                    return endKing.checkCastling(board, start, end)
                }
            }
        }
        return false
    }

    override fun copyPiece(): Rook {
        return Rook(black)
    }

    override fun movePiece(board: Board, move: Move) {
        if (checkRookCastling(board, board.getTile(move.getStart()), board.getTile(move.getEnd()))) {
            val king = board.getTile(move.getEnd()).piece!! as King
            king.movePiece(board, move)
        } else {
            super.movePiece(board, move)
        }
    }

    override fun toString(): String {
        return if (black) {
            "RKB"
        } else {
            "RKW"
        }
    }
}