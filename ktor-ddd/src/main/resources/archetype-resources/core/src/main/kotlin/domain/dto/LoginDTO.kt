package ${package}.domain.dto

import com.fasterxml.jackson.annotation.*
import java.time.*


@JsonInclude(JsonInclude.Include.NON_NULL)
data class LoginDTO(
	val id: Long?,
	val name: String,
	val phone: String
)
