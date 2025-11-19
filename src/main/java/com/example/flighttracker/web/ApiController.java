package com.example.flighttracker.web;

import com.example.flighttracker.domain.Flight;
import com.example.flighttracker.domain.Position;
import com.example.flighttracker.service.FlightService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;

import java.io.*;
import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final FlightService flightService;
    private final SimpMessagingTemplate messagingTemplate;

    public ApiController(FlightService flightService, SimpMessagingTemplate messagingTemplate) {
        this.flightService = flightService;
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping("/telemetry")
    public ResponseEntity<?> telemetry(@RequestBody Map<String,Object> payload) {
        String fid = (String) payload.getOrDefault("flightId", payload.get("id"));
        String callsign = (String) payload.getOrDefault("callsign", "N/A");
        double lat = ((Number)payload.get("lat")).doubleValue();
        double lon = ((Number)payload.get("lon")).doubleValue();
        Integer alt = payload.get("altitude") == null ? null : ((Number)payload.get("altitude")).intValue();
        Double speed = payload.get("speed") == null ? null : ((Number)payload.get("speed")).doubleValue();
        Double heading = payload.get("heading") == null ? null : ((Number)payload.get("heading")).doubleValue();

        Flight f = new Flight(fid, callsign, "",""); 
        flightService.upsertFlight(f);
        Position p = new Position(fid, lat, lon, alt, speed, heading);
        flightService.savePosition(p);

        Map<String,Object> m = new HashMap<>();
        m.put("flightId", fid);
        m.put("callsign", callsign);
        m.put("lat", lat);
        m.put("lon", lon);
        m.put("altitude", alt);
        m.put("speed", speed);
        m.put("heading", heading);
        m.put("timestamp", p.getCreatedAt().toEpochMilli());

        messagingTemplate.convertAndSend("/topic/positions", Map.of("type","positions","data", List.of(m)));
        return ResponseEntity.ok(Map.of("status","ok"));
    }

    @GetMapping("/history/export")
    public ResponseEntity<byte[]> exportCsv(@RequestParam String flightId, @RequestParam long from, @RequestParam long to) throws IOException {
        Instant a = Instant.ofEpochMilli(from);
        Instant b = Instant.ofEpochMilli(to);
        var rows = flightService.getHistory(flightId, a, b);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (OutputStreamWriter writer = new OutputStreamWriter(baos);
             CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("flightId","timestamp","lat","lon","altitude","speed","heading"))) {
            for (var r : rows) {
                printer.printRecord(r.getFlightId(), r.getCreatedAt().toString(), r.getLat(), r.getLon(), r.getAltitude(), r.getSpeed(), r.getHeading());
            }
        }

        byte[] bytes = baos.toByteArray();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDisposition(ContentDisposition.attachment().filename("history_"+flightId+".csv").build());
        headers.setContentLength(bytes.length);
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    @GetMapping("/flights")
    public List<Flight> listFlights() {
        return flightService.getAllFlights();
    }
}
