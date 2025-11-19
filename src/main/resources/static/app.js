// src/main/resources/static/app.js
let map, vectorSource, vectorLayer;
let flights = {}; // flightId -> {meta, marker, lastPos, targetPos, visible}
let updatesCount = 0, lastCount = 0;
let stompClient = null;
let selectedFlight = null;
let speedHist = {}; // flightId -> array
let speedChart;

function initMap() {
  vectorSource = new ol.source.Vector();
  vectorLayer = new ol.layer.Vector({ source: vectorSource });

  map = new ol.Map({
    target: 'map',
    layers: [
      new ol.layer.Tile({ source: new ol.source.OSM() }),
      vectorLayer
    ],
    view: new ol.View({
      center: ol.proj.fromLonLat([37.6173, 55.7558]),
      zoom: 3
    })
  });

  requestFlights();
  initChart();
  connectStomp();
  setInterval(() => {
    const delta = updatesCount - lastCount;
    document.getElementById('updatesCount').innerText = delta;
    lastCount = updatesCount;
  }, 1000);

  animate();
  document.getElementById('toggleAll').addEventListener('click', toggleAll);
  document.getElementById('focusBtn').addEventListener('click', focusSelected);
  document.getElementById('exportBtn').addEventListener('click', exportCsvSelected);
}

function requestFlights() {
  fetch('/api/flights').then(r => r.json()).then(list => {
    list.forEach(f => {
      flights[f.id] = { meta: f, visible: true };
    });
    renderList();
  }).catch(console.error);
}

function renderList() {
  const container = document.getElementById('flightList');
  container.innerHTML = '';
  Object.keys(flights).sort().forEach(id => {
    const f = flights[id];
    const div = document.createElement('div');
    div.className = 'item';
    div.dataset.id = id;
    div.innerHTML = `<div><strong>${f.meta.callsign || id}</strong><div style="font-size:12px;color:#666">${id}</div></div><div><input type="checkbox" ${f.visible ? 'checked':''}></div>`;
    div.querySelector('input[type=checkbox]').addEventListener('change', (e)=> {
      f.visible = e.target.checked;
      updateVisibility(id);
    });
    div.addEventListener('click', () => selectFlight(id));
    container.appendChild(div);
  });
}

function createMarker(id, lon, lat) {
  const feature = new ol.Feature({
    geometry: new ol.geom.Point(ol.proj.fromLonLat([lon, lat])),
    name: id
  });
  feature.setStyle(new ol.style.Style({
    image: new ol.style.Circle({
      radius: 8,
      fill: new ol.style.Fill({ color: '#2b6df6' }),
      stroke: new ol.style.Stroke({ color: '#fff', width: 2 })
    })
  }));
  vectorSource.addFeature(feature);
  flights[id].marker = feature;
  flights[id].lastPos = {lon, lat, ts: Date.now()};
  flights[id].targetPos = {lon, lat, ts: Date.now()};
}

function updateMarker(id, lon, lat, speed) {
  if (!flights[id]) flights[id] = { meta: { id }, visible: true };
  if (!flights[id].marker) createMarker(id, lon, lat);

  flights[id].lastPos = flights[id].lastPos || {lon: lon, lat: lat, ts: Date.now()};
  flights[id].targetPos = {lon: lon, lat: lat, ts: Date.now()};

  if (!speedHist[id]) speedHist[id] = [];
  if (speed != null) {
    speedHist[id].push(speed);
    if (speedHist[id].length > 30) speedHist[id].shift();
    if (selectedFlight === id) updateChart();
  }
}

function animate() {
  requestAnimationFrame(animate);
  const now = Date.now();
  Object.keys(flights).forEach(id => {
    const f = flights[id];
    if (!f.marker || !f.lastPos || !f.targetPos) return;
    const dt = Math.min(1000, now - (f.lastPos.ts || now));
    const t = Math.min(1, dt / 1000);
    const lon = f.lastPos.lon + (f.targetPos.lon - f.lastPos.lon) * t;
    const lat = f.lastPos.lat + (f.targetPos.lat - f.lastPos.lat) * t;
    f.marker.getGeometry().setCoordinates(ol.proj.fromLonLat([lon, lat]));
  });
}

function connectStomp() {
  const socket = new SockJS('/ws');
  stompClient = Stomp.over(socket);
  stompClient.debug = null;
  stompClient.connect({}, function(frame) {
    document.getElementById('status').innerText = 'Status: connected';
    stompClient.subscribe('/topic/positions', function(message) {
      updatesCount++;
      const payload = JSON.parse(message.body);
      if (payload.type === 'positions' && Array.isArray(payload.data)) {
        payload.data.forEach(p => {
          const id = p.flightId;
          flights[id] = flights[id] || { meta: { id }, visible: true };
          flights[id].meta.callsign = p.callsign || flights[id].meta.callsign;
          updateMarker(id, p.lon, p.lat, p.speed);
        });
        renderList();
      }
    });
  }, function() {
    document.getElementById('status').innerText = 'Status: disconnected';
    setTimeout(connectStomp, 2000);
  });
}

function selectFlight(id) {
  selectedFlight = id;
  const f = flights[id];
  if (f && f.targetPos) {
    map.getView().animate({ center: ol.proj.fromLonLat([f.targetPos.lon, f.targetPos.lat]), zoom: 6, duration: 500 });
  }
  updateChart();
}

function initChart() {
  const ctx = document.getElementById('speedChart').getContext('2d');
  speedChart = new Chart(ctx, {
    type:'line',
    data:{ labels:Array(30).fill(''), datasets:[{ label:'kph', data:Array(30).fill(null), borderColor:'#2b6df6', tension:0.3, spanGaps:true }]},
    options:{ animation:false, responsive:true, plugins:{legend:{display:false}}, scales:{ x:{display:false}, y:{ beginAtZero:true } } }
  });
}

function updateChart() {
  if (!selectedFlight) return;
  const arr = speedHist[selectedFlight] || [];
  const data = [...Array(30 - arr.length).fill(null), ...arr];
  speedChart.data.datasets[0].data = data;
  speedChart.update('none');
}

function toggleAll() {
  const anyOn = Object.values(flights).some(f => f.visible);
  Object.keys(flights).forEach(id => flights[id].visible = !anyOn);
  Object.keys(flights).forEach(updateVisibility);
  renderList();
}

function updateVisibility(id) {
  const f = flights[id];
  if (!f || !f.marker) return;
  f.marker.setStyle(f.visible ? new ol.style.Style({
    image: new ol.style.Circle({ radius:8, fill:new ol.style.Fill({color:'#2b6df6'}), stroke:new ol.style.Stroke({color:'#fff',width:2}) })
  }) : null);
}

function focusSelected() {
  if (!selectedFlight) { alert('Select a flight first'); return; }
  selectFlight(selectedFlight);
}

function exportCsvSelected() {
  if (!selectedFlight) { alert('Select a flight first'); return; }
  const to = Date.now();
  const from = to - 30_000;
  window.location = `/api/history/export?flightId=${encodeURIComponent(selectedFlight)}&from=${from}&to=${to}`;
}

document.addEventListener('DOMContentLoaded', initMap);
