package com.stoyanovskiy.tictactoe

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(properties = ["opponent.url=http://localhost:2393"])
class TicTacToeApplicationTests {

    @Test
    fun contextLoads() {
    }

}
