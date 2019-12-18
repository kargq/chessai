package pieces

import Board
import kotlin.math.abs

class Knight(black: Boolean = false) : Piece(black) {
    override fun validMove(board: Board, start: Tile, end: Tile): Boolean {
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

}