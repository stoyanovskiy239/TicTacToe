package com.stoyanovskiy.tictactoe.model

import com.stoyanovskiy.tictactoe.model.Symbol.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class GameBoardTest {

    @Test
    fun `board is empty on initialization`() {
        GameBoard().getMatrix().forEach { row -> row.forEach { assertEquals(NONE, it) } }
    }

    @Test
    fun `valid move is applied`() {
        val board = GameBoard()
        assertTrue(board.applyMove(Move(0, 0, X)))
        assertEquals(X, board.getMatrix()[0][0])
    }

    @Test
    fun `invalid move is rejected`() {
        val board = GameBoard()
        board.applyMove(Move(0, 0, X))
        assertFalse(board.applyMove(Move(0, 0, O)))
        assertEquals(X, board.getMatrix()[0][0])
    }

    @Test
    fun `detects row win`() {
        val board = GameBoard()
        board.applyMove(Move(0, 0, X))
        board.applyMove(Move(0, 1, X))
        board.applyMove(Move(0, 2, X))
        assertEquals(X, board.checkWinner())
    }

    @Test
    fun `detects column win`() {
        val board = GameBoard()
        board.applyMove(Move(0, 1, O))
        board.applyMove(Move(1, 1, O))
        board.applyMove(Move(2, 1, O))
        assertEquals(O, board.checkWinner())
    }

    @Test
    fun `detects diagonal win`() {
        val board = GameBoard()
        board.applyMove(Move(0, 0, X))
        board.applyMove(Move(1, 1, X))
        board.applyMove(Move(2, 2, X))
        assertEquals(X, board.checkWinner())
    }

    @Test
    fun `detects tie`() {
        val board = GameBoard()
        listOf(
            Move(0, 0, X), Move(0, 1, O), Move(0, 2, X),
            Move(1, 0, X), Move(1, 1, O), Move(1, 2, X),
            Move(2, 0, O), Move(2, 1, X), Move(2, 2, O)
        ).forEach(board::applyMove)
        assertTrue(board.isFull())
        assertEquals(NONE, board.checkWinner())
    }
}
