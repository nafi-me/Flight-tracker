CREATE TABLE flights (
    id SERIAL PRIMARY KEY,
    icao VARCHAR(10),
    callsign VARCHAR(20),
    origin VARCHAR(50),
    destination VARCHAR(50)
);

CREATE TABLE positions (
    id SERIAL PRIMARY KEY,
    flight_id INT REFERENCES flights(id),
    lat DOUBLE PRECISION,
    lon DOUBLE PRECISION,
    altitude INT,
    speed INT,
    timestamp TIMESTAMP DEFAULT NOW()
);
