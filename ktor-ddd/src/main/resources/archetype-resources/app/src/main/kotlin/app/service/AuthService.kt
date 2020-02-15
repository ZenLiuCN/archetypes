package ${package}.app.service

import cn.zenliu.bsonid.*
import ${package}.domain.port.*
import ${package}.domain.user.usecase.*
import io.ktor.auth.*

object AuthService {
	private val cache = mutableMapOf<String, String>()
	private val tokenCache = mutableMapOf<String, UserPrincipal>()

	data class UserPrincipal(
		val id: Long,
		val token: BsonShortId
	) : Principal

	val service by lazy {
		UserUsecaseFactory.newUserUsecase(
			object : CachePort {
				override fun putCode(phone: String, code: String) =
					cache.put(phone, code).let { Unit }

				override fun fetchCode(phone: String) = cache[phone]
			},
			object : SmsPort {
				override fun send(phone: String, code: String) = true
			},
			object : PasswordHashPort {
				override fun hash(password: String) = password
			}
		)
	}

	fun validateToken(token: String) =
		runCatching { BsonShortId(token) }
			.getOrNull()
			?.let { tokenCache[it.hex] }
}
