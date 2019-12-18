import pieces.Move
import shared.debug
import java.lang.Exception

class Game(
    val board: Board = Board(),
    val blackPlayer: Player = ConsolePlayer(true),
    val whitePlayer: Player = ConsolePlayer(false)
) {
    init {
        if (!blackPlayer.black) {
            throw Exception("No, it has to be black, just no")
        }
        if (whitePlayer.black) {
            throw Exception("No, it has to be white, just no")
        }


    }

    var gameState: GameState = GameState.ACTIVE
    var blackTurn = false

    fun startGameLoop() {
        while (gameState == GameState.ACTIVE) {
            nextMove()
        }
    }

    fun nextMove() {
        debug("Check checkmate and shit")
        debug(board.isKingInCheckmate(true))
        debug(board.isKingInCheckmate(false))
        when {
            board.isKingInCheckmate(true) -> {
                sendBothAMessage(board)
                sendBothAMessage("Checkmate, White wins!")
                gameState = GameState.WIN_WHITE
            }
            board.isKingInCheckmate(false) -> {
                sendBothAMessage(board)
                sendBothAMessage("Checkmate, Black wins!")
                gameState = GameState.WIN_BLACK
            }
            board.isKingInStalemate() -> {
                sendBothAMessage(board)
                sendBothAMessage("Stalemate, it's a draw!")
                gameState = GameState.STALEMATE
            }
            else -> {
                sendBothAMessage("Black player turn: $blackTurn")
                sendBothAMessage(board)
                if (!blackTurn) {
                    whitePlayer.determineNextMove(board) { move: Move ->
                        makeMove(whitePlayer, move)
                    }
                } else {
                    blackPlayer.determineNextMove(board) { move: Move ->
                        makeMove(blackPlayer, move)
                    }
                }
            }
        }
    }


    fun makeMove(player: Player, move: Move): Boolean {
        val startX = move.startX
        val startY = move.startY
        val endX = move.endX
        val endY = move.endY
        println("Move from ($startX, $startY) to ($endX, $endY)")
        if (withinBounds(startX) && withinBounds(startY) && withinBounds(endX) && withinBounds(endY))
            board.getTile(startX, startY).piece?.let {
                val startTile = board.getTile(startX, startY)
                val endTile = board.getTile(endX, endY)
                println("Moving tile $it from ")
                if (validPlayerMove(player, startX, startY, endX, endY)) {
                    it.makeMove(board, Move(startTile, endTile))
                    blackTurn = !blackTurn
                } else {
                    sendBothAMessage("Invalid move, please retry.")
                    if (blackTurn) blackPlayer.sendMessage("Invalid move by YOU blackPlayer, try again please!")
                    else whitePlayer.sendMessage("Invalid move by YOU whitePlayer, try again please!")
                }
            }
        return false
    }

    fun validPlayerMove(player: Player, startX: Int, startY: Int, endX: Int, endY: Int): Boolean {
        if (player.black == blackTurn) {
            val startTile = board.getTile(startX, startY)
            val endTile = board.getTile(endX, endY)
            startTile.piece?.let {
                if (it.black != blackTurn) return false
                if (it.validMove(board, startTile, endTile)) return true
            }
        }
        return false
    }

    fun sendBothAMessage(mgs: Any) {
        whitePlayer.sendMessage(mgs)
        blackPlayer.sendMessage(mgs)
    }

}


enum class GameState {
    ACTIVE,
    WIN_BLACK,
    WIN_WHITE,
    STALEMATE,
    TERMINATED
}