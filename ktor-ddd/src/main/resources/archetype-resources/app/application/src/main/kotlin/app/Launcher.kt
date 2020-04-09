@file:JvmName("Launcher")
package ${package}.app



import cn.zenliu.ktor.features.auth.*
import  ${package}.app.*
import  ${package}.app.api.*
import  ${package}.app.api.common.*
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.routing.*


fun Application.main() {
	ApiProcessor.apply {
		setErrorMapper {
			"" to HttpStatusCode.InternalServerError
		}
	}
	Routes.register(
		Routing::exampleApi
	)

	entrance(
		cacheInitializer = {
			withCacheBuilder(

			)
		},
		authenticationInitializer = {

		},
		corsInitializer = {

		})
}
