package ${package}.domain.user.usecase

import ${package}.domain.dto.*
import java.util.*

/**
 * just some example for show how this work
 *
 */
interface UserUsecase {
	fun passwordLogin(username: String, password: String): Optional<LoginDTO>
	fun smsLogin(phone:String,code:String):Optional<LoginDTO>
	fun smsCode(phone:String):Boolean
}

