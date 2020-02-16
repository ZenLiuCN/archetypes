package ${package}.app

import cn.zenliu.ktor.features.auth.*
import cn.zenliu.ktor.features.cache.*
import cn.zenliu.ktor.features.datasource.*
import cn.zenliu.ktor.features.ebean.*
import ${package}.app.api.*
import ${package}.app.support.infra.*
import ${package}.app.service.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import org.slf4j.event.*
import java.time.*


fun Application.main() {


	//region features
	//region  ORM
	install(Hikari)
	install(Liquibase)
	install(EbeanProvider) {
		createUserProvider(0L)
	}
	install(EbeanORM) {
		configEbean {
			this.dataSource = Hikari.datasource
			this.currentUserProvider = EbeanProvider.getCurrentUserProvider()
		}
	}
	//endregion
	//region offical
	install(CORS) {
		anyHost()
		header(HttpHeaders.AccessControlAllowHeaders)
		header(HttpHeaders.ContentType)
		header(HttpHeaders.AccessControlAllowOrigin)
		allowCredentials = true
		allowNonSimpleContentTypes = true
		maxAge = Duration.ofDays(1)
	}
	install(DefaultHeaders)
	install(CallLogging) {
		this.level = Level.INFO
	}
	install(Compression)
	install(ContentNegotiation) {
		jackson {
			JacksonMapper.initMapper(this)//register to JacksonMapper
		}
	}
	//endregion
	//region Caffeine
	install(CaffeineManager) {
		//buildCache<String, String>(CACHE_NAME)

	}
	//endregion
	//region Token Authentication
	install(Auth)
	install(Authentication) {
		token(name = "user") {
			validate { tk ->
				AuthService.validateToken(tk.token)?.apply {//how to validate a token
					EbeanProvider.setUserId(id) //userid
				}
			}
		}
	}
	//endregion
	//region error handler
	install(StatusPages) {
		exception<Throwable> { cause ->
			ExceptionHelper.onException(cause, this, log)
		}
	}
	//endregion
	//endregion

	//region internal service

	//endregion

	//region monitors
	environment.monitor.subscribe(ApplicationStarted) {

	}
	environment.monitor.subscribe(ApplicationStopped) {

	}
	environment.log.info("Initialize completely.")
	//endregion

	//region api routes
	apiRoutes()
	//endregion

}
