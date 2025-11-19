package view

import controller.AuthController
import tornadofx.*

class LoginView : View("Admin Login") {

    private val auth: AuthController by inject()

    private var username = ""
    private var password = ""

    override val root = form {
        fieldset("Login") {
            field("Username") {
                textfield {
                    textProperty().addListener { _, _, new ->
                        username = new
                    }
                }
            }
            field("Password") {
                passwordfield {
                    textProperty().addListener { _, _, new ->
                        password = new
                    }
                }
            }

            button("Login") {
                action {
                    if (auth.login(username, password)) {
                        replaceWith<DashboardView>(sizeToScene = true, centerOnScreen = true)
                    } else {
                        error("Login Failed", "Incorrect username or password")
                    }
                }
            }
        }
    }
}
