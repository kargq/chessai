package pieces

import Board
import withinBounds
import kotlin.math.abs


class Pawn(black: Boolean = false) : Piece(black) {
    override fun validMove(board: Board, start: Tile, end: Tile): Boolean {
        if (!validStartEnd(start, end)) return false
        val startPiece = start.piece!!
        val diff = end.y - start.y
        val direction = if (startPiece.black) -1 else 1
        when {
            onlyVertical(start, end) -> {
                debug("IT IS ONLY VERTICAL, WOYEARRRAK")
                // Can only move if end is empty
                if (end.empty()) {

                    val allowedDiff = direction * if (startTile(start)) 2 else 1
                    return diff == allowedDiff
                }
            }
            onlyDiagonal(start, end) -> {
                // Handle normal kill move
                if (opppnents(start, end) && diff == direction) {
                    return true
                }
                // check en passant
                // if previous move skipped the current captureable position, do it
                return checkEnPassant(board, start, end)
            }
        }
        return false
    }

    private fun getSkippedOpponentX(x: Int): List<Int> {
        val result: MutableList<Int> = mutableListOf()
        if (withinBounds(x - 1)) result.add(x - 1)
        if (withinBounds(x + 1)) result.add(x + 1)
        return result
    }

    private fun startTile(tile: Tile): Boolean {
        return if (black) {
            tile.y == 1
        } else {
            tile.y == 6
        }
    }

    private fun checkEnPassant(board: Board, start: Tile, end: Tile): Boolean {
        val diff = end.y - start.y
        // check en passant
        // if previous move skipped the current captureable position, do it
        if (abs(diff) == 1) {
            if (board.pawnSkip != null) {
                val skippedPawnTile = board.getTile(board.pawnSkip!!)
                if (skippedPawnTile.piece != null) {
                    val piece = skippedPawnTile.piece
                    if (piece is Pawn) {
                        // We know the piece is a pawn
                        // Check start pos x against this position
                        if (abs(start.x - skippedPawnTile.x) == 1 && start.y == skippedPawnTile.y && skippedPawnTile.x == start.x) {
                            // There's a skipped piece
                            // It is captureable in the next move
                            // next move is indeed trying capture
                            return true
                        }
                    }
                }
            }
        }
        return false
    }

    override fun toString(): String {
        return if (black) {
            "PNB"
        } else {
            "PNW"
        }
    }
}