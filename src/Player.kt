import pieces.BoardPosition
import pieces.Move
import pieces.Pawn
import shared.getColorText
import java.lang.Exception
import java.util.*

enum class PromotionType {
    QUEEN, ROOK, BISHOP, KNIGHT
}

abstract class Player(val black: Boolean) {
    abstract fun determineNextMove(
        board: Board,
        onDetermined: (Move) -> Unit
    )

    open fun sendMessage(umm: Any) {
        println("To ${getColorText(black)} \n $umm")
    }

    override fun toString(): String {
        return "${getColorText(black)} ${super.toString()}"
    }
}


class ConsolePlayer(black: Boolean) : Player(black) {
    val input = Scanner(System.`in`)

    override fun determineNextMove(
        board: Board,
        onDetermined: (Move) -> Unit
    ) {
        try {
            println("Start X")
            val startX = input.nextInt()
            println("Start Y")
            val startY = input.nextInt()
            val startPos = BoardPosition(startX, startY)
            println("End X")
            val endX = input.nextInt()
            println("End Y")
            val endY = input.nextInt()
            val endPos = BoardPosition(endX, endY)

            var promotionType = PromotionType.QUEEN

            board.getTile(startPos).piece?.let {
                if (it is Pawn) {
                    if (it.validMove(board, Move(startPos, endPos))) {
                        if (it.black && startPos.y == 6 || !it.black && startPos.y == 1) {
                            println("Promotion might be possible in the next move, What do you want to promote your piece to?")
                            println("1. Queen, 2. Knight 3. Rook 4. Bishop")
                            when (input.nextInt()) {
                                1 -> promotionType = PromotionType.QUEEN
                                2 -> promotionType = PromotionType.KNIGHT
                                3 -> promotionType = PromotionType.ROOK
                                4 -> promotionType = PromotionType.BISHOP
                            }
                        }
                    }
                }
            }

            onDetermined(Move(startPos, endPos, promotionType))
        } catch (e: Exception) {
            onDetermined(Move(-1, -1, -1, -1))
        }
    }

    override fun sendMessage(umm: Any) {
        println("GAME:/ $umm")
    }
}
