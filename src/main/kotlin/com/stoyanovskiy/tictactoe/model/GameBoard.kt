package com.stoyanovskiy.tictactoe.model

import com.stoyanovskiy.tictactoe.model.Symbol.NONE

class GameBoard {
    private val matrix = Array(3) { Array(3) { NONE } }

    fun getMatrix() = matrix.map { it.clone() }.toTypedArray()

    fun applyMove(move: Move): Boolean {
        val (row, col, symbol) = move
        if (matrix[row][col] != NONE ||
            row !in 0..2 ||
            col !in 0..2
        ) return false
        matrix[row][col] = symbol
        return true
    }

    fun isFull() = matrix.all { row -> row.all { it != NONE } }

    fun checkWinner(): Symbol {
        // rows
        for (i in 0..2) {
            if (matrix[i][0] != NONE &&
                matrix[i][0] == matrix[i][1] &&
                matrix[i][1] == matrix[i][2]
            ) return matrix[i][0]
        }
        // columns
        for (i in 0..2) {
            if (matrix[0][i] != NONE &&
                matrix[0][i] == matrix[1][i] &&
                matrix[1][i] == matrix[2][i]
            ) return matrix[0][i]
        }
        // diagonals
        if (matrix[0][0] != NONE &&
            matrix[0][0] == matrix[1][1] &&
            matrix[1][1] == matrix[2][2]
        ) return matrix[0][0]
        if (matrix[0][2] != NONE &&
            matrix[0][2] == matrix[1][1] &&
            matrix[1][1] == matrix[2][0]
        ) return matrix[0][2]
        return NONE
    }

    fun reset() {
        for (row in 0..2) {
            for (col in 0..2) {
                matrix[row][col] = NONE
            }
        }
    }

    override fun toString() = matrix.joinToString("\n---------\n") { row ->
        row.joinToString(" | ") { "$it" }
    }
}
