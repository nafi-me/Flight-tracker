ymaps.ready(init);

let map;
let markers = {};

function init() {
    map = new ymaps.Map("map", {
        center: [55.75, 37.61],
        zoom: 4
    });

    const socket = new WebSocket("ws://localhost:8080/ws/flights");

    socket.onmessage = (msg) => {
        const data = JSON.parse(msg.data);
        updateFlight(data);
    };
}

function updateFlight(f) {
    const id = f.icao;

    if (!markers[id]) {
        markers[id] = new ymaps.Placemark([f.lat, f.lon], { balloonContent: id });
        map.geoObjects.add(markers[id]);
    }

    markers[id].geometry.setCoordinates([f.lat, f.lon]);
}
