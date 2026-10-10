package com.portfolio.basketball_stats_api.auth;

import com.portfolio.basketball_stats_api.player.PlayerController;
import com.portfolio.basketball_stats_api.player.PlayerService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlayerController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = {
        "app.auth.mode=google",
        "app.auth.google.client-id=123.apps.googleusercontent.com",
        "app.auth.google.allowed-emails=me@example.com"
})
class GoogleModeSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlayerService playerService;

    @Test
    void contextLoadsAndRejectsMissingToken() throws Exception {
        mockMvc.perform(get("/api/players")).andExpect(status().isUnauthorized());
    }

    @Test
    void rejectsGarbageBearerToken() throws Exception {
        mockMvc.perform(get("/api/players").header("Authorization", "Bearer not-a-jwt"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void localLoginEndpointDoesNotExist() throws Exception {
        mockMvc.perform(post("/api/auth/login").contentType("application/json").content("{}"))
                .andExpect(status().isNotFound());
    }
}
