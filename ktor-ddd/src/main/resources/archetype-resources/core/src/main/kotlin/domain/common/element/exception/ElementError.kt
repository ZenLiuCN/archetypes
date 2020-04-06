
package ${package}.domain.common.element.exception

import ${package}.domain.common.*


open class ElementError(message: String?) : DomainError(message)
open class ElementNotExists(id: Long, type: String?) : DomainError("element of $type with id $id not exists")


