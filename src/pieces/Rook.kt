package pieces

import Board

class Rook(black: Boolean = false, var hasMoved: Boolean = false) : Piece(black) {
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

    override fun getCopy(): Rook {
        return Rook(black)
    }

    override fun makeMove(board: Board, move: Move) {
        val flag = validMove(board, move)
        super.makeMove(board, move)
        hasMoved = flag
    }

    override fun toString(): String {
        return if (black) {
            "RKB"
        } else {
            "RKW"
        }
    }
}