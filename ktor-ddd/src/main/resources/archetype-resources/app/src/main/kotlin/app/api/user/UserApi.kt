package ${package}.app.api.user

import ${package}.app.api.dto.*
import ${package}.app.service.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*


fun Application.userApi() {
	routing {
		route("/user") {
			post("/login") {
				//api to login with username and password
				runCatching {call.receive<LoginDTO>()}
					.getOrNull()
					?.let {
						AuthService.service.passwordLogin(it.username,it.password)
					}
					?.takeIf {it.isPresent}
					?.let { call.respond(it.get()) }
					?:call.respond(HttpStatusCode.Forbidden)
			}
			get("/sms/{phone}") {
				call.parameters["phone"]
					?.takeIf { it.length==11 }
					?.let {
						AuthService.service.smsCode(it)
					}
					?: call.respond(HttpStatusCode.NotFound)
			}
			post("/sms") {
				//api to send login sms code
				runCatching {call.receive<LoginDTO>()}
					.getOrNull()
					?.let {
						AuthService.service.smsLogin(it.username,it.password)
					}
					?.takeIf {it.isPresent}
					?.let { call.respond(it.get()) }
					?:call.respond(HttpStatusCode.Forbidden)
			}
			authenticate("user") {
				//apis after login
				get("logined") {
					call.respond("hello")
				}
			}
		}
	}
}
