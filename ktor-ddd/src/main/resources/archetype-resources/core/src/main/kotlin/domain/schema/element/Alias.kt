
package ${package}.domain.schema.element

import java.math.*

typealias Name = String
typealias Phone = String
typealias SHICode = String
typealias CitizenId = String
typealias Height = BigDecimal
typealias Weight = BigDecimal

enum class Gender(val text: String) {
	UNK("未知"), MALE("男"), FAMAL("女")
}
