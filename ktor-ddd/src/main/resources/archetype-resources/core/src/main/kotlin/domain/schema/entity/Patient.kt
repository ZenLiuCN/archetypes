package ${package}.domain.basic.schema.entity

import ${package}.domain.basic.schema.element.*
import ${package}.domain.basic.schema.entity.query.*
import ${package}.domain.common.*
import ${package}.domain.common.element.exception.*
import javax.persistence.*

/**
 *
 * @property identity Long
 * @property name String
 * @property gender Gender
 * @property age String?
 * @property height BigDecimal?
 * @property weight BigDecimal?
 * @property citizenId String
 * @property refCode String?
 * @property phone String
 * @property shiCode String
 * @property active Boolean
 * @property platformId Long?
 * @constructor
 */
@Entity
class Patient(
	var identity: Identity,
	var name: Name,
	var gender: Gender,
	var age: String?,
	var height: Height?,
	var weight: Weight?,
	var citizenId: CitizenId,
	var refCode: String?,
	var phone: Phone,
	val shiCode: SHICode,
	var active: Boolean,
	val platformId: Identity? = null
) : DomainModel() {
	companion object : PatientFinder()
	open class PatientFinder : DomainFinder<Long, Patient, QPatient>(
		Patient::class.java, QPatient::class, ::ElementNotExists
	)
}
