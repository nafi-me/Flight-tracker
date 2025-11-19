// c-simulator/simulator.c
// Simple flight telemetry simulator printing JSON lines to stdout
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <time.h>
#include <math.h>

double rand_double(double a, double b) {
    return a + (b - a) * (rand() / (double)RAND_MAX);
}

int main(int argc, char **argv) {
    srand(time(NULL));

    // define three sample flights
    double lat[3] = {55.75, 51.5074, 40.7128};
    double lon[3] = {37.6167, -0.1278, -74.0060};
    double heading[3] = {90.0, 180.0, 270.0};
    char *id[3] = {"F-100", "F-200", "F-300"};
    char *callsign[3] = {"CALL100", "CALL200", "CALL300"};

    while (1) {
        for (int i=0;i<3;i++) {
            double speed = rand_double(200.0, 550.0);
            double mps = speed * 1000.0 / 3600.0;
            double deltaDeg = mps / 111320.0 * rand_double(0.5, 1.5);

            // advance lat/lon by a bit depending on heading
            lat[i] += deltaDeg * cos(heading[i] * M_PI / 180.0);
            lon[i] += deltaDeg * sin(heading[i] * M_PI / 180.0);

            heading[i] += rand_double(-2.0, 2.0);
            if (heading[i] < 0) heading[i] += 360.0;
            if (heading[i] >= 360.0) heading[i] -= 360.0;

            printf("{\"flightId\":\"%s\",\"callsign\":\"%s\",\"lat\":%.6f,\"lon\":%.6f,\"altitude\":%d,\"speed\":%.2f,\"heading\":%.2f}\n",
                   id[i], callsign[i], lat[i], lon[i], (int) (rand_double(20000, 40000)), speed, heading[i]);
            fflush(stdout);
        }
        usleep(400000); // ~0.4s loop -> ~2.5 updates/s per flight
    }

    return 0;
}
