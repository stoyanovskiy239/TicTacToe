package com.stoyanovskiy.tictactoe.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.stoyanovskiy.tictactoe.model.Move
import com.stoyanovskiy.tictactoe.model.Symbol.X
import com.stoyanovskiy.tictactoe.sync.ConsistencyService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.mockito.Mockito.*
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = ["opponent.url=http://localhost:2393"])
@AutoConfigureMockMvc
class GameControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var consistencyService: ConsistencyService

    @BeforeEach
    fun setup() {
        `when`(consistencyService.isConsistent()).thenReturn(true)
    }

    @Test
    fun `move should be accepted and board updated`() {
        mockMvc.perform(
            post("/game/move")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Move(0, 0, X)))
        )
            .andExpect(status().isOk)
            .andExpect(content().string("true"))

        mockMvc.perform(get("/game/state"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.boardMatrix[0][0]").value("X"))
    }
}