package com.stoyanovskiy.tictactoe.sync

import com.stoyanovskiy.tictactoe.client.OpponentClient
import com.stoyanovskiy.tictactoe.model.*
import com.stoyanovskiy.tictactoe.model.GameStatus.IN_PROGRESS
import com.stoyanovskiy.tictactoe.model.Symbol.*
import com.stoyanovskiy.tictactoe.service.GameService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ConsistencyServiceTest {
    private lateinit var gameService: GameService
    private lateinit var opponentClient: OpponentClient
    private lateinit var consistencyService: ConsistencyService

    private val sharedState = GameState(GameBoard().getMatrix(), IN_PROGRESS, X, NONE)

    @BeforeEach
    fun setup() {
        gameService = mock(GameService::class.java)
        opponentClient = mock(OpponentClient::class.java)
        consistencyService = ConsistencyService(gameService, opponentClient)
    }

    @Test
    fun `isConsistent should return true when game states match`() {
        `when`(gameService.getGameState()).thenReturn(sharedState)
        `when`(opponentClient.getGameState()).thenReturn(sharedState)
        consistencyService.checkConsistency()
        assertTrue(consistencyService.isConsistent())
    }

    @Test
    fun `isConsistent should return false when states differ`() {
        `when`(gameService.getGameState()).thenReturn(sharedState)
        `when`(opponentClient.getGameState()).thenReturn(sharedState.copy(turn = O))
        consistencyService.checkConsistency()
        assertFalse(consistencyService.isConsistent())
    }

    @Test
    fun `should call resendLastMove when inconsistent during my turn`() {
        `when`(gameService.getGameState()).thenReturn(sharedState)
        `when`(opponentClient.getGameState()).thenReturn(sharedState.copy(turn = O))
        `when`(gameService.isMyTurn()).thenReturn(true)
        consistencyService.checkConsistency()
        verify(gameService).resendLastMove()
    }

    @Test
    fun `should not call resendLastMove if not my turn`() {
        `when`(gameService.getGameState()).thenReturn(sharedState)
        `when`(opponentClient.getGameState()).thenReturn(sharedState.copy(turn = O))
        `when`(gameService.isMyTurn()).thenReturn(false)
        consistencyService.checkConsistency()
        verify(gameService, never()).resendLastMove()
    }
}
