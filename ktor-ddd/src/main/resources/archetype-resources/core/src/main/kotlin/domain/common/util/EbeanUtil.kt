
package ${package}.domain.common.util

import io.ebean.typequery.*


@Suppress("NOTHING_TO_INLINE")
inline fun <T : Any, Q : TQRootBean<T, Q>> TQRootBean<T, Q>.findFirstOne() =
	setMaxRows(1).findList()
		.firstOrNull()


