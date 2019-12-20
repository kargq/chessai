package pieces

import Board
import BoardPosition
import Move
import Tile
import kotlin.math.abs
import kotlin.math.sign


class King(black: Boolean = false) : Piece(black) {

    /*
        Castling: Can only happen once, and only if king and rook did not move at all throughout the game.
        The king has not previously moved;
        Your chosen rook has not previously moved;
        There must be no pieces between the king and the chosen rook;
        The king is not currently in check;
        Your king must not pass through a square that is under attack by enemy pieces;
        The king must not end up in check.
         */
    override fun checkPieceMoveConstraints(board: Board, move: Move): Boolean {
        val start = board.getTile(move.getStart())
        val end = board.getTile(move.getEnd())
        if (checkCastling(board, start, end)) {
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

    override fun movePiece(board: Board, move: Move) {
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
            super.movePiece(board, move)
        }
    }

    override fun copyPiece(): King {
        val copy = King(black)
        return copy
    }

    fun checkCastling(board: Board, start: Tile, end: Tile): Boolean {
        return if (end.piece is Rook) {
            if (atStartPosition(start)) {
                val endPiece = end.piece as Rook
                return (!hasMoved && !endPiece.hasMoved && !opppnents(
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