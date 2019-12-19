package pieces

import Board
import shared.debugln

class Rook(black: Boolean = false) : Piece(black) {

    fun getHasMoved(board: Board): Boolean {
        return if (black) {
            board.blackRookMoved
        } else {
            board.whiteRookMoved
        }
    }

    fun setHasMoved(board: Board, value: Boolean) {
        if (black) {
            board.blackRookMoved = value
        } else {
            board.whiteRookMoved = value
        }
    }

    override fun validMove(board: Board, start: Tile, end: Tile): Boolean {
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

    override fun getCopy(): Rook {
        return Rook(black)
    }

    override fun makeMove(board: Board, move: Move) {
        val flag = validMove(board, move)
        if (checkRookCastling(board, board.getTile(move.getStart()), board.getTile(move.getEnd()))) {
            val king = board.getTile(move.getEnd()).piece!! as King
            king.makeMove(board, move)
        } else {
            super.makeMove(board, move)
        }
        if (getHasMoved(board)) {
            setHasMoved(board, flag)
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