package pieces

import Board
import BoardPosition
import Move
import Tile
import kotlin.math.*


abstract class Piece(val black: Boolean) {

    var hasMoved = false

    abstract fun checkPieceMoveConstraints(board: Board, move: Move): Boolean


    /**
     * Always call from start piece. Does not check for validity, just executes the move.
     */
    open fun movePiece(board: Board, move: Move) {
        val start = board.getTile(move.getStart())
        val end = board.getTile(move.getEnd())
        end.piece = start.piece
        start.piece = null
    }

    protected fun validStartEnd(start: Tile, end: Tile): Boolean {
        return !start.empty() && (end.empty() || opppnents(start, end))
    }

    fun generateAllValidMoves(board: Board, start: BoardPosition): List<Move> {
        val result = mutableListOf<Move>()
        forAllValidMovesFromPiece(board, start) {
            result.add(it)
        }
        return result
    }

    inline fun forAllValidMovesFromPiece(board: Board, start: BoardPosition, callback: (Move) -> Unit){
        val result = mutableListOf<Move>()
        for (x in 0..7) {
            for (y in 0..7) {
                val move = Move(start, BoardPosition(x, y))
                if (checkPieceMoveConstraints(board, move)) {
                    callback(move)
                }
            }
        }
    }

    fun getCopy(): Piece {
        val result = copyPiece()
        result.hasMoved = hasMoved
        return result
    }

    protected abstract fun copyPiece(): Piece
}


fun getStartValue(from: Int, to: Int): Int {
    return if (from < to) from + 1 else to + 1
}

fun getEndValue(from: Int, to: Int): Int {
    return if (to > from) to - 1 else from - 1
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
//    debug("checking diagonal")

    if (start.identicalPosition(end)) return true

    val xDir = (end.x - start.x).sign
    val yDir = (end.y - start.y).sign

    val startX = start.x + xDir
    val startY = start.y + yDir

    val graduations = abs(end.x - start.x) - 1

    if (onlyDiagonal(start, end)) {
        for (g in 0 until graduations) {
            if (!board.getTile(startX + g * xDir, startY + g * yDir).empty()) return false
        }
        return true
    } else return false

}

fun onlyHorizontal(start: Tile, end: Tile): Boolean {
    return (start.y == end.y) && (start.x != end.x)
}

fun onlyVertical(start: Tile, end: Tile): Boolean {
    return (start.x == end.x) && (start.y != end.y)
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

