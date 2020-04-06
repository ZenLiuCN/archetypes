
package ${package}.app.tools


import cn.zenliu.ktor.features.ebean.*
import io.ebean.annotation.*

fun main(args: Array<String>) {

	EbeanUtil.generateLiquibaseChangeLog(
		platform = Platform.POSTGRES,
		user = "Zen.liu",
		changeSetName = "init",
		majorVersion = 1,
		minorVersion = 0,
		typeNumber = 0,
		resourcePath = "app/src/main/resources",
		outputPath = "app/src/main/resources/schema",
		backupPath = "app/src/main/resources/schema_backup"
	)
}

