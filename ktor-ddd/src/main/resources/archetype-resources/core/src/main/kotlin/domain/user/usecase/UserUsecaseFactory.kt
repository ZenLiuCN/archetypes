package ${package}.domain.user.usecase

import cn.zenliu.bsonid.*
import ${package}.domain.dto.*
import ${package}.domain.port.*
import ${package}.domain.user.schema.entity.*
import ${package}.domain.user.schema.entity.query.* //this should be generate with ebean
import java.security.*
import java.util.*


object UserUsecaseFactory {
	fun newUserUsecase(
		cache: CachePort,
		sms:SmsPort,
		hash:PasswordHashPort
	) = object : UserUsecase {
		override fun passwordLogin(username: String, password: String)=
			QUser()
				.username.eq(username)
				.password.eq(hash.hash(password))
				.findOne()?.let { LoginDTO(it.id, it.phone, it.name) }
				.let { Optional.ofNullable(it) }

		override fun smsLogin(phone:String,code:String)=
			cache.fetchCode(phone)
				?.takeIf { it==code }
				?.let { QUser().phone.eq(phone).findOne() }
				?.let { LoginDTO(it.id, it.phone, it.name) }
				.let { Optional.ofNullable(it) }
		override fun smsCode(phone:String)=
			((Math.random() * 9 + 1) * 100000).toInt().toString()
				.takeIf {sms.send(phone, it)}
				?.apply { cache.putCode(phone,this) }
				.isNullOrEmpty().not()
	}
}
