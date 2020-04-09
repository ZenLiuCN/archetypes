package ${package}.app

import com.fasterxml.jackson.databind.*
import ${package}.app.support.util.*


val mapper = ObjectMapper().apply {
	findAndRegisterModules()
}

val dump = { any: Any? -> println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(any)) }

val dumpJsonNode = { any: String -> mapper.readTree(any) }

fun InitTesting() = TestUtil.enableTesting()

@Suppress("UNUSED_VARIABLE")
fun InitData() {

}

