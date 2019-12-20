package pieces

import Board
import BoardPosition
import Move
import Tile
import withinBounds
import kotlin.math.abs


class Pawn(black: Boolean = false) : Piece(black) {

    // The first move was a 2 forward
    var hasSkipped = false

    override fun checkPieceMoveConstraints(board: Board, move: Move): Boolean {
        val start = board.getTile(move.getStart())
        val end = board.getTile(move.getEnd())
        if (!validStartEnd(start, end)) return false
        val startPiece = start.piece!!
        val diff = end.y - start.y
        val direction = if (startPiece.black) 1 else -1
        if (validStartEnd(start, end)) {
            when {
                onlyVertical(start, end) -> {
                    if (end.empty() && checkVerticalUnblocked(
                            board,
                            start,
                            end
                        )
                    ) {
                        val allowedDiff = direction * if (startTile(start)) 2 else 1
                        return (diff == allowedDiff || diff == direction)
                    }
                }
                onlyDiagonal(start, end) -> {
                    // Handle normal kill move
                    return diff == direction && !end.empty()
                }
            }
        }
        return false
    }

    override fun copyPiece(): Piece {
        val result = Pawn(black)
        result.hasSkipped = hasSkipped
        return result
    }

    private fun atStartPosition(tile: Tile): Boolean {
        return (black && tile.y == 1) || (!black && tile.y == 6)
    }

    /**
     * Always call from start piece
     */
    override fun movePiece(board: Board, move: Move) {
        val start: Tile = board.getTile(move.getStart())
        val end: Tile = board.getTile(move.getEnd())

        end.piece = start.piece
        start.piece = null

        if (checkEnPassant(board, start, end)) {
            val passantPos = BoardPosition(end.x, if (black) 3 else 4)
            board.setPiece(passantPos, null)
        } else {
            // Handle promotion
            if (black) {
                if (end.y == 7) {
                    // Promote
                    board.setPiece(end, move.getNewPromotionPiece(black))
                }
            } else {
                if (end.y == 0) {
                    // Promote
                    board.setPiece(end, move.getNewPromotionPiece(black))
                }
            }
        }

        hasMoved = true
    }


    private fun startTile(tile: Tile): Boolean {
        return if (black) {
            tile.y == 1
        } else {
            tile.y == 6
        }
    }

    private fun canKillPassedY(black: Boolean): Int {
        return if (black) {
            4
        } else {
            3
        }
    }

    private fun checkEnPassant(board: Board, start: Tile, end: Tile): Boolean {
        if (abs(start.x - end.x) == 1 && abs(start.y - end.y) == 1 && canKillPassedY(black) == start.y) {
            val endPassantTile = if (withinBounds(end.x)) board.getTile(end.x, start.y) else null

            return (endPassantTile != null && endPassantTile.piece is Pawn && opppnents(
                start,
                endPassantTile
            ) && (endPassantTile.piece as Pawn).hasSkipped)
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