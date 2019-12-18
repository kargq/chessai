class Game(
    val board: Board = Board()
) {
    fun makeMove(startX: Int, startY: Int, endX: Int, endY: Int) {
        if (withinBounds(startX) && withinBounds(startY) && withinBounds(endX) && withinBounds(endY))
            board.getTile(startX, startY).piece?.let {
                println("Moving tile $it")
                it.makeMove(board, board.getTile(startX, startY), board.getTile(endX, endY))
            }
    }
}