package ${package}.domain.user.schema.entity

import ${package}.domain.common.*
import io.ebean.*
import io.ebean.annotation.Cache
import javax.persistence.*


@Cache //enable ebean l2 cache
@Entity //jpa entity
class Department( //entity class
	val name: String,
	@ManyToOne(cascade = [CascadeType.REFRESH]) //link to Entity Hospital
	val hospital: Hospital
) : DomainModel() {//inhert from base model
	@OneToMany(cascade = [CascadeType.ALL]) //link to doctors in this department
	val doctors: MutableList<Doctor> = mutableListOf()
	companion object : DepartmentFinder()
}

//!! must open
open class DepartmentFinder : Finder<Long, Department>(Department::class.java)
