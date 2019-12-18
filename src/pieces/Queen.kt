package pieces

import Board

class Queen(black: Boolean = false) : Piece(black) {
    override fun validMove(board: Board, start: Tile, end: Tile): Boolean {
        return if (validStartEnd(start, end)) {
            // Is able to go to end, check if unblocked and movement is valid.
            when {
                onlyDiagonal(start, end) -> checkDiagonalUnblocked(
                    board,
                    start,
                    end
                )
                onlyHorizontal(
                    start,
                    end
                ) -> checkHorizontalUnblocked(board, start, end)
                onlyVertical(start, end) -> checkVerticalUnblocked(
                    board,
                    start,
                    end
                )
                else -> false
            }
        } else false
    }

    override fun toString(): String {
        return if (black) {
            "QIB"
        } else {
            "QIW"
        }
    }
}