package com.example.flighttracker.repo;

import com.example.flighttracker.domain.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;
import java.util.List;

public interface PositionRepository extends JpaRepository<Position, Long> {
    List<Position> findTop1000ByFlightIdOrderByCreatedAtDesc(String flightId);
    List<Position> findByFlightIdAndCreatedAtBetweenOrderByCreatedAt(String flightId, Instant from, Instant to);
}
