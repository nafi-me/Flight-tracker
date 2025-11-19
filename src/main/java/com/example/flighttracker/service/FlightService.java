package com.example.flighttracker.service;

import com.example.flighttracker.domain.Flight;
import com.example.flighttracker.domain.Position;
import com.example.flighttracker.repo.FlightRepository;
import com.example.flighttracker.repo.PositionRepository;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.time.Instant;
import java.util.*;

@Service
public class FlightService {

    private final FlightRepository flightRepo;
    private final PositionRepository positionRepo;

    public FlightService(FlightRepository flightRepo, PositionRepository positionRepo) {
        this.flightRepo = flightRepo;
        this.positionRepo = positionRepo;
    }

    @PostConstruct
    public void seed() {
        if (flightRepo.count() == 0) {
            flightRepo.save(new Flight("F-100","CALL100","SVO","JFK"));
            flightRepo.save(new Flight("F-200","CALL200","LHR","DXB"));
            flightRepo.save(new Flight("F-300","CALL300","JFK","LAX"));
        }
    }

    public List<Flight> getAllFlights() {
        return flightRepo.findAll();
    }

    public void upsertFlight(Flight f) {
        flightRepo.save(f);
    }

    public Position savePosition(Position p) {
        if (p.getCreatedAt() == null) p.setCreatedAt(Instant.now());
        return positionRepo.save(p);
    }

    public List<Position> getHistory(String flightId, Instant from, Instant to) {
        return positionRepo.findByFlightIdAndCreatedAtBetweenOrderByCreatedAt(flightId, from, to);
    }
}
