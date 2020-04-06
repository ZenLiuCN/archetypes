
package ${package}.app.api.dto

import com.fasterxml.jackson.annotation.*
import io.ktor.http.*

/**
 *  a normal response
 * @param T
 * @property data T?
 * @property status ErrorCode
 * @property msg String?
 * @property debug String?
 * @constructor
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ResponseDTO<T : Any>(
	val data: T?,
	val status: HttpStatusCode = HttpStatusCode.OK,
	val msg: String? = null,
	val debug: String? = null
)
