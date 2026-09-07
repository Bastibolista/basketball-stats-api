package com.portfolio.basketball_stats_api.shot;

import com.portfolio.basketball_stats_api.shot.dto.CreateShotRequest;
import com.portfolio.basketball_stats_api.shot.dto.ShotResponse;
import com.portfolio.basketball_stats_api.shot.dto.ZoneStatisticsResponse;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
public class ShotController {

    private final ShotService shotService;

    public ShotController(ShotService shotService) {
        this.shotService = shotService;
    }

    @PostMapping("/api/shots")
    @PreAuthorize("hasAuthority('SHOT_WRITE')")
    public ResponseEntity<ShotResponse> createShot(@Valid @RequestBody CreateShotRequest request) {
        ShotResponse response = shotService.createShot(request);
        return ResponseEntity.created(URI.create("/api/shots/" + response.id())).body(response);
    }

    @GetMapping("/api/players/{playerId}/shots")
    @PreAuthorize("hasAuthority('SHOT_READ')")
    public List<ShotResponse> getShotsForPlayer(@PathVariable UUID playerId) {
        return shotService.getShotsForPlayer(playerId);
    }

    @GetMapping("/api/players/{playerId}/stats/zones")
    @PreAuthorize("hasAuthority('SHOT_READ')")
    public List<ZoneStatisticsResponse> getZoneStatisticsForPlayer(@PathVariable UUID playerId) {
        return shotService.getZoneStatisticsForPlayer(playerId);
    }
}
