package com.portfolio.basketball_stats_api.shot;

import com.portfolio.basketball_stats_api.auth.SecurityConfig;
import com.portfolio.basketball_stats_api.common.NotFoundException;
import com.portfolio.basketball_stats_api.shot.dto.CreateShotRequest;
import com.portfolio.basketball_stats_api.shot.dto.ShotResponse;
import com.portfolio.basketball_stats_api.shot.dto.ZoneStatisticsResponse;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ShotController.class)
@Import(SecurityConfig.class)
class ShotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ShotService shotService;

    @Test
    void createShotReturnsCreatedWithZone() throws Exception {
        UUID playerId = UUID.randomUUID();
        ShotResponse response = new ShotResponse(UUID.randomUUID(), playerId,
                BigDecimal.ZERO, BigDecimal.valueOf(7.0), ShotZone.TOP_OF_KEY_THREE, true, (short) 3, Instant.now());
        when(shotService.createShot(any())).thenReturn(response);

        mockMvc.perform(post("/api/shots")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SHOT_WRITE")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateShotRequest(playerId, BigDecimal.ZERO, BigDecimal.valueOf(7.0), true))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.zone").value("TOP_OF_KEY_THREE"))
                .andExpect(jsonPath("$.points").value(3));
    }

    @Test
    void createShotReturns404WhenPlayerMissing() throws Exception {
        UUID playerId = UUID.randomUUID();
        when(shotService.createShot(any())).thenThrow(new NotFoundException("Player not found: " + playerId));

        mockMvc.perform(post("/api/shots")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SHOT_WRITE")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateShotRequest(playerId, BigDecimal.ZERO, BigDecimal.valueOf(7.0), true))))
                .andExpect(status().isNotFound());
    }

    @Test
    void createShotRejectsMissingPosY() throws Exception {
        UUID playerId = UUID.randomUUID();

        mockMvc.perform(post("/api/shots")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SHOT_WRITE")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"playerId\":\"" + playerId + "\",\"posX\":0,\"made\":true}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createShotWithoutTokenIsUnauthorized() throws Exception {
        UUID playerId = UUID.randomUUID();

        mockMvc.perform(post("/api/shots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateShotRequest(playerId, BigDecimal.ZERO, BigDecimal.valueOf(7.0), true))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getShotsForPlayerReturnsList() throws Exception {
        UUID playerId = UUID.randomUUID();
        ShotResponse response = new ShotResponse(UUID.randomUUID(), playerId,
                BigDecimal.ZERO, BigDecimal.valueOf(5.0), ShotZone.MID_RANGE_CENTER, false, (short) 2, Instant.now());
        when(shotService.getShotsForPlayer(playerId)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/players/" + playerId + "/shots")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SHOT_READ"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].zone").value("MID_RANGE_CENTER"));
    }

        @Test
        void getZoneStatisticsReturnsAggregatedMetrics() throws Exception {
                UUID playerId = UUID.randomUUID();
                ZoneStatisticsResponse response = new ZoneStatisticsResponse(
                                ShotZone.TOP_OF_KEY_THREE, 4, 3, 1, 75.0, 9);
                when(shotService.getZoneStatisticsForPlayer(playerId)).thenReturn(List.of(response));

                mockMvc.perform(get("/api/players/" + playerId + "/stats/zones")
                                                .with(jwt().authorities(new SimpleGrantedAuthority("SHOT_READ"))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].zone").value("TOP_OF_KEY_THREE"))
                                .andExpect(jsonPath("$[0].attempts").value(4))
                                .andExpect(jsonPath("$[0].madeShots").value(3))
                                .andExpect(jsonPath("$[0].missedShots").value(1))
                                .andExpect(jsonPath("$[0].fieldGoalPercentage").value(75.0))
                                .andExpect(jsonPath("$[0].pointsScored").value(9));
        }
}
