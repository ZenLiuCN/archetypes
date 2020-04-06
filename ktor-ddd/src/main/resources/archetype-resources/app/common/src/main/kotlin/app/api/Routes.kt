
package ${package}.app.api


import io.ktor.application.*
import io.ktor.routing.*

typealias Api = Routing.() -> Unit

/**
 * Api Registry
 */
object Routes {
	private val apis: MutableSet<Api> = mutableSetOf()

	/**
	 * 注册多个接口
	 */
	fun register(vararg api: Api) {
		apis.addAll(api)
	}

	fun register(api: Api) {
		apis.add(api)
	}

	internal fun Application.apiRoutes() {
		routing {
			apis.forEach { it(this) }
		}
	}

}

