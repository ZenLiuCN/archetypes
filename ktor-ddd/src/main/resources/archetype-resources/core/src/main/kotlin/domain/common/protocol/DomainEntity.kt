package ${package}.domain.common.protocol

import ${package}.domain.common.*

interface DomainEntity<out T : DomainModel> {
	fun asEntity(): T
}

