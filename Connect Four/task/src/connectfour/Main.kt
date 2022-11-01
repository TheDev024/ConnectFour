package connectfour

import java.util.*

val scanner: Scanner = Scanner(System.`in`)

enum class Color { YELLOW, RED }

const val leftBottom = '\u255A'
const val rightBottom = '\u255D'
const val midBottom = '\u2569'
const val bottom = '\u2550'
const val side = '\u2551'


class ConnectFour {

    private val rows: Int
    private val columns: Int
    private val player1: String
    private val player2: String
    private val color1: Color = Color.YELLOW
    private val color2: Color = Color.RED
    private var currentPlayer: String
    private var currentColor: Color
    private val gameBoard: List<MutableList<Color?>>

    init {
        println("Connect Four")
        println("First player's name:")
        val first = scanner.nextLine()
        println("Second player's name:")
        val second = scanner.nextLine()
        this.player1 = first
        this.player2 = second
        val (row, col) = getDimensions()
        this.rows = row
        this.columns = col
        this.gameBoard = List(columns) { mutableListOf() }
        this.currentPlayer = player1
        this.currentColor = color1
    }

    fun startGame() {
        println("${this.player1} VS ${this.player2}\n${this.rows} X ${this.columns} board")
        drawBoard()
        playGame()
    }

    private fun playGame() {
        game@ while (true) {
            val input: Int = getInput()
            if (input == 0) break@game
            gameBoard[input - 1].add(currentColor)
            drawBoard()
            if (playerWins()) {
                println("Player $currentPlayer won")
                break@game
            }
            if (isDraw()) {
                println("It is a draw")
                break@game
            }
            changeTurn()
        }
        println("Game over!")
    }

    private fun isDraw(): Boolean {
        repeat(columns) { col -> if (gameBoard[col].size < rows) return false }
        return true
    }

    private fun playerWins(): Boolean {
        val currentState = List(columns) {
            MutableList<Color?>(rows) { null }
        }

        repeat(columns) { col ->
            val column = gameBoard[col]
            repeat(column.size) { row ->
                currentState[col][row] = column[row]
            }
        }

        repeat(columns) { col ->
            val column = gameBoard[col]
            val colSize = column.lastIndex
            repeat(column.size) { row ->
                if (currentState[col][row] == currentColor) {
                    if (row <= colSize - 3)
                        for (i in 1..3) {
                            if (currentState[col][row + i] != currentColor) break
                            if (i == 3) return true
                        }

                    if (col < columns - 3)
                        for (i in 1..3) {
                            if (currentState[col + i][row] != currentColor) break
                            if (i == 3) return true
                        }

                    if (col > 2 && row < rows - 3)
                        for (i in 1..3) {
                            if (currentState[col - i][row + i] != currentColor) break
                            if (i == 3) return true
                        }

                    if (col < columns - 3 && row < rows - 3)
                        for (i in 1..3) {
                            if (currentState[col + i][row + i] != currentColor) break
                            if (i == 3) return true
                        }
                }
            }
        }

        return false
    }

    private fun changeTurn() {
        currentPlayer = if (currentPlayer == player1) player2 else player1
        currentColor = if (currentColor == color1) color2 else color1
    }

    private fun getInput(): Int {
        println("$currentPlayer's turn:")
        val input = scanner.nextLine()
        if (input == "end") return 0
        var column = input.toIntOrNull()
        if (column == null) {
            println("Incorrect column number")
            column = getInput()
        } else if (column !in 1..columns) {
            println("The column number is out of range (1 - ${columns})")
            column = getInput()
        } else if (gameBoard[column - 1].size == rows) {
            println("Column $column is full")
            column = getInput()
        }
        return column
    }

    private fun drawBoard() {
        print(" ")
        repeat(columns) { print("${it + 1} ") }
        repeat(rows + 1) { row ->
            println()
            print(if (row == rows) leftBottom else side)
            val index = rows - row - 1
            repeat(columns) { col ->
                val pattern = if (gameBoard[col].size > index && index != -1) {
                    if (gameBoard[col][index] == Color.YELLOW) 'o'
                    else '*'
                } else ' '
                if (row == rows) print(bottom.toString() + if (col == columns - 1) rightBottom else midBottom)
                else print(pattern.toString() + side)
            }
        }
        println()
    }

    private fun getDimensions(): Pair<Int, Int> {
        println("Set the board dimensions (Rows x Columns)\nPress Enter for default (6 x 7)")
        try {
            val input = scanner.nextLine()
            if (input == "") return Pair(6, 7)
            val dimens = input
                .replace(Regex("\\s*"), "")
                .split(Regex("x", RegexOption.IGNORE_CASE))
            var row = dimens[0].toInt()
            var col = dimens[1].toInt()
            if (row !in 5..9) {
                println("Board rows should be from 5 to 9")
                val (x, y) = getDimensions()
                row = x
                col = y
            } else if (col !in 5..9) {
                println("Board columns should be from 5 to 9")
                val (x, y) = getDimensions()
                row = x
                col = y
            }
            return Pair(row, col)
        } catch (e: Exception) {
            println("Invalid input")
            val (row, col) = getDimensions()
            return Pair(row, col)
        }
    }
}

fun main() {
    val game = ConnectFour()
    game.startGame()
}