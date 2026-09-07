package com.portfolio.basketball_stats_api;

import com.portfolio.basketball_stats_api.player.DominantHand;
import com.portfolio.basketball_stats_api.player.Player;
import com.portfolio.basketball_stats_api.player.PlayerRepository;
import com.portfolio.basketball_stats_api.shot.Shot;
import com.portfolio.basketball_stats_api.shot.ShotRepository;
import com.portfolio.basketball_stats_api.shot.ShotZone;
import com.portfolio.basketball_stats_api.shot.ZoneStatisticsProjection;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class BasketballStatsApiApplicationTests {

	@Autowired
	private PlayerRepository playerRepository;

	@Autowired
	private ShotRepository shotRepository;

	@Container
	@ServiceConnection
	static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine")
			.withDatabaseName("basketball_stats")
			.withUsername("basketball_stats")
			.withPassword("basketball_stats");

	@Test
	void contextLoads() {
	}

	@Test
	void aggregatesShotStatisticsByZone() {
		Player player = playerRepository.save(new Player("Integration Player", DominantHand.RIGHT, (short) 169));
		shotRepository.saveAll(List.of(
				new Shot(player, BigDecimal.ZERO, BigDecimal.valueOf(7), ShotZone.TOP_OF_KEY_THREE, true),
				new Shot(player, BigDecimal.ONE, BigDecimal.valueOf(7), ShotZone.TOP_OF_KEY_THREE, true),
				new Shot(player, BigDecimal.TWO, BigDecimal.valueOf(7), ShotZone.TOP_OF_KEY_THREE, false)));

		List<ZoneStatisticsProjection> statistics = shotRepository.findZoneStatisticsByPlayerId(player.getId());

		assertThat(statistics).singleElement().satisfies(zone -> {
			assertThat(zone.getZone()).isEqualTo(ShotZone.TOP_OF_KEY_THREE);
			assertThat(zone.getAttempts()).isEqualTo(3L);
			assertThat(zone.getMadeShots()).isEqualTo(2L);
			assertThat(zone.getPointsScored()).isEqualTo(6L);
			assertThat(zone.getFieldGoalPercentage()).isCloseTo(66.666, org.assertj.core.data.Offset.offset(0.001));
		});
	}

}
