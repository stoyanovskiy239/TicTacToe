package com.stoyanovskiy.tictactoe.service

import com.stoyanovskiy.tictactoe.client.OpponentClient
import com.stoyanovskiy.tictactoe.model.*
import com.stoyanovskiy.tictactoe.model.GameStatus.*
import com.stoyanovskiy.tictactoe.model.Symbol.*
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.annotations.TestOnly
import org.springframework.stereotype.Service

@Service
class GameService(private val opponentClient: OpponentClient) {
    private val gameBoard = GameBoard()
    private var gameStatus = IN_PROGRESS
    private var localPlayer = NONE
    private var turn = X
    private var winner = NONE
    private var lastMove: Move? = null

    @PostConstruct
    fun initGame() {
        CoroutineScope(Dispatchers.Default).launch {
            while (localPlayer == NONE) {
                try {
                    val response = opponentClient.negotiateRole(RoleRequest(X))
                    if (response.accepted) {
                        localPlayer = X
                        println("Successfully negotiated role, I'm X")
                        makeDelayedMove()
                    } else {
                        localPlayer = response.assignedSymbol
                        println("Opponent already picked a role, I'm $localPlayer")
                    }
                } catch (e: Exception) {
                    println("Opponent not available. Retrying in ${DELAY / 1000}s...")
                }
                delay(DELAY)
            }
        }
    }

    fun getGameState() = GameState(gameBoard.getMatrix(), gameStatus, turn, winner)

    fun applyMove(move: Move): Boolean {
        if (gameStatus != IN_PROGRESS) return false
        if (!gameBoard.applyMove(move)) return false
        updateGameStatus()
        if (gameStatus == IN_PROGRESS) {
            turn = turn.opponent()
            if (localPlayer == turn) makeDelayedMove()
        }
        printGameState()
        return true
    }

    private fun makeDelayedMove() {
        CoroutineScope(Dispatchers.Default).launch {
            delay(DELAY)
            val (row, col) = findRandomEmptyCell(gameBoard.getMatrix()) ?: return@launch
            val move = Move(row, col, localPlayer)
            lastMove = move
            try {
                opponentClient.sendMove(move)
                applyMove(move)
            } catch (e: Exception) {
                println("Error during delayed move: ${e.message}")
            }
        }
    }

    fun resendLastMove() {
        try {
            lastMove?.let {
                println("Re-sending last move to opponent: $it")
                opponentClient.sendMove(it)
            }
        } catch (e: Exception) {
            println("Failed to re-send last move: ${e.message}")
        }
    }

    fun updateGameStatus() {
        winner = gameBoard.checkWinner()
        gameStatus = when {
            winner != NONE -> WIN
            gameBoard.isFull() -> TIE
            else -> IN_PROGRESS
        }
    }

    fun assignSymbol(requested: Symbol): RoleResponse {
        if (localPlayer != NONE) return RoleResponse(false, localPlayer)
        localPlayer = requested.opponent()
        return RoleResponse(true, localPlayer)
    }

    fun isMyTurn() = localPlayer == turn

    fun restartGame() {
        resetLocalState()
        try {
            opponentClient.resetGame()
        } catch (e: Exception) {
            println("Failed to notify opponent of game reset: ${e.message}")
        }
        initGame()
    }

    fun resetLocalState() {
        gameBoard.reset()
        gameStatus = IN_PROGRESS
        localPlayer = NONE
        turn = X
        winner = NONE
    }

    private fun printGameState() {
        println("=== Game State ===")
        println(gameBoard)
        println("Status: $gameStatus")
        println(if (winner == NONE) "Next turn: $turn" else "Winner: $winner")
        println("==================\n")
    }

    @TestOnly
    fun setLocalPlayer(symbol: Symbol) {
        localPlayer = symbol
    }

    companion object {
        private const val DELAY = 2000L // ms

        private fun findRandomEmptyCell(boardMatrix: Array<Array<Symbol>>): Pair<Int, Int>? {
            val emptyCells = mutableListOf<Pair<Int, Int>>()
            for (row in 0..2) {
                for (col in 0..2) {
                    if (boardMatrix[row][col] == NONE) {
                        emptyCells.add(row to col)
                    }
                }
            }
            return if (emptyCells.isNotEmpty()) emptyCells.random() else null
        }
    }
}
