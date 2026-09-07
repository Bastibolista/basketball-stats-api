package com.portfolio.basketball_stats_api.player;

import com.portfolio.basketball_stats_api.common.NotFoundException;
import com.portfolio.basketball_stats_api.player.dto.CreatePlayerRequest;
import com.portfolio.basketball_stats_api.player.dto.PlayerResponse;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;

import tools.jackson.databind.ObjectMapper;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlayerController.class)
class PlayerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PlayerService playerService;

    @Test
    void createPlayerReturnsCreatedWithBody() throws Exception {
        UUID id = UUID.randomUUID();
        PlayerResponse response = new PlayerResponse(id, "Bastian", DominantHand.RIGHT, (short) 169, Instant.now());
        when(playerService.createPlayer(any())).thenReturn(response);

        mockMvc.perform(post("/api/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreatePlayerRequest("Bastian", DominantHand.RIGHT, (short) 169))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Bastian"));
    }

    @Test
    void createPlayerRejectsBlankName() throws Exception {
        mockMvc.perform(post("/api/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreatePlayerRequest("", DominantHand.RIGHT, (short) 169))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPlayerReturnsNotFoundWhenMissing() throws Exception {
        UUID id = UUID.randomUUID();
        when(playerService.getPlayer(id)).thenThrow(new NotFoundException("Player not found: " + id));

        mockMvc.perform(get("/api/players/" + id))
                .andExpect(status().isNotFound());
    }
}
