package ${package}.domain.user.schema.entity


import com.fasterxml.jackson.annotation.*
import ${package}.domain.common.*
import io.ebean.*
import io.ebean.annotation.Cache
import javax.persistence.*


@Cache
@Entity
class User(
	val name: String,
	@Column(unique = true, length = 11)
	val phone: String,
	val username: String,
	val password: String,
	@ManyToOne(cascade = [CascadeType.REFRESH])
	val primaryDepartment: Department,
	@JsonIgnore //fix for json
	@ManyToMany(cascade = [CascadeType.REFRESH])
	val department: MutableList<Department> //only need to link to department
) : DomainModel() {
	companion object : UserFinder()
}

open class UserFinder : Finder<Long, User>(User::class.java)

