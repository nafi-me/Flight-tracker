#include <stdio.h>
#include <unistd.h>

int main() {
    double lat = 40.0;
    double lon = -70.0;

    while (1) {
        lat += 0.01;
        lon += 0.01;

        printf("{\"icao\":\"SIM123\",\"lat\":%f,\"lon\":%f,\"altitude\":10000,\"speed\":500}\n",
               lat, lon);

        fflush(stdout);
        sleep(1);
    }
}
