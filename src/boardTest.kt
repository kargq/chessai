import java.io.FileInputStream

fun main() {
    val board = Board(FileInputStream("tests/inp1"))
    print(board)
    println(board.isKingInCheck(false))
    println(board.isKingOnCheckmate(false))
    println("======")
    val board2 = Board(FileInputStream("tests/checktest1"))
    println(board2)
    println(board2.isKingInCheck(false))
    println("======")
    val board3 = Board(FileInputStream("tests/stalemate"))
    println(board3)
    println(board3.isKingOnStalemate(true))
}