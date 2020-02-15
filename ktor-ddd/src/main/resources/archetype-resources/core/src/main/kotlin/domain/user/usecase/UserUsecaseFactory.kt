package ${package}.domain.user.usecase

import cn.zenliu.bsonid.*
import ${package}.domain.dto.*
import ${package}.domain.port.*
import ${package}.domain.user.schema.entity.*
import java.security.*
import java.util.*


object UserUsecaseFactory {
	fun newUserUsecase(
		cache: CachePort,
		sms:SmsPort,
		hash:PasswordHashPort
	) = object : UserUsecase {
		override fun passwordLogin(username: String, password: String): Optional<LoginDTO>{
			QUser()
				.username.eq(username)
				.password.eq(hash.hash(password))
				.findOne()?.let{LoginDto(it.id,it.phone,it.name)}
		}
		override fun smsLogin(phone:String,code:String):Optional<LoginDTO>{
			TODO("not implemented")
		}
		override fun smsCode(phone:String):Boolean{
			TODO("not implemented")
		}
	}
}
