#set($d="$")
package ${package}.app.support.infra


import com.fasterxml.jackson.core.*
import com.fasterxml.jackson.core.util.*
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.module.*
import com.fasterxml.jackson.module.kotlin.*
import java.time.*

object JacksonMapper {
	private lateinit var _mapper: ObjectMapper
	val mapper by lazy { _mapper }
	val prettyMapper by lazy { ObjectMapper().configModules(true) }
	private fun ObjectMapper.configModules(pretty: Boolean = false) = this.apply {
		findAndRegisterModules()
		//registerModule(JavaTimeModule())  // support java.time.* types
		if (pretty) {
			configure(SerializationFeature.INDENT_OUTPUT, true)
			setDefaultPrettyPrinter(DefaultPrettyPrinter().apply {
				indentArraysWith(DefaultPrettyPrinter.FixedSpaceIndenter.instance)
				indentObjectsWith(DefaultIndenter("  ", "\n"))
			})
		}
		registerModule(object : SimpleModule() {
			init {
				this.addSerializer(Instant::class.java, object : JsonSerializer<Instant>() {
					override fun serialize(value: Instant?, gen: JsonGenerator?, serializers: SerializerProvider?) {
						gen?.writeRawValue(value?.let {
							//"\"${d}{value.plus(8, ChronoUnit.HOURS).toString().replace("T", " ").substring(0, 19)}\""
							it.toEpochMilli().toString() //全部为mills
						} ?: "null")
					}
				})
				this.addDeserializer(Instant::class.java, object : JsonDeserializer<Instant?>() {
					override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Instant? =
						when {
							p.text.isNullOrEmpty() -> null
							p.text == "null" -> null
							else -> Instant.ofEpochMilli(p.text.toLongOrNull() ?: 0)
							//Instant.parse(p.text.replace(" ", "T") + ".000Z").minus(8,ChronoUnit.HOURS)
						}

				})
			}

		})
	}

	fun initMapper(mapper: ObjectMapper) {
		_mapper = mapper.configModules()
	}

	fun valueToTree(value: Any) = mapper.valueToTree<JsonNode>(value)
	fun valueAsString(value: Any?, pretty: Boolean = false) = if (pretty) prettyMapper.writeValueAsString(value) else mapper.writeValueAsString(value)
	inline fun <reified T : Any> readValue(src: String) = mapper.readValue<T>(src)
}
