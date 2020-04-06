
package ${package}.app.api.dto

import com.fasterxml.jackson.annotation.*
import io.ktor.http.*

/**
 * error response
 * @property status Int? optional
 * @property msg String? front user viewable message
 * @property debug String? debug message
 * @constructor
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties("cause", "message")
class ErrorStatus(
	val status: ErrorCode,
	val msg: String? = null,
	val debug: String? = null,
	throwable: Throwable? = null
) : Throwable(debug, throwable)

enum class ErrorCode(val code: HttpStatusCode) {
	INTERNAL(HttpStatusCode.InternalServerError),
	UNAUTHORIZED(HttpStatusCode.Unauthorized),
	ANTI_HACKING(HttpStatusCode.NotFound),
	BIZ(HttpStatusCode(555, "business error"));

	@JsonValue
	fun getCode() = code.value
}

enum class CommonErrorStatus(val status: ErrorStatus) {
	INTERNAL_ERROR(ErrorStatus(ErrorCode.INTERNAL)),
	ANTI_HACKING(ErrorStatus(ErrorCode.ANTI_HACKING))
}

