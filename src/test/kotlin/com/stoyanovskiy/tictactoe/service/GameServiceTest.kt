package com.stoyanovskiy.tictactoe.service

import com.stoyanovskiy.tictactoe.client.OpponentClient
import com.stoyanovskiy.tictactoe.model.GameStatus.*
import com.stoyanovskiy.tictactoe.model.Move
import com.stoyanovskiy.tictactoe.model.Symbol.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

class GameServiceTest {
    private lateinit var gameService: GameService
    private lateinit var opponentClient: OpponentClient

    @BeforeEach
    fun setup() {
        opponentClient = mock(OpponentClient::class.java)
        gameService = GameService(opponentClient)
        gameService.setLocalPlayer(X)
    }

    @Test
    fun `game starts with player X`() {
        val state = gameService.getGameState()
        assertEquals(X, state.turn)
        assertEquals(IN_PROGRESS, state.status)
    }

    @Test
    fun `valid move updates the game state`() {
        assertTrue(gameService.applyMove(Move(0, 0, X)))
        val state = gameService.getGameState()
        assertEquals(O, state.turn)
        assertEquals(X, state.boardMatrix[0][0])
    }

    @Test
    fun `invalid move does not change the game state`() {
        val move = Move(0, 0, X)
        gameService.applyMove(move)
        assertFalse(gameService.applyMove(move))
        assertEquals(X, gameService.getGameState().boardMatrix[0][0])
    }

    @Test
    fun `game ends when there is a winner`() {
        listOf(
            Move(0, 0, X), Move(0, 1, O),
            Move(1, 0, X), Move(1, 1, O),
            Move(2, 0, X) // winning move
        ).forEach(gameService::applyMove)
        val state = gameService.getGameState()
        assertEquals(WIN, state.status)
        assertEquals(X, state.winner)
    }

    @Test
    fun `game ends with tie when board is full`() {
        listOf(
            Move(0, 0, X), Move(0, 1, O), Move(0, 2, X),
            Move(1, 0, O), Move(1, 1, X), Move(1, 2, O),
            Move(2, 0, O), Move(2, 1, X), Move(2, 2, O)
        ).forEach(gameService::applyMove)
        val state = gameService.getGameState()
        assertEquals(TIE, state.status)
        assertEquals(NONE, state.winner)
    }

    @Test
    fun `restartGame should clear game state and notify opponent to reset state`() {
        gameService.applyMove(Move(0, 0, X))
        gameService.restartGame()
        val state = gameService.getGameState()
        assertEquals(IN_PROGRESS, state.status)
        assertEquals(X, state.turn)
        assertEquals(NONE, state.boardMatrix[0][0])
        verify(opponentClient).resetGame()
    }
}
