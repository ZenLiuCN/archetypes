package ${package}.domain.port

interface PasswordHashPort{
	fun hash(password:String):String
}
