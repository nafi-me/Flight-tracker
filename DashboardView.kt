###

package view

import controller.FlightController
import controller.WebSocketController
import model.Flight
import tornadofx.*

class DashboardView : View("Flight Dashboard") {

    private val flightController: FlightController by inject()
    private val ws: WebSocketController by inject()

    override val root = borderpane {

        top = hbox(10) {
            button("Refresh Flights") {
                action { reloadFlights() }
            }
        }

        center = tableview<Flight> {
            column("ID", Flight::id)
            column("ICAO", Flight::icao)
            column("Callsign", Flight::callsign)
            column("Origin", Flight::origin)
            column("Destination", Flight::destination)

            items = mutableListOf<Flight>().asObservable()
        }
    }

    init {
        reloadFlights()

        ws.listener = { json ->
            println("Real-time: $json")
        }
        ws.connect()
    }

    private fun reloadFlights() {
        val flights = flightController.fetchFlights()
        (root.center as TableView<Flight>).items.setAll(flights)
    }
}
