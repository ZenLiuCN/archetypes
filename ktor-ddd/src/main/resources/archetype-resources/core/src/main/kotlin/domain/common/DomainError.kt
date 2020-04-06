package ${package}.domain.common

/**
 * 领域异常
 * @constructor
 */
abstract class DomainError(msg: String?) : Throwable(msg)
