fun main() {
    val game = Game(board = Board(System.`in`), blackTurn = true)
    game.startGameLoop()
}