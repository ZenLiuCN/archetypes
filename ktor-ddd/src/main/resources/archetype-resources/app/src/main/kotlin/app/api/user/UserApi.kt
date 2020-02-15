package $

{ package }.app.api.doctor

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.routing.*


fun Application.userApi() {
	routing {
		route("/user") {
			post("/login") {
				//api to login with username and password
			}
			post("/sms") {
				//api to send login sms code
			}
			authenticate("user") {
			//apis after login
			}
		}
	}
}
}
