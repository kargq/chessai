package pieces

import Board
import Move


class Bishop(black: Boolean = false) : Piece(black) {
    override fun checkPieceMoveConstraints(board: Board, move: Move): Boolean {
        val start = board.getTile(move.getStart())
        val end = board.getTile(move.getEnd())
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

    override fun copyPiece(): Bishop {
        return Bishop(black)
    }
}