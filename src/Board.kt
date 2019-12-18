import pieces.*

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
class Board {
    // Not null if pawn skipped a position the last position on the board, stores the position it skipped from
    var pawnSkip: BoardPosition? = null


    private val grid: List<List<Tile>>

    private val startPos: List<List<Piece?>> = listOf(
        listOf(
            Rook(true),
            Knight(true),
            Bishop(true),
            Queen(true),
            King(true),
            Queen(true),
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
            Queen(),
            Knight(),
            Rook()
        )
    )

    init {
        val tempGrid: MutableList<List<Tile>> = mutableListOf()
        for ((rIndex, row) in startPos.withIndex()) {
            val currRow = mutableListOf<Tile>()
            for ((cIndex, col) in row.withIndex()) {
                currRow.add(Tile(cIndex, rIndex, col))
            }
            tempGrid.add(currRow)
        }
        grid = tempGrid
    }

    fun getTile(x: Int, y: Int): Tile {
        // The y x flip is intentional
        return grid[y][x]
    }

    fun getTile(p: BoardPosition): Tile {
        return getTile(p.x, p.y)
    }

    fun setPiece(x: Int, y: Int, pc: Piece) {
        grid[x][y].piece = pc
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
}

fun withinBounds(xory: Int): Boolean {
    return xory in (0..7)
}

