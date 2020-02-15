package ${package}.domain.common

import io.ebean.*
import io.ebean.annotation.*
import java.time.*
import javax.persistence.*


@MappedSuperclass
abstract class DomainModel:Model(){
	@Id
	val id:Long=0

	@WhenCreated
	val createAt: Instant?=null

	@WhenModified
	val updateAt: Instant?=null


}
