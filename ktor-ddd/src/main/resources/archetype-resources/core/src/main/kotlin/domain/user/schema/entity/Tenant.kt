package ${package}.domain.user.schema.entity

import com.fasterxml.jackson.annotation.*
import ${package}.domain.common.*
import io.ebean.*
import io.ebean.annotation.Cache
import javax.persistence.*


@Cache
@Entity
class Tenant(
	val name:String,
	val addr:String
) : DomainModel(){
	@JsonIgnore
	@OneToMany(cascade = [CascadeType.ALL])
	val departments:MutableList<Department> = mutableListOf()
	companion object: TenantFinder()
}
open class TenantFinder: Finder<Long, Hospital>(Tenant::class.java)
