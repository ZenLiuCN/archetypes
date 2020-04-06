
package ${package}.app.support.infra


import ${package}.app.api.dto.*
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.util.pipeline.*
import org.slf4j.*

/**
 * Exception Helper
 */
object ExceptionHelper {
	/**
	 * common handler of Throwable
	 * ```kotlin
	 * install(StatusPages) {
	 *    exception<Throwable> { cause ->
	 *      ExceptionHelper.onException(cause, this)
	 *    }
	 * }

	 * ```
	 * @param cause Throwable
	 * @param ctx PipelineContext<Unit, ApplicationCall>
	 * @param dump Boolean show other exception message or not
	 * @return Unit
	 */
	suspend fun onException(cause: Throwable, ctx: PipelineContext<Unit, ApplicationCall>, logger: Logger?,
	                        dump: Boolean = false) {
		when (cause) {
			is ErrorStatus -> ctx.call.respond(cause.status.code, cause)
			else -> {
				logger?.error("Global caught exception. ", cause)
				ctx.call.respond(HttpStatusCode.InternalServerError, cause.message?.takeIf { dump }?.let { mapOf("debug" to it) }
					?: mapOf<String, String>())
			}
		}
	}


}
