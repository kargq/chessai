package pieces

import Board
import shared.*
import kotlin.math.abs
import kotlin.math.sign


class King(black: Boolean = false) : Piece(black) {

    fun getHasMoved(board: Board): Boolean {
        return if (black) {
            board.blackKingMoved
        } else {
            board.whiteKingMoved
        }
    }

    fun setHasMoved(board: Board, value: Boolean) {
        if (black) {
            board.blackKingMoved = value
        } else {
            board.whiteKingMoved = value
        }
    }

    // TODO: Test castling
    /*
    Castling: Can only happen once, and only if king and rook did not move at all throughout the game.
    The king has not previously moved;
    Your chosen rook has not previously moved;
    There must be no pieces between the king and the chosen rook;
    The king is not currently in check;
    Your king must not pass through a square that is under attack by enemy pieces;
    The king must not end up in check.
     */
    override fun validMove(board: Board, start: Tile, end: Tile): Boolean {
        if (checkCastling(board, start, end)) {
            debugln("Move from $start to $end is apparently castling. HasMoved: ${getHasMoved(board)} ")
            debugln("The actual board: ${board.getParseableStateString()}")
            debugln("black King moved: ${board.blackKingMoved}")
            return true
        }
        if (!validStartEnd(start, end)) return false
        return if (checkValidShift(start, end)) {
            if (!end.empty()) {
                opppnents(start, end)
            } else {
                true
            }
        } else false
    }

    fun atStartPosition(pos: BoardPosition): Boolean {
        return if (black) {
            pos.x == 4 && pos.y == 0
        } else {
            pos.x == 4 && pos.y == 7
        }
    }

    override fun makeMove(board: Board, move: Move) {
        debug("Make move called on king")
        val flag = validMove(board, move)
        if (checkCastling(board, board.getTile(move.getStart()), board.getTile(move.getEnd()))) {
            val rookDirection = (move.endX - move.startX).sign
            val moveBy = 2 * rookDirection
            val kingX = move.startX + moveBy
            val rookX = kingX - rookDirection
            val y = move.startY
            val king = board.getTile(move.getStart()).piece!!
            val rook = board.getTile(move.getEnd()).piece!!
            board.setPiece(move.getStart(), null)
            board.setPiece(move.getEnd(), null)
            board.setPiece(kingX, y, king)
            board.setPiece(rookX, y, rook)
        } else {
            super.makeMove(board, move)
        }
        if (getHasMoved(board)) {
            setHasMoved(board, flag)
            debugln("HAS MOVED FLAG SET")
        }
    }

    override fun getCopy(): King {
        val copy = King(black)
        return copy
    }

    fun checkCastling(board: Board, start: Tile, end: Tile): Boolean {
        return if (end.piece is Rook) {
            if (atStartPosition(start)) {
                val endPiece = end.piece as Rook
                return (!getHasMoved(board) && !endPiece.getHasMoved(board) && !opppnents(
                    start,
                    end
                ) && checkHorizontalUnblocked(
                    board,
                    start,
                    end
                ))
            } else {
                false
            }
        } else false
    }


    private fun checkValidShift(start: Tile, end: Tile): Boolean {
        return abs(start.x - end.x) <= 1 && abs(start.y - end.y) <= 1
    }

    override fun toString(): String {
        return if (black) {
            "KIB"
        } else {
            "KIW"
        }
    }
}