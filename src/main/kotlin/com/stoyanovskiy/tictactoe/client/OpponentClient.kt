package com.stoyanovskiy.tictactoe.client

import com.stoyanovskiy.tictactoe.model.GameState
import com.stoyanovskiy.tictactoe.model.Move
import com.stoyanovskiy.tictactoe.model.RoleRequest
import com.stoyanovskiy.tictactoe.model.RoleResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader

@FeignClient(name = "opponent", url = "\${opponent.url}")
interface OpponentClient {

    @PostMapping("/game/move")
    fun sendMove(@RequestBody move: Move): Boolean

    @PostMapping("/game/role")
    fun negotiateRole(@RequestBody request: RoleRequest): RoleResponse

    @PostMapping("/game/reset")
    fun resetGame(@RequestHeader("X-Reset-Origin") origin: String = "opponent"): Boolean

    @GetMapping("/game/state")
    fun getGameState(): GameState
}
