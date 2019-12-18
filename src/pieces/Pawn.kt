package pieces

import Board
import shared.debug
import withinBounds
import kotlin.math.abs


class Pawn(black: Boolean = false) : Piece(black) {
    override fun validMove(board: Board, start: Tile, end: Tile): Boolean {
        if (!validStartEnd(start, end)) return false
        val startPiece = start.piece!!
        val diff = end.y - start.y
        val direction = if (startPiece.black) 1 else -1
        if (validStartEnd(start, end)) {
            when {
                onlyVertical(start, end) -> {
                    if(end.empty() && checkVerticalUnblocked(board, start, end)) {
                        val allowedDiff = direction * if (startTile(start)) 2 else 1
                        return (diff == allowedDiff || diff == direction )
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
        }
        return false
    }

    override fun getCopy(): Piece {
        return Pawn(black)
    }

    fun atStartPosition(tile: Tile): Boolean {
        return (black && tile.y == 1) || (!black && tile.y == 6)
    }

    /**
     * Always call from start piece
     */
    override fun makeMove(board: Board, move: Move) {
        val start: Tile = board.getTile(move.getStart())
        val end: Tile = board.getTile(move.getEnd())
        val passant = checkEnPassant(board, start, end)

        val passantPos = board.pawnSkip

        if (validMove(board, start, end)) {
            start.piece?.let {
                if (atStartPosition(start)) {
                    board.pawnSkip = getPassantPos(start.x)
                } else {
                    board.pawnSkip = null
                }
            }
            end.piece = start.piece
            start.piece = null


        } else {
        }


        if (passant) board.setPiece(passantPos!!, null)
    }

    fun getPassantPos(x: Int): BoardPosition {
        return BoardPosition(x, if (black) 3 else 4)
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
                        if (abs(start.x - skippedPawnTile.x) == 1 && start.y == skippedPawnTile.y && skippedPawnTile.x == end.x) {
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