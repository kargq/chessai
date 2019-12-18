package pieces

import Board
import shared.debug

class Queen(black: Boolean = false) : Piece(black) {

    override fun validMove(board: Board, start: Tile, end: Tile): Boolean {
//        debug("checking queen")
        return if (validStartEnd(start, end)) {
//            debug("Valid start and end for queen")
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

    override fun getCopy(): Queen {
        return Queen(black)
    }

    override fun toString(): String {
        return if (black) {
            "QIB"
        } else {
            "QIW"
        }
    }
}