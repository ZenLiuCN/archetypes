package ${package}.domain.port


interface SmsPort {
	fun send(phone:String,code:String):Boolean
}
