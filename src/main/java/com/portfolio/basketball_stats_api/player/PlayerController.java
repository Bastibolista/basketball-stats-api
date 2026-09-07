package com.portfolio.basketball_stats_api.player;

import com.portfolio.basketball_stats_api.player.dto.CreatePlayerRequest;
import com.portfolio.basketball_stats_api.player.dto.PlayerResponse;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

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
@RequestMapping("/api/players")
@SecurityRequirement(name = "bearerAuth")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PLAYER_WRITE')")
    public ResponseEntity<PlayerResponse> createPlayer(@Valid @RequestBody CreatePlayerRequest request) {
        PlayerResponse response = playerService.createPlayer(request);
        return ResponseEntity.created(URI.create("/api/players/" + response.id())).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PLAYER_READ')")
    public PlayerResponse getPlayer(@PathVariable UUID id) {
        return playerService.getPlayer(id);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PLAYER_READ')")
    public List<PlayerResponse> listPlayers() {
        return playerService.listPlayers();
    }
}
