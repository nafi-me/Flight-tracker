package com.example.flighttracker.service;

import com.example.flighttracker.domain.Position;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class PositionBroadcaster {

    private final FlightService flightService;
    private final SimpMessagingTemplate messagingTemplate;
    private final Map<String, Double> headings = new HashMap<>();

    public PositionBroadcaster(FlightService flightService, SimpMessagingTemplate messagingTemplate) {
        this.flightService = flightService;
        this.messagingTemplate = messagingTemplate;
    }

    @Scheduled(fixedRate = 1000)
    public void tick() {
        var flights = flightService.getAllFlights();
        List<Map<String,Object>> payload = new ArrayList<>();
        for (var f : flights) {
            double baseLat = 55.75 + (f.getId().hashCode() % 10) * 0.01;
            double baseLon = 37.61 + (f.getId().hashCode() % 10) * 0.01;
            double heading = headings.getOrDefault(f.getId(), Math.random() * 360);
            heading = (heading + (Math.random()*10 - 5) + 360) % 360;
            headings.put(f.getId(), heading);

            double speed = 400 + Math.random()*100;
            double meters = speed * 1000.0 / 3600.0;
            double deltaDeg = meters / 111320.0;

            double lat = baseLat + Math.cos(Math.toRadians(heading)) * deltaDeg * (Math.random()*3);
            double lon = baseLon + Math.sin(Math.toRadians(heading)) * deltaDeg * (Math.random()*3);

            Position p = new Position(f.getId(), lat, lon, 30000 + (int)(Math.random()*3000), speed, heading);
            flightService.savePosition(p);

            Map<String,Object> m = new HashMap<>();
            m.put("flightId", f.getId());
            m.put("callsign", f.getCallsign());
            m.put("lat", lat);
            m.put("lon", lon);
            m.put("altitude", p.getAltitude());
            m.put("speed", p.getSpeed());
            m.put("heading", p.getHeading());
            m.put("timestamp", p.getCreatedAt().toEpochMilli());
            payload.add(m);
        }

        var envelope = Map.of("type","positions","data",payload);
        messagingTemplate.convertAndSend("/topic/positions", envelope);
    }
}
