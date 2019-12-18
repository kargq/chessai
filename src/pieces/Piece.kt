package pieces

import Board
import PromotionType
import shared.debug
import kotlin.math.*


abstract class Piece(val black: Boolean) {

    abstract fun validMove(board: Board, start: Tile, end: Tile): Boolean


    /**
     * Always call from start piece
     */
    open fun makeMove(board: Board, move: Move) {
        makeMoveDelegate(board, board.getTile(move.getStart()), board.getTile(move.getEnd()))
    }

    /**
     * Always call from start piece
     */
    private fun makeMoveDelegate(board: Board, start: Tile, end: Tile) {
        board.pawnSkip = null
        if (validMove(board, start, end)) {
            end.piece = start.piece
            start.piece = null
        } else {
            debug("NOT A VALID MOVE")
        }
    }

    fun validMove(board: Board, move: Move): Boolean {

        return validMove(board, board.getTile(move.startX, move.startY), board.getTile(move.endX, move.endY))
    }

    protected fun validStartEnd(start: Tile, end: Tile): Boolean {
        return !start.empty() && (end.empty() || opppnents(start, end))
    }

    open fun generateAllValidMoves(board: Board, start: BoardPosition): List<Move> {
        // TODO: override this at least for king and maybe pawn
        // TODO: Get moves for promotion too
        val result = mutableListOf<Move>()
        for (x in 0..7) {
            for (y in 0..7) {
                val move = Move(start, BoardPosition(x, y))
                if (validMove(board, move)) {
//                    debug("Valid move $move")
                    result.add(move)
                }
            }
        }
        return result
    }

    abstract fun getCopy(): Piece
}

class Move(
    val startX: Int,
    val startY: Int,
    val endX: Int,
    val endY: Int,
    val promotionType: PromotionType = PromotionType.QUEEN
) {
    constructor(start: BoardPosition, end: BoardPosition, promotionType: PromotionType = PromotionType.QUEEN) : this(
        start.x,
        start.y,
        end.x,
        end.y,
        promotionType
    )

    fun getStart(): BoardPosition {
        return BoardPosition(startX, startY)
    }

    fun getEnd(): BoardPosition {
        return BoardPosition(endX, endY)
    }

    override fun toString(): String {
        return "${getStart()} to ${getEnd()}"
    }
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

open class BoardPosition(
    val x: Int,
    val y: Int
) {
    fun identicalPosition(pos: BoardPosition): Boolean {
        return x == pos.x && y == pos.y
    }

    override fun toString(): String {
        return "($x, $y)"
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
        val pcStr =  if (piece == null) "___" else piece.toString()
        return "[$x,$y] $pcStr"
    }
}

