
package ${package}.app.api.common

import io.ktor.application.*
import io.ktor.request.*
import io.ktor.util.pipeline.*


//region 接收帮助函数
/**
 * 安全接收body对象
 * @receiver PipelineContext<Unit, ApplicationCall>
 * @param validator Function1<T, Boolean>?
 * @return T?
 */
suspend inline fun <reified T : Any> PipelineContext<Unit, ApplicationCall>.bodyOf(noinline validator: ((T) -> Boolean)? = null) =
	kotlin.runCatching {
		call.receive<T>().takeIf { validator?.invoke(it) ?: true }
	}.onFailure {
		this.application.log.warn(" bad request for ${call.request.path()}", it)
	}.getOrNull()

/**
 *  安全接收字符串body
 * @receiver PipelineContext<Unit, ApplicationCall>
 * @param validator Function1<String, Boolean>? 校验器,可以为空,默认校验字符串不能为空
 * @return String?
 */
suspend inline fun PipelineContext<Unit, ApplicationCall>.bodyOfText(noinline validator: ((String) -> Boolean)? = null) =
	kotlin.runCatching {
		call.receiveStream().readBytes().toString(Charsets.UTF_8) //不使用默认的ReceiveText 强制UTF编码.takeIf {
			.let {
				validator?.invoke(it)
					?: it.isNotBlank()
			}
	}.onFailure {
		this.application.log.warn(" bad request for ${call.request.path()}", it)
	}.getOrNull()


/**
 * Bool 的Query参数 (默认非空为真)
 * @receiver PipelineContext<Unit, ApplicationCall>
 * @param name String
 * @param boolValue String?
 * @return Boolean
 */
@Suppress("NOTHING_TO_INLINE")
inline fun PipelineContext<Unit, ApplicationCall>.boolQueryParameter(name: String, boolValue: String? = null) = call.request.queryParameters[name]
	?.takeIf { v -> boolValue?.let { v == it } ?: v.isNotEmpty() } != null

/**
 * 获取Query参数,并转换类型
 * @receiver PipelineContext<Unit, ApplicationCall>
 * @param name String
 * @param converter Function1<String, T>
 * @return T?
 */
inline fun <reified T : Any> PipelineContext<Unit, ApplicationCall>.queryParameter(name: String, noinline converter: (String) -> T?) = call.request.queryParameters[name]
	?.takeIf { it.isNotBlank() }
	?.let { converter(it) }

/**
 * 获取Query参数
 * @receiver PipelineContext<Unit, ApplicationCall>
 * @param name String
 * @return String?
 */
inline fun <reified T : Any?> PipelineContext<Unit, ApplicationCall>.queryParameter(name: String) = call.request.queryParameters[name]
	?.takeIf { it.isNotBlank() }

/**
 * 路径参数
 * @receiver PipelineContext<Unit, ApplicationCall>
 * @param name String
 * @param validator Function1<String, Boolean>?
 * @return String?
 */
@Suppress("NOTHING_TO_INLINE")
inline fun PipelineContext<Unit, ApplicationCall>.parameter(name: String, noinline validator: ((String) -> Boolean)? = null) = call.parameters[name]
	?.takeIf { validator?.invoke(it) ?: it.isNotEmpty() }

/**
 * 路径参数(转换为指定结构)
 * @receiver PipelineContext<Unit, ApplicationCall>
 * @param name String
 * @param converter Function1<String, T>
 * @param validator Function1<String, Boolean>?
 * @return T?
 */
inline fun <reified T : Any?> PipelineContext<Unit, ApplicationCall>.parameterOf(name: String, noinline converter: (String) -> T, noinline validator: ((String) -> Boolean)? = null) = call.parameters[name]
	?.takeIf { validator?.invoke(it) ?: it.isNotEmpty() }?.let(converter)

//endregion

