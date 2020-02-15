package ${package}.app.support.infra


import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.*
import java.time.Instant
import java.time.temporal.ChronoUnit

object JacksonMapper {
	private lateinit var _mapper: ObjectMapper
	val mapper: ObjectMapper?
		get() = _mapper.takeIf { JacksonMapper::_mapper.isInitialized }
	fun initMapper(mapper: ObjectMapper) {
		mapper.findAndRegisterModules()
		_mapper = mapper.apply {
			configure(SerializationFeature.INDENT_OUTPUT, true)
			setDefaultPrettyPrinter(DefaultPrettyPrinter().apply {
				indentArraysWith(DefaultPrettyPrinter.FixedSpaceIndenter.instance)
				indentObjectsWith(DefaultIndenter("  ", "\n"))
			})
			registerModule(JavaTimeModule())  // support java.time.* types
			registerModule(object : SimpleModule() {
				init {
					this.addSerializer(Instant::class.java, object : JsonSerializer<Instant>() {
						override fun serialize(value: Instant?, gen: JsonGenerator?, serializers: SerializerProvider?) {
							gen?.writeRawValue(value?.let {
								it.epochSecond.toString()
							} ?: "null")
						}
					})
					this.addDeserializer(Instant::class.java, object : JsonDeserializer<Instant?>() {
						override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Instant? =
							when {
								p.text.isNullOrEmpty() -> null
								p.text == "null" -> null
								else -> Instant.ofEpochSecond(p.text.toLongOrNull() ?: 0)
								//Instant.parse(p.text.replace(" ", "T") + ".000Z").minus(8,ChronoUnit.HOURS)
							}

					})
				}

			})
		}
	}

	fun valueToTree(value: Any) = mapper!!.valueToTree<JsonNode>(value)
	fun valueAsString(value: Any?) = mapper!!.writeValueAsString(value)

	inline fun <reified T:Any> readValue(src:String)= mapper!!.readValue<T>(src)
}
