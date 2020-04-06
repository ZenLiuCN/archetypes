
package ${package}.domain.common

import com.fasterxml.jackson.annotation.JsonIgnore
import ${package}.domain.common.protocol.*
import io.ebean.*
import io.ebean.annotation.*
import java.time.*
import javax.persistence.*

/**
 * 基本领域实体
 * @property id Long
 * @property createdAt Instant?
 * @property modifiedAt Instant?
 */
@MappedSuperclass
abstract class DomainModel : Model(), DTOWithID {
	@Id
	@DbComment("ID")
	override val id: Long = 0

	@JsonIgnore
	@WhenCreated
	@DbComment("创建时间")
	val createdAt: Instant? = null

	@JsonIgnore
	@WhenModified
	@DbComment("最后修改时间")
	val modifiedAt: Instant? = null

	@JsonIgnore
	@WhoCreated
	@DbComment("创建人")
	val createdBy: Long? = null

	@JsonIgnore
	@WhoModified
	@DbComment("修改人")
	val modifiedBy: Long? = null
	/**
	 * 本实体数据是否有效
	 * @return Boolean
	 */
	open fun validate(): Boolean = true

}
