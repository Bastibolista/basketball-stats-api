package com.portfolio.basketball_stats_api.player;

import com.portfolio.basketball_stats_api.player.dto.CreatePlayerRequest;
import com.portfolio.basketball_stats_api.player.dto.PlayerResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

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
@Tag(name = "Players", description = "Player profile management")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PLAYER_WRITE')")
        @Operation(summary = "Create player", description = "Creates a player profile for shot tracking.")
        @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Player created"),
            @ApiResponse(responseCode = "400", description = "Invalid player data"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "PLAYER_WRITE permission required")
        })
    public ResponseEntity<PlayerResponse> createPlayer(@Valid @RequestBody CreatePlayerRequest request) {
        PlayerResponse response = playerService.createPlayer(request);
        return ResponseEntity.created(URI.create("/api/players/" + response.id())).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PLAYER_READ')")
        @Operation(summary = "Get player", description = "Returns a player profile by UUID.")
        @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Player found"),
            @ApiResponse(responseCode = "404", description = "Player not found"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
        })
    public PlayerResponse getPlayer(@PathVariable UUID id) {
        return playerService.getPlayer(id);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PLAYER_READ')")
    @Operation(summary = "List players", description = "Returns all player profiles.")
    public List<PlayerResponse> listPlayers() {
        return playerService.listPlayers();
    }
}
