package com.portfolio.basketball_stats_api.player;

import com.portfolio.basketball_stats_api.auth.SecurityConfig;
import com.portfolio.basketball_stats_api.common.NotFoundException;
import com.portfolio.basketball_stats_api.player.dto.CreatePlayerRequest;
import com.portfolio.basketball_stats_api.player.dto.PlayerResponse;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlayerController.class)
@Import(SecurityConfig.class)
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
        when(playerService.createPlayer(any(), eq("bastian"))).thenReturn(response);

        mockMvc.perform(post("/api/players")
                        .with(jwt().jwt(token -> token.subject("bastian"))
                            .authorities(new SimpleGrantedAuthority("PLAYER_WRITE")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreatePlayerRequest("Bastian", DominantHand.RIGHT, (short) 169))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Bastian"));
    }

    @Test
    void createPlayerRejectsBlankName() throws Exception {
        mockMvc.perform(post("/api/players")
                        .with(jwt().jwt(token -> token.subject("bastian"))
                            .authorities(new SimpleGrantedAuthority("PLAYER_WRITE")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreatePlayerRequest("", DominantHand.RIGHT, (short) 169))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createPlayerWithoutTokenIsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreatePlayerRequest("Bastian", DominantHand.RIGHT, (short) 169))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createPlayerWithoutWritePermissionIsForbidden() throws Exception {
        mockMvc.perform(post("/api/players")
                        .with(jwt().jwt(token -> token.subject("bastian"))
                            .authorities(new SimpleGrantedAuthority("PLAYER_READ")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreatePlayerRequest("Bastian", DominantHand.RIGHT, (short) 169))))
                .andExpect(status().isForbidden());
    }

    @Test
    void getPlayerReturnsNotFoundWhenMissing() throws Exception {
        UUID id = UUID.randomUUID();
        when(playerService.getPlayer(id, "bastian"))
            .thenThrow(new NotFoundException("Player not found: " + id));

        mockMvc.perform(get("/api/players/" + id)
                .with(jwt().jwt(token -> token.subject("bastian"))
                    .authorities(new SimpleGrantedAuthority("PLAYER_READ"))))
                .andExpect(status().isNotFound());
    }
}
