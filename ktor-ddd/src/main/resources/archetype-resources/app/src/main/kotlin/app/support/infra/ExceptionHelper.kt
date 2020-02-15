package ${package}.app.support.infra


import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.util.pipeline.PipelineContext
import org.slf4j.Logger

/**
 * Exception Helper
 */
object ExceptionHelper{
	class StatusException(msg:String,val status:HttpStatusCode=HttpStatusCode.OK):Throwable(msg)
	fun badRequestError(msg:String="")= StatusException(msg, HttpStatusCode.BadRequest)
	fun internalError(msg:String="")= StatusException(msg, HttpStatusCode.InternalServerError)
	fun notFoundError(msg:String="")= StatusException(msg, HttpStatusCode.NotFound)
	fun otherError(code:Int,msg:String="")= StatusException(msg, HttpStatusCode(code, msg))
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
	suspend fun onException(cause: Throwable, ctx: PipelineContext<Unit, ApplicationCall>,logger: Logger?,
	                        dump:Boolean=false) {
		when (cause){
			is StatusException ->ctx.call.respond(cause.status,cause.message?:"")
			else->{
				logger?.error("Global caught exception. ",cause)
				ctx.call.respond(HttpStatusCode.InternalServerError,cause.message?.takeIf { dump }?:"")
			}
		}
	}


}
