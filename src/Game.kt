import shared.debug
import shared.getColorText
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

    fun startGameLoop() {
        while (gameState == GameState.ACTIVE) {
            nextMove()
        }
    }

    fun nextMove() {
        sendBothAMessage("=============================")

        when {
            board.kingInCheckMate && board.blackPlayerTurn -> {
                sendBothAMessage(board)
                sendBothAMessage("Checkmate, White wins!")
                gameState = GameState.WIN_WHITE
            }
            board.kingInCheckMate && !board.blackPlayerTurn -> {
                sendBothAMessage(board)
                sendBothAMessage("Checkmate, Black wins!")
                gameState = GameState.WIN_BLACK
            }
            board.stalemate -> {
                sendBothAMessage(board)
                sendBothAMessage("Stalemate, it's a draw!")
                gameState = GameState.STALEMATE
            }
            else -> {
                if(board.kingInCheck) sendBothAMessage("${getColorText(board.blackPlayerTurn)} King in check")
                sendBothAMessage(board)
                sendBothAMessage("Turn: ${getColorText(board.blackPlayerTurn)}")
                if (!board.blackPlayerTurn) {
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


    fun makeMove(player: Player, move: Move) {
        val startX = move.startX
        val startY = move.startY
        val endX = move.endX
        val endY = move.endY
        sendBothAMessage("Move $move")
        if (withinBounds(startX) && withinBounds(startY) && withinBounds(endX) && withinBounds(endY)) {
            val startTile = board.getTile(startX, startY)
            val endTile = board.getTile(endX, endY)
            sendBothAMessage("${getColorText(board.blackPlayerTurn)} $startTile $move")
            if (!board.executeMove(move)) {
                sendBothAMessage("Invalid move, please retry.")
                if (board.blackPlayerTurn) blackPlayer.sendMessage("Invalid move by blackPlayer, try again!")
                else whitePlayer.sendMessage("Invalid move by whitePlayer, try again!")
            }
        }
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