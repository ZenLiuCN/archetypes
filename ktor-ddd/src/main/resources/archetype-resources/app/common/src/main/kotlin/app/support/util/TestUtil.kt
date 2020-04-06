
package ${package}.app.support.util


object TestUtil {
	fun isTest() = System.getProperty("testing", "FALSE").takeIf { it == "TRUE" } != null || System.getenv("KT_TESTING").takeIf { !it.isNullOrBlank() } != null
	fun enableTesting() = System.setProperty("testing", "TRUE").let { Unit }
	fun disableTesting() = System.setProperty("testing", "FALSE").let { Unit }
	fun isDebug() = System.getenv("KT_DEBUG").takeIf { !it.isNullOrBlank() } != null
	fun onDebug(act: () -> Any) = isDebug().takeIf { it }?.let { act.invoke() }
	fun onDebugElse(act: () -> Any?, otherwise: (() -> Any)? = null) =
		isDebug().takeIf { it }
			?.let { act.invoke() }
			?: otherwise?.invoke()

	fun onTest(act: () -> Any) = isTest().takeIf { it }?.let { act() }


}

