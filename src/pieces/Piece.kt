package pieces

import Board
import shared.debug
import kotlin.math.*


abstract class Piece(val black: Boolean) {

    abstract fun validMove(board: Board, start: Tile, end: Tile): Boolean

    /**
     * Always call from start piece
     */
    fun makeMove(board: Board, start: Tile, end: Tile) {

        if (validMove(board, start, end)) {
            end.piece = start.piece
            start.piece = null
        } else {
            debug("NOT A VALID MOVE")
        }
    }

    protected fun validStartEnd(start: Tile, end: Tile): Boolean {
        return !start.empty() && (end.empty() || opppnents(start, end))
    }

}


fun getStartValue(from: Int, to: Int): Int {
    return if (from < to) from + 1 else from - 1
}

fun getEndValue(from: Int, to: Int): Int {
    return if (to < from) to + 1 else from - 1
}

// Unblocked methods omit start and end.
fun checkHorizontalUnblocked(board: Board, start: Tile, end: Tile): Boolean {
    if (start.identicalPosition(end)) return true
    val startX = getStartValue(start.x, end.x)
    val endX = getEndValue(start.x, end.x)

    if (onlyHorizontal(start, end)) {
        val y = start.y
        for (x in startX..endX) {
            if (!board.getTile(x, y).empty()) return false
        }
        return true
    } else return false
}

fun checkVerticalUnblocked(board: Board, start: Tile, end: Tile): Boolean {
    if (start.identicalPosition(end)) return true
    val startY = getStartValue(start.y, end.y)
    val endY = getEndValue(start.y, end.y)

    if (onlyVertical(start, end)) {
        val x = start.x
        for (y in startY..endY) {
            if (!board.getTile(x, y).empty()) return false
        }
        return true
    } else return false
}

fun checkDiagonalUnblocked(board: Board, start: Tile, end: Tile): Boolean {
    if (start.identicalPosition(end)) return true
    val startX = getStartValue(start.x, end.x)
    val startY = getStartValue(start.y, end.y)

    val endX = getEndValue(start.x, end.x)
    val graduations = startX - endX

    if (onlyDiagonal(start, end)) {
        for (g in 0..graduations) {
            if (!board.getTile(startX + g, startY + g).empty()) return false
        }
        return true
    } else return false

}

fun onlyHorizontal(start: Tile, end: Tile): Boolean {
    return (start.y == end.y) && (start.x != end.x)
}

fun onlyVertical(start: Tile, end: Tile): Boolean {
    return (start.y == end.y) && (start.x != end.x)
}

fun onlyDiagonal(start: Tile, end: Tile): Boolean {
    return abs(start.y - end.y) == abs(start.x - end.x)
}


fun opppnents(p1: Piece, p2: Piece): Boolean {
    return p1.black != p2.black
}

fun opppnents(t1: Tile, t2: Tile): Boolean {
    if (t1.piece == null || t2.piece == null) return false
    return opppnents(t1.piece!!, t2.piece!!)
}

open class BoardPosition(
    val x: Int,
    val y: Int
) {
    fun identicalPosition(pos: BoardPosition): Boolean {
        return x == pos.x && y == pos.y
    }
}


class Tile(
    x: Int,
    y: Int,
    var piece: Piece? = null
) : BoardPosition(x, y) {

    fun empty(): Boolean {
        return piece == null
    }

    fun ifNotEmpty(block: (piece: Piece) -> Unit) {
        val piece: Piece? = piece
        piece?.let {
            block(piece)
        }
    }

    override fun toString(): String {
        return if (piece == null) "___" else piece.toString()
    }
}

