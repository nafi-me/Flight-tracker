package com.example.flighttracker.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "flights")
public class Flight {
    @Id
    private String id;
    private String callsign;
    private String origin;
    private String destination;

    public Flight() {}

    public Flight(String id, String callsign, String origin, String destination) {
        this.id = id;
        this.callsign = callsign;
        this.origin = origin;
        this.destination = destination;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCallsign() { return callsign; }
    public void setCallsign(String callsign) { this.callsign = callsign; }
    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
}
