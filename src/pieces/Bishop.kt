package pieces

import Board


class Bishop(black: Boolean = false) : Piece(black) {
    override fun validMove(board: Board, start: Tile, end: Tile): Boolean {
        return validStartEnd(start, end) && onlyDiagonal(
            start,
            end
        ) && checkDiagonalUnblocked(board, start, end)
    }

    override fun toString(): String {
        return if (black) {
            "BIB"
        } else {
            "BIW"
        }
    }

    override fun getCopy(): Bishop {
        return Bishop(black)
    }
}