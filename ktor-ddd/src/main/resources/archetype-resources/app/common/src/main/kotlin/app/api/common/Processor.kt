@file:Suppress("unused")

package ${package}.app.api.common


import ${package}.app.api.dto.*
import ${package}.app.support.infra.*
import ${package}.app.support.util.*
import ${package}.domain.common.*
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import org.slf4j.*

/**
 * 预处理器
 */
object ApiProcessor {
	internal var errorMapper: ((DomainError) -> String)? = null
	internal var responseWithDTO: Boolean = false
	fun setErrorMapper(mapper: ((DomainError) -> String)?) {
		errorMapper = mapper
	}

	fun setResponseType(withDTO: Boolean) {
		responseWithDTO = withDTO
	}
}

//region 接口处理帮助函数
/**
 * 错误负载处理方法枚举
 * None(不处理),
 * Debug(DEBUG进行DEBUG推送),
 * DebugOrInfo(DEBUG进行DEBUG推送,否则推送INFO),
 * Info(INFO推送),
 * Warn(Warning推送),
 * Error(Error推送),
 */
enum class FailurePayloadMethod {
	None, Debug, DebugOrInfo, Info, Warn, Error
}

/**
 * 错误负载对象
 * @property name String 本业务接口名称
 * @property payload Map<String, Any?> 业务数据
 * @property withLog Boolean 是否打印日志
 * @property code HttpStatusCode? 专用Http状态
 * @property method FailurePayloadMethod 处理级别和方法
 * @property logger Logger //额外指定日志
 * @constructor
 */
data class FailurePayload(
	val name: String,
	val payload: Map<String, Any?>,
	val withLog: Boolean = true,
	val code: HttpStatusCode = HttpStatusCode.OK,
	val method: FailurePayloadMethod = FailurePayloadMethod.None,
	val logger: Logger
) {
	/**
	 * 处理
	 * @param throwable Throwable? 错误异常
	 * @return Any?
	 */
	fun process(throwable: Throwable? = null) = withLog
		.let {
			if (it) {
				val pay = JacksonMapper.valueAsString(this.payload).let { "$name: $it" }
				when {
					//有异常就一定打印错误
					throwable != null -> logger.error(pay, throwable)
					else -> when (method) {//按定义类型进行日志
						FailurePayloadMethod.None -> logger.trace(pay)
						FailurePayloadMethod.Debug -> logger.debug(pay)
						FailurePayloadMethod.DebugOrInfo -> logger.info(pay)
						FailurePayloadMethod.Info -> logger.info(pay)
						FailurePayloadMethod.Warn -> logger.warn(pay)
						FailurePayloadMethod.Error -> logger.error(pay)
					}
				}
			}
			it
		}
		.let {
			//消息推送的处理
			when (method) {
				FailurePayloadMethod.None -> Unit
				FailurePayloadMethod.Debug ->
					logger.debug(name, payload, throwable)
				FailurePayloadMethod.DebugOrInfo ->
					logger.info(name, payload, throwable)
				FailurePayloadMethod.Warn ->
					logger.warn(name, payload, throwable)
				FailurePayloadMethod.Error ->
					logger.error(name, payload, throwable)
				FailurePayloadMethod.Info ->
					logger.info(name, payload, throwable)
			}
		}
}

/**
 * 业务接口错误专用日志器
 */
private val logger = LoggerFactory.getLogger("api.error")

val BusinessErrorStatus = HttpStatusCode(555, "Internal Error")

//内部的对象Key
val PayloadKey = AttributeKey<FailurePayload>("FailurePayload")

/**
 * 暂存业务信息
 * @receiver PipelineContext<Unit, ApplicationCall>
 * @param name String
 * @param payload Map<String, Any?>
 * @param method FailurePayloadMethod
 * @param withLog Boolean
 * @param code HttpStatusCode?
 * @param otherLogger Logger?
 */
@Suppress("NOTHING_TO_INLINE")
fun PipelineContext<Unit, ApplicationCall>.putTraceData(
	name: String,
	payload: Map<String, Any?>,
	method: FailurePayloadMethod = FailurePayloadMethod.DebugOrInfo,
	withLog: Boolean = true,
	code: HttpStatusCode = HttpStatusCode.OK,
	otherLogger: Logger? = null
) = this.context.attributes.put(PayloadKey, FailurePayload(name, payload, withLog, code, method, otherLogger ?: logger))

/**
 * 转换请求为 Pair
 * @receiver Any?
 * @return Pair<HttpStatusCode?, Any?>
 */
@Suppress("NOTHING_TO_INLINE")
inline fun Any?.asRespond(): Pair<HttpStatusCode?, Any?> = when (this) {
	is Pair<*, *> -> when {
		this.first is HttpStatusCode -> this as Pair<HttpStatusCode?, Any?>
		this.first == null && this.second != null -> this as Pair<HttpStatusCode?, Any?>
		else -> null to null
	}
	is HttpStatusCode -> this to null
	null -> null to null
	else -> null to this
}

suspend fun PipelineContext<Unit, ApplicationCall>.responseExceptionGenerator(ex: Throwable) = kotlin.run {
	when (ex) {
		is ErrorStatus -> call.respond(ex.status.code, ex)
		is DomainError -> { //领域错误处理
			logger.warn("error for business: ${call.request.path()} :${ex.message}")
			val message = ApiProcessor.errorMapper?.invoke(ex) ?: "操作失败,请重试"
			call.respond(BusinessErrorStatus, message)
		}
		else -> {
			logger.error("error on api: ${call.request.path()} :${ex.message}", ex)
			call.respond(
				TestUtil.isDebug().takeIf { it }
					?.let { HttpStatusCode.InternalServerError }
					?: HttpStatusCode.InternalServerError
				, mapOf<String, String>()
			)
		}
	}
}

suspend fun PipelineContext<Unit, ApplicationCall>.process(
	deprecated: Boolean = false,
	act: suspend PipelineContext<Unit, ApplicationCall>.() -> Pair<HttpStatusCode?, Any?>) = when {
	deprecated -> call.respond(HttpStatusCode.NotFound)
	else -> kotlin.runCatching {
		act.invoke(this)
	}.onFailure { ex ->
		this.context.attributes.getOrNull(PayloadKey)
			?.let { if (it.method == FailurePayloadMethod.Info) it.copy(method = FailurePayloadMethod.Error) else it }
			?.let { it.process(ex); it.code }
			?.let {
				if (it == HttpStatusCode.OK) responseExceptionGenerator(ex) else call.respond(it)
				this.context.attributes.remove(PayloadKey)
			}
			?: responseExceptionGenerator(ex)
	}.getOrNull()?.let { (status, body) ->
		when {
			status != null && body != null
			-> call.respond(status, if (ApiProcessor.responseWithDTO) ResponseDTO(body, status) else body)
			status != null && body == null
			-> call.respond(status)
			status == null && body != null
			-> call.respond(if (ApiProcessor.responseWithDTO) ResponseDTO(body) else body)
			else -> call.respond(HttpStatusCode.OK)
		}
		this.context.attributes.getOrNull(PayloadKey)
			?.process()
			?.let { this.context.attributes.remove(PayloadKey) }
	} ?: Unit
}
//endregion

