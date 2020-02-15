package ${package}.app.api

import ${package}.app.api.user.*
import io.ktor.application.*
import io.ktor.routing.*

fun Application.apiRoutes() {
	routing {
		userApi()
	}
}
