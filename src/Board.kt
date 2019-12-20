import pieces.*
import shared.*
import java.io.InputStream
import java.lang.Exception
import java.util.*

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

val blackCheckMateCache: LRUCacheMap<String, Boolean> = LRUCacheMap(100000)
val whiteCheckMateCache: LRUCacheMap<String, Boolean> = LRUCacheMap(100000)

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
    ),
    var blackPlayerTurn: Boolean = false
) {

    private val grid: List<List<Tile>>
    var kingInCheck = false
    var kingInCheckMate = false
    var stalemate = false
    private var allValidMoves: List<Move>? = null

    fun getCopy(): Board {
        val result = Board(
            getPieceArray(),
            blackPlayerTurn = blackPlayerTurn
        )
        result.kingInCheckMate = kingInCheckMate
        result.kingInCheck = kingInCheck
        result.allValidMoves = allValidMoves?.map { Move(it) }
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


    fun getAllNextValidMoves(): List<Move> {
        if (allValidMoves == null) {
            val result = mutableListOf<Move>()
            forAllPieceConstraintValidMoves(blackPlayerTurn) { move ->
                result.add(move)
            }
            allValidMoves = result
            println("\n==== \n\n\n SHOULD ONLY HAPPEN ONCE A GAME \n\n\n ===\n")
        }
        return allValidMoves!!
    }


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
        var result = ""
        result += "  0          1          2          3          4          5          6          7\n"
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
                if (!currTile.empty()) {
                    currTile.piece?.let {
                        if (it.black == black) {
                            result.add(currTile)
                        } else {
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

    fun getKingTile(black: Boolean): Tile? {
        forAllPieceTiles(black) { tile, piece ->
            if (piece is King) {
                return tile
            }
        }
        return null
    }

    fun checkmatePlayer() {
        if(kingInCheckMate) {

        }
    }
    inline fun forAllPieceConstraintValidMoves(black: Boolean, callback: (move: Move) -> Unit) {
        forAllPieceTiles(black) { tile, piece ->
            piece.forAllValidMovesFromPiece(this, tile) { move ->
                callback(move)
            }
        }
    }

    fun isKingInStalemateDelegate(black: Boolean): Boolean {
        val kingTile = getKingTile(black)
        if (kingTile != null) {
            if (!kingInCheck) {
                // It's not currently in check, but everything else should be in check around it.
                var numValidMoves = 0
                forAllPieceConstraintValidMoves(black) { move ->
                    numValidMoves++
                    if (!moveLeadsToOwnCheck(move)) {
                        // If all moves lead to king being in a check, it's a stalemate.
                        return false
                    }
                }
                return numValidMoves > 0
            } else {
                return false
            }
        } else return false
    }



    fun executeMove(move: Move): Boolean {
        if (!kingInCheckMate) {
            getTile(move.getStart()).piece?.let {
                if (it.checkPieceMoveConstraints(this, move)) {
                    // can't do a move that leads to a check.
                    // if in check, cannot do any move that does not change this.
                    // Both should be handled by saying that the next move cannot put current king in check.
                    if (!moveLeadsToOwnCheck(move)) {
                        it.movePiece(this, move)
                        // Check if this move put opponent king in check.
                        kingInCheck = false
                        kingInCheckMate = false
                        forAllPieceTiles(blackPlayerTurn) { tile, piece ->
                            getKingTile(!blackPlayerTurn)?.let { opponentKingTile ->
                                if (piece.checkPieceMoveConstraints(this, Move(tile, opponentKingTile))) {
                                    // Can move one of own pieces to opponent king, put opponent in check.
//                                println("King in check because ${Move(tile, opponentKingTile)}")
                                    kingInCheck = true
                                    return@forAllPieceTiles
                                }
                            }
                        }
                        blackPlayerTurn = !blackPlayerTurn

                        // Generate all the next possible board states. Only add ones that don't lead to own check.
                        val newAllValidMoves = mutableListOf<Move>()
                        forAllPieceTiles(blackPlayerTurn) { tile, piece ->
                            piece.forAllValidMovesFromPiece(this, tile) { move ->
                                // Generate all possible next states, if any of them lead to a 'not own check', it's not a checkmate.
                                // Also add the ones that don't have a 'own heck' to preserve all next possible board states.
//                                println("Checking move $move")
                                val boardCopy = getCopy()
                                if (!boardCopy.moveLeadsToOwnCheck(move)) {
                                    // Not a Checkmate as soon as it gets here.
//                                    newAllBoardStates.add(boardCopy)
//                                    println("Valid, not checkmate.")
                                    newAllValidMoves.add(move)
                                }
                            }
                        }

//                        allBoardStates = newAllBoardStates
                        allValidMoves = newAllValidMoves

                        if (kingInCheck) {
                            // Check if new player is in checkmate
                            // If there's plausible next states, it's not a checkmate.
//                            println("Will be setting checkmate in this step, is king in check? $kingInCheck. Moves size: ${newAllValidMoves.size}")
                            kingInCheckMate = (newAllValidMoves.size == 0)
                        } else {
                            // Check stalemate
                            stalemate =
                                isKingInStalemateDelegate(!blackPlayerTurn) || isKingInStalemateDelegate(blackPlayerTurn)
                        }
                        return true
                    } else {
                        return false
                    }
                }
            }
        }
        return false

    }

    fun moveLeadsToOwnCheck(move: Move): Boolean {
        val boardCopy = getCopy()
        // Do this move without any checks on a copy of this board.
        val copyStart = boardCopy.getTile(move.getStart())
        copyStart.piece?.let {
            it.movePiece(boardCopy, move)
            val copyEndKingTile = boardCopy.getKingTile(blackPlayerTurn)!!
            forAllPieceTiles(!blackPlayerTurn) { tile, piece ->
                if (piece.checkPieceMoveConstraints(boardCopy, Move(tile, copyEndKingTile))) {
                    // Can move opponent piece to king following this move, check possible.
                    return true
                }
            }
        }
        return false
    }

}

fun withinBounds(xory: Int): Boolean {
    return xory in (0..7)
}

fun withinBounds(vararg xory: Int): Boolean {
    for (arg in xory) {
        if (!withinBounds(arg)) return false
    }
    return true
}


fun constructBoardFromInput(inp: InputStream): Board {

    // Parse pieces
    val input: Scanner = Scanner(inp)
    val board = Board()

    println(board)
    var move = scanMove(input)

    while (move != null) {
        board.executeMove(move)
        println(board)
        move = scanMove(input)
    }
    println(board)
    println("Done!")
    return board
}

fun scanMove(inp: Scanner): Move? {
    return try {
        println("Enter start position. (Can just be in format startX, startY, endX, endY, e.g. 5 6 5 5). ")
        println("Enter DONE when done.")
        println("Start X: ")
        val sx = inp.nextInt()
        println("Start Y: ")
        val sy = inp.nextInt()
        println("End X: ")
        val ex = inp.nextInt()
        println("End Y: ")
        val ey = inp.nextInt()

        return if (withinBounds(sx, sy, ex, ey)) {
            Move(sx, sy, ex, ey)
        } else null
    } catch (e: Exception) {
        null
    }
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