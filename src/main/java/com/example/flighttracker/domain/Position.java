package com.example.flighttracker.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "positions")
public class Position {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String flightId;
    private double lat;
    private double lon;
    private Integer altitude;
    private Double speed;
    private Double heading;
    private Instant createdAt;

    public Position() {}

    public Position(String flightId, double lat, double lon, Integer altitude, Double speed, Double heading) {
        this.flightId = flightId;
        this.lat = lat;
        this.lon = lon;
        this.altitude = altitude;
        this.speed = speed;
        this.heading = heading;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getFlightId() { return flightId; }
    public void setFlightId(String flightId) { this.flightId = flightId; }
    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }
    public double getLon() { return lon; }
    public void setLon(double lon) { this.lon = lon; }
    public Integer getAltitude() { return altitude; }
    public void setAltitude(Integer altitude) { this.altitude = altitude; }
    public Double getSpeed() { return speed; }
    public void setSpeed(Double speed) { this.speed = speed; }
    public Double getHeading() { return heading; }
    public void setHeading(Double heading) { this.heading = heading; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
