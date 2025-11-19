package com.example.flighttracker.repo;

import com.example.flighttracker.domain.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlightRepository extends JpaRepository<Flight, String> {}
