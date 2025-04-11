package com.stoyanovskiy.tictactoe.controller

import com.stoyanovskiy.tictactoe.model.Move
import com.stoyanovskiy.tictactoe.model.RoleRequest
import com.stoyanovskiy.tictactoe.service.GameService
import com.stoyanovskiy.tictactoe.sync.ConsistencyService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/game")
class GameController(private val gameService: GameService, private val consistencyService: ConsistencyService) {

    @GetMapping("/state")
    fun getGameState() = gameService.getGameState()

    @GetMapping("/consistent")
    fun isConsistent() = consistencyService.isConsistent()

    @PostMapping("/move")
    fun applyMove(@RequestBody move: Move) = gameService.applyMove(move)

    @PostMapping("/role")
    fun negotiateRole(@RequestBody request: RoleRequest) = gameService.assignSymbol(request.requestedSymbol)

    @PostMapping("/restart")
    fun restartGame() = gameService.restartGame()

//    @PostMapping("/reset")
//    fun resetGame() = gameService.handleRemoteReset()

    @PostMapping("/reset")
    fun handleRemoteReset(@RequestHeader("X-Reset-Origin", required = false) origin: String?): Boolean {
        if (origin != "opponent") {
            println("Blocked direct call to /game/reset — use /game/restart instead!")
            return false
        }
        println("Received remote reset signal from opponent")
        gameService.resetLocalState()
        return true
    }

}
