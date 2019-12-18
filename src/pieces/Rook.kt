package pieces

import Board

class Rook(black: Boolean = false, var hasMoved: Boolean = false) : Piece(black) {
    override fun validMove(board: Board, start: Tile, end: Tile): Boolean {
        if (validStartEnd(start, end)) when {
            onlyHorizontal(
                start,
                end
            ) -> return checkHorizontalUnblocked(board, start, end)
            onlyVertical(
                start,
                end
            ) -> return checkVerticalUnblocked(board, start, end)
            else -> return false
        } else {
            // Handle castling case
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
    }

    override fun toString(): String {
        return if (black) {
            "RKB"
        } else {
            "RKW"
        }
    }
}