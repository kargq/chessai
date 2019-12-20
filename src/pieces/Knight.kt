package pieces

import Board
import Move
import Tile
import kotlin.math.abs

class Knight(black: Boolean = false) : Piece(black) {
    override fun checkPieceMoveConstraints(board: Board, move: Move): Boolean {
        val start = board.getTile(move.getStart())
        val end = board.getTile(move.getEnd())
        return validStartEnd(start, end) && checkValidShhift(start, end)
    }

    private fun checkValidShhift(start: Tile, end: Tile): Boolean {
        val yDiff = abs(end.x - start.x)
        val xDiff = abs(end.y - start.y)
        return (xDiff == 2 && yDiff == 1) || (xDiff == 1 && yDiff == 2)
    }

    override fun toString(): String {
        return if (black) {
            "KNB"
        } else {
            "KNW"
        }
    }

    override fun copyPiece(): Knight {
        return Knight(black)
    }

}