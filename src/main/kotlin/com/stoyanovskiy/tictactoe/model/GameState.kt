package com.stoyanovskiy.tictactoe.model

data class GameState(
    val boardMatrix: Array<Array<Symbol>>,
    val status: GameStatus,
    val turn: Symbol,
    val winner: Symbol
) {

    override fun equals(other: Any?) = when {
        this === other -> true
        other !is GameState -> false
        else -> boardMatrix.contentDeepEquals(other.boardMatrix) &&
                status == other.status &&
                turn == other.turn &&
                winner == other.winner
    }

    override fun hashCode(): Int {
        var result = boardMatrix.contentDeepHashCode()
        result = 31 * result + status.hashCode()
        result = 31 * result + turn.hashCode()
        result = 31 * result + winner.hashCode()
        return result
    }
}
