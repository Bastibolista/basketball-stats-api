package com.portfolio.basketball_stats_api.shot;

import com.portfolio.basketball_stats_api.shot.dto.CreateShotRequest;
import com.portfolio.basketball_stats_api.shot.dto.GlobalStatisticsResponse;
import com.portfolio.basketball_stats_api.shot.dto.PagedShotsResponse;
import com.portfolio.basketball_stats_api.shot.dto.ShotResponse;
import com.portfolio.basketball_stats_api.shot.dto.ZoneStatisticsResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.RequestParam;

@RestController
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Shots", description = "Shot registration and shooting analytics")
public class ShotController {

    private final ShotService shotService;

    public ShotController(ShotService shotService) {
        this.shotService = shotService;
    }

    @PostMapping("/api/shots")
    @PreAuthorize("hasAuthority('SHOT_WRITE')")
        @Operation(summary = "Register shot", description = "Classifies coordinates into a tactical zone and stores the shot result.")
        @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Shot registered"),
            @ApiResponse(responseCode = "400", description = "Invalid coordinates or payload"),
            @ApiResponse(responseCode = "404", description = "Player not found"),
            @ApiResponse(responseCode = "403", description = "SHOT_WRITE permission required")
        })
    public ResponseEntity<ShotResponse> createShot(@Valid @RequestBody CreateShotRequest request) {
        ShotResponse response = shotService.createShot(request);
        return ResponseEntity.created(URI.create("/api/shots/" + response.id())).body(response);
    }

    @GetMapping("/api/players/{playerId}/shots")
    @PreAuthorize("hasAuthority('SHOT_READ')")
    @Operation(summary = "List player shots", description = "Returns paginated shots ordered from newest to oldest. The date range is [from, to).")
    public PagedShotsResponse getShotsForPlayer(
            @PathVariable UUID playerId,
            @Parameter(description = "Inclusive UTC start timestamp") @RequestParam(required = false) Instant from,
            @Parameter(description = "Exclusive UTC end timestamp") @RequestParam(required = false) Instant to,
            @PageableDefault(size = 20) Pageable pageable) {
        return shotService.getShotsForPlayer(playerId, from, to, pageable);
    }

    @GetMapping("/api/players/{playerId}/stats/zones")
    @PreAuthorize("hasAuthority('SHOT_READ')")
    @Operation(summary = "Get zone statistics", description = "Returns attempts, makes, misses, percentage and points for each zone.")
    public List<ZoneStatisticsResponse> getZoneStatisticsForPlayer(@PathVariable UUID playerId) {
        return shotService.getZoneStatisticsForPlayer(playerId);
    }

    @GetMapping("/api/players/{playerId}/stats")
    @PreAuthorize("hasAuthority('SHOT_READ')")
    @Operation(summary = "Get global statistics", description = "Returns aggregate shooting statistics for the player.")
    public GlobalStatisticsResponse getGlobalStatisticsForPlayer(@PathVariable UUID playerId) {
        return shotService.getGlobalStatisticsForPlayer(playerId);
    }
}
