import pieces.Move
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

            println("End X")
            val endX = input.nextInt()
            println("End Y")
            val endY = input.nextInt()

            println("Sure ok")

            onDetermined(Move(startX, startY, endX, endY))

            //TODO: Check if promotion will be available for this move, determine type before calling.
        } catch (e: Exception) {
            onDetermined(Move(-1, -1, -1, -1))
        }
    }
}
