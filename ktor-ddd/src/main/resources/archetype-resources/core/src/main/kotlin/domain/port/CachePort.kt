package ${package}.domain.port


interface CachePort {
	fun putCode(phone:String,code:String)
	fun fetchCode(phone:String):String?
}
