package com.stoyanovskiy.tictactoe.sync

import com.stoyanovskiy.tictactoe.client.OpponentClient
import com.stoyanovskiy.tictactoe.service.GameService
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicBoolean

@Service
class ConsistencyService(private val gameService: GameService, private val opponentClient: OpponentClient) {
    private val consistent = AtomicBoolean(true)

    fun isConsistent() = consistent.get()

    @PostConstruct
    fun init() = LOG.info("Consistency check service initialized")

    @Scheduled(fixedDelay = 5000)
    fun checkConsistency() = try {
        val consistentNow = gameService.getGameState() == opponentClient.getGameState()
        if (!consistentNow) {
            LOG.warn("Game state inconsistent. Attempting re-sync.")
            if (gameService.isMyTurn()) gameService.resendLastMove()
        }
        if (consistentNow != consistent.get()) {
            LOG.warn("Game state consistency changed to $consistentNow")
        }
        consistent.set(consistentNow)
    } catch (e: Exception) {
        LOG.error("Could not fetch opponent state for consistency check: ${e.message}")
        consistent.set(false)
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(ConsistencyService::class.java)
    }
}
