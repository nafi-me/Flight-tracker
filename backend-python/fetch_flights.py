import requests
from kafka import KafkaProducer
import json
import time

producer = KafkaProducer(
    bootstrap_servers=['kafka:9092'],
    value_serializer=lambda x: json.dumps(x).encode('utf-8')
)

API = "https://opensky-network.org/api/states/all"

while True:
    try:
        data = requests.get(API).json()
        for flight in data["states"]:
            obj = {
                "icao": flight[0],
                "lat": flight[6],
                "lon": flight[5],
                "altitude": flight[13],
                "speed": flight[9]
            }
            producer.send("flight_positions", obj)

        time.sleep(3)

    except Exception as e:
        print("Error:", e)
