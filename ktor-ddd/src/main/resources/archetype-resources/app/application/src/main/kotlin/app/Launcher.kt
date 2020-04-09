@file:JvmName("Launcher")
package ${package}.app



import cn.zenliu.ktor.features.auth.*
import  ${package}.app.*
import  ${package}.app.api.*
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.routing.*


fun Application.main() {
	Routes.register(
		Routing::exampleApi,
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
