package ${package}.domain.common

import io.ebean.*
import io.ebean.typequery.*
import kotlin.reflect.*
import kotlin.reflect.full.*

/**
 *
 * @param I : Any Id type
 * @param T : Any Entity Type
 * @param Q : TQRootBean<T, Q>  QueryBean Type
 * @property queryBean KClass<Q> QueryBean KClass
 * @property notFoundThrowable Function2<I, String?, DomainError>
 * @property Q Q
 * @constructor
 */
abstract class DomainFinder<I : Any, T : Any, Q : TQRootBean<T, Q>>(
	clazz: Class<T>,
	private val queryBean: KClass<Q>,
	private val notFoundThrowable: (I, String?) -> DomainError
) : Finder<I, T>(clazz) {
	val Q get() = queryBean.createInstance()
	internal fun ofId(identity: I) = this.byId(identity) ?: throw notFoundThrowable(identity, javaClass.simpleName)
}

