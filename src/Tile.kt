import pieces.Piece

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
        val pcStr = if (piece == null) "___" else piece.toString()
        return "[$x,$y] $pcStr"
    }
}
