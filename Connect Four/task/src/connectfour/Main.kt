package connectfour

import java.util.*

val scanner: Scanner = Scanner(System.`in`)

enum class Color { YELLOW, RED }

data class Player(
    val name: String,
    val color: Color,
    var score: Int
)

// Box drawings unicodes
const val leftBottom = '\u255A'
const val rightBottom = '\u255D'
const val midBottom = '\u2569'
const val bottom = '\u2550'
const val side = '\u2551'


class ConnectFour {

    private val rows: Int // number of rows in the board
    private val columns: Int // number of columns in the board
    private val gameBoard: List<MutableList<Color?>>

    private val player1: Player
    private val player2: Player
    private var currentPlayer: Player

    init {
        println("Connect Four")
        println("First player's name:")
        val first = scanner.nextLine()
        println("Second player's name:")
        val second = scanner.nextLine()
        this.player1 = Player(first, Color.YELLOW, 0)
        this.player2 = Player(second, Color.RED, 0)
        val (row, col) = getDimensions()
        this.rows = row
        this.columns = col
        this.gameBoard = List(columns) { mutableListOf() }
        this.currentPlayer = player1
    }

    fun startGame() {
        val games: Int = multipleGames()
        println("${this.player1.name} VS ${this.player2.name}\n${this.rows} X ${this.columns} board")
        if (games == 1) {
            println("Single game")
            drawBoard()
            playGame()
        } else {
            println("Total $games games")
            repeat(games) {
                println("Game #${it + 1}")
                drawBoard()
                playGame()
                println("Score\n" +
                        "${player1.name}: ${player1.score} ${player2.name}: ${player2.score}")
                currentPlayer = if (it % 2 == 0) player2 else player1
                clearBoard()
            }
        }
        println("Game over!")
    }

    private fun clearBoard() {
        repeat(columns) { gameBoard[it].clear() }
    }

    private fun multipleGames(): Int {
        println(
            "Do you want to play single or multiple games?\n" +
                    "For a single game, input 1 or press Enter\n" +
                    "Input a number of games:"
        )
        val count = scanner.nextLine()
        if (count == "") return 1
        var n = count.toIntOrNull()
        if (n == null || n < 1) {
            println("Invalid Input")
            n = multipleGames()
        }
        return n
    }

    private fun playGame() {
        game@ while (true) {
            val input: Int = getInput()
            if (input == 0) break@game
            gameBoard[input - 1].add(currentPlayer.color)
            drawBoard()
            if (playerWins()) {
                println("Player ${currentPlayer.name} won")
                currentPlayer.score += 2
                break@game
            }
            if (isDraw()) {
                println("It is a draw")
                player1.score += 1
                player2.score += 1
                break@game
            }
            changeTurn()
        }
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
                if (currentState[col][row] == currentPlayer.color) {
                    if (row <= colSize - 3)
                        for (i in 1..3) {
                            if (currentState[col][row + i] != currentPlayer.color) break
                            if (i == 3) return true
                        }

                    if (col < columns - 3)
                        for (i in 1..3) {
                            if (currentState[col + i][row] != currentPlayer.color) break
                            if (i == 3) return true
                        }

                    if (col > 2 && row < rows - 3)
                        for (i in 1..3) {
                            if (currentState[col - i][row + i] != currentPlayer.color) break
                            if (i == 3) return true
                        }

                    if (col < columns - 3 && row < rows - 3)
                        for (i in 1..3) {
                            if (currentState[col + i][row + i] != currentPlayer.color) break
                            if (i == 3) return true
                        }
                }
            }
        }

        return false
    }

    private fun changeTurn() {
        currentPlayer = if (currentPlayer == player1) player2 else player1
    }

    private fun getInput(): Int {
        println("${currentPlayer.name}'s turn:")
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