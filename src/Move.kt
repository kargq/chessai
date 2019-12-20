import pieces.*


enum class PromotionType {
    QUEEN, ROOK, BISHOP, KNIGHT
}

open class Move(
    val startX: Int,
    val startY: Int,
    val endX: Int,
    val endY: Int,
    val promotionType: PromotionType = PromotionType.QUEEN
) {
    constructor(start: BoardPosition, end: BoardPosition, promotionType: PromotionType = PromotionType.QUEEN) : this(
        start.x,
        start.y,
        end.x,
        end.y,
        promotionType
    )

    constructor(move: Move) : this(
        move.startX,
        move.startY,
        move.endX,
        move.endY,
        move.promotionType
    )

    fun getNewPromotionPiece(black: Boolean): Piece {
        return when (promotionType) {
            PromotionType.QUEEN -> Queen(black)
            PromotionType.ROOK -> Rook(black)
            PromotionType.KNIGHT -> Knight(black)
            PromotionType.BISHOP -> Bishop(black)
        }
    }

    fun getStart(): BoardPosition {
        return BoardPosition(startX, startY)
    }

    fun getEnd(): BoardPosition {
        return BoardPosition(endX, endY)
    }

    override fun toString(): String {
        return "${getStart()} to ${getEnd()}"
    }
}