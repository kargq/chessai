import pieces.*
import shared.debug
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.StringReader
import java.util.*
import kotlin.math.*

/*

8A B C D E F G H
7
6
5
4
3
2
1

Corresponds to

y x->
0 1 2 3 4 5 6 7
1
2
3
4
5
6
7

 */
class Board(
    private val startArray: List<List<Piece?>> = listOf(
        listOf(
            Rook(true),
            Knight(true),
            Bishop(true),
            Queen(true),
            King(true),
            Bishop(true),
            Knight(true),
            Rook(true)
        ),
        listOf(
            Pawn(true),
            Pawn(true),
            Pawn(true),
            Pawn(true),
            Pawn(true),
            Pawn(true),
            Pawn(true),
            Pawn(true)
        ),
        listOf(null, null, null, null, null, null, null, null),
        listOf(null, null, null, null, null, null, null, null),
        listOf(null, null, null, null, null, null, null, null),
        listOf(null, null, null, null, null, null, null, null),
        listOf(
            Pawn(),
            Pawn(),
            Pawn(),
            Pawn(),
            Pawn(),
            Pawn(),
            Pawn(),
            Pawn()
        ),
        listOf(
            Rook(),
            Knight(),
            Bishop(),
            Queen(),
            King(),
            Bishop(),
            Knight(),
            Rook()
        )
    )
) {
    // Not null if pawn skipped a position the last position on the board, stores the position it skipped from
    var pawnSkip: BoardPosition? = null


    private val grid: List<List<Tile>>

    fun getParseableStateString(): String {
        var result = ""
        for (x in 0..7) {
            for (y in 0..7) {
                result += "${getPieceString(getTile(x, y).piece)} "
            }
        }
        return result
    }

    fun getPieceString(piece: Piece?): String {
        return if (piece == null) "x" else piece.toString()
    }

    fun getCopy(): Board {
        val result = Board(getPieceArray())
        result.pawnSkip = pawnSkip?.let { BoardPosition(it.x, it.y) }
        return result
    }

    init {
        val tempGrid: MutableList<List<Tile>> = mutableListOf()
        for ((rIndex, row) in startArray.withIndex()) {
            val currRow = mutableListOf<Tile>()
            for ((cIndex, col) in row.withIndex()) {
                currRow.add(Tile(cIndex, rIndex, col))
            }
            tempGrid.add(currRow)
        }
        grid = tempGrid
    }

    constructor(inp: InputStream) : this(parseInput(inp))
    constructor(inp: String) : this(parseInput(stringInputStream(inp)))

    fun getPieceArray(): List<List<Piece?>> {
        return grid.map { it.map { tile -> tile.piece?.getCopy() } }
    }

    fun getTile(x: Int, y: Int): Tile {
        // The y x flip is intentional
        return grid[y][x]
    }

    fun getTile(p: BoardPosition): Tile {
        return getTile(p.x, p.y)
    }

    fun setPiece(x: Int, y: Int, pc: Piece?) {
        grid[y][x].piece = pc
    }

    fun setPiece(p: BoardPosition, pc: Piece?) {
//        debug("set ${p.x} ${p.y} to $pc")
        setPiece(p.x, p.y, pc)
    }

    override fun toString(): String {
        var result = "${super.toString()}\n"
        result += "  0   1   2   3   4   5   6   7\n"
        for (y in 0..7) {
            result += "$y "
            for (x in 0..7) {
                result += "${getTile(x, y)} "
            }
            result += "\n"
        }
        return result
    }

    fun getAllPieceTiles(black: Boolean): List<Tile> {
        val result = mutableListOf<Tile>()
        for (x in 0..7) {
            for (y in 0..7) {
                val currTile = getTile(x, y)
//                debug("Get tile $currTile")
                if (!currTile.empty()) {
                    currTile.piece?.let {
                        if (it.black == black) {
                            result.add(currTile)
                        } else {
//                            debug("Don't add tile $currTile, not ${getColorText(black)}")
                        }
                    }
                }
            }
        }
        return result
    }

    inline fun forAllPieceTiles(black: Boolean, callback: (tile: Tile, piece: Piece) -> Unit) {
        forAllTiles { currTile ->
            if (!currTile.empty()) {
                currTile.piece?.let {
                    if (it.black == black)
                        callback(currTile, it)
                }
            }
        }
    }


    inline fun forAllTiles(callback: (tile: Tile) -> Unit) {
        for (x in 0..7) {
            for (y in 0..7) {
                val currTile = getTile(x, y)
                callback(currTile)
            }
        }
    }

    fun getKingTile(black: Boolean): Tile {
        forAllPieceTiles(black) { tile, piece ->
            if (piece is King) {
                return tile
            }
        }
        return null!!
    }

    fun isKingOnCheckmate(black: Boolean): Boolean {
//        val king = getKingTile(black)
//        king.piece?.let { piece ->
//            piece.generateAllValidMoves(this, king).forEach {
//                if (!isPositionCheck(it.getEnd(), black)) return false
//            }
//            // Even if one position avoids check, it's fine
//        }
//        val king = getKingTile(black)
//        if (isPositionCheck(king, black)) {
//            for (x in max(king.x - 1, 0)..min(king.x + 1, 7)) {
//                for (y in max(king.x - 1, 0)..min(king.x + 1, 7)) {
//                    if (x != king.x && y != king.y) {
//                        king.piece?.let {
//                            if (it.validMove(this@Board, king, getTile(x, y))) {
//                                // Even if one valid position avoids check, it's fine
//                                if (!isPositionCheck(BoardPosition(x, y), black)) return false
//                            }
//                        }
//                    }
//                }
//            }
//            return true
//        } else return false'
        val states = generateAllBoardStates(black)
//        debug(states)
        for ((index, state) in states.withIndex()) {
//            debug("Checking state $index")
            if (!state.isKingInCheck(black)) {
                debug("${getColorText(black)} KING is not on check, board state $state \n at $index for \n ${this}")
                return false
            }
        }
//        debug("${getColorText(black)} KING is on check \n ${this}")
        return true
    }

    fun getColorText(black: Boolean): String {
        return if (black) "Black" else "White"
    }


    // TODO: Some sort of caching for these results
    fun isKingOnStalemate(black: Boolean): Boolean {
        val kingTile = getKingTile(black)
        if (!isPositionCheck(kingTile, black)) {
            // It's not currently in check, but everything else should be a check around it.
            var numValidMoves = 0
//            debug("Stalemate king at $kingTile")
            for (x in max(kingTile.x - 1, 0)..min(kingTile.x + 1, 7)) {
                for (y in max(kingTile.y - 1, 0)..min(kingTile.y + 1, 7)) {
                    if (x != kingTile.x && y != kingTile.y) {
                        val currPos = BoardPosition(x, y)
//                        debug("Stalemate $currPos")
                        kingTile.piece?.let {
                            if (it.validMove(this@Board, kingTile, getTile(x, y))) {
                                numValidMoves++
                                // Even if one valid position avoids check, it's fine
                                if (!isPositionCheck(currPos, black)) return false
                            }
                        }
                    }
                }
            }
            return numValidMoves > 0
        } else {
            return false
        }
    }

    fun executeMove(move: Move) {
        getTile(move.getStart()).piece?.let {
            it.makeMove(this, move)
        }
    }

    fun isKingOnStalemate(): Boolean {
        return isKingOnStalemate(true) || isKingOnStalemate(false)
    }

    fun isPositionCheck(pos: BoardPosition, checkAgainstBlack: Boolean): Boolean {
        // Only works when pos is empty or pos is opponent .
        // Could pass in new board if this does not work.
        val startTiles = getAllPieceTiles(!checkAgainstBlack)
        val endTile = getTile(pos)
//        debug("Start tiles being checked to have a move towards ${endTile}, checking if ${getKingTile(checkAgainstBlack)} is under check. \n $startTiles")
        // Check if any of the start tiles has a valid move to endTile
        for (startTile in startTiles) {
            startTile.piece?.let { piece ->
                //                debug("Check valid move from $startTile to $endTile")
                if (piece.validMove(this, startTile, endTile)) return true
//                debug("No valid move from $startTile to $endTile")
            }
        }
        return false
    }

    fun isKingInCheck(black: Boolean): Boolean {
//        debug("Check if ${getColorText(black)} king is in check")
        val king = getKingTile(black)
        val result = isPositionCheck(king, black)
        if (!result) {
//            debug("King is not in check for $this")
        }
        return result
    }

    fun generateAllBoardStates(black: Boolean): List<Board> {
        val result = mutableListOf<Board>()
        forAllPieceTiles(black) { tile, piece ->
            piece.generateAllValidMoves(this, tile).forEach { move ->
                val newBoard = getCopy()
                newBoard.executeMove(move)
                result.add(newBoard)
            }
        }
        return result
    }
}

fun withinBounds(xory: Int): Boolean {
    return xory in (0..7)
}


fun parseInput(inp: InputStream): List<List<Piece?>> {
    val input: Scanner = Scanner(inp)
    val result: MutableList<List<Piece?>> = mutableListOf()
    for (rIndex in 0..7) {
        val currRow = mutableListOf<Piece?>()
        for (cIndex in 0..7) {
            currRow.add(stringToPiece(input.next()))
        }
        result.add(currRow)
    }
    return result
}

fun stringInputStream(str: String): InputStream {
    return str.byteInputStream()
}

fun stringToPiece(str: String): Piece? {
    return when (str.trim().toUpperCase()) {
        "KIW" -> King()
        "QIW" -> Queen()
        "BIW" -> Bishop()
        "KNW" -> Knight()
        "RKW" -> Rook()
        "PNW" -> Pawn()
        "KIB" -> King(true)
        "QIB" -> Queen(true)
        "BIB" -> Bishop(true)
        "KNB" -> Knight(true)
        "RKB" -> Rook(true)
        "PNB" -> Pawn(true)
        else -> null
    }
}