
package ${package}.app

import cn.zenliu.ktor.features.auth.*
import cn.zenliu.ktor.features.cache.*
import cn.zenliu.ktor.features.datasource.*
import cn.zenliu.ktor.features.ebean.*
import ${package}.app.api.Routes.apiRoutes
import ${package}.app.support.infra.*

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import org.slf4j.event.*


/**
 * must all at end of a application module function
 * @receiver Application
 * @param cacheInitializer [@kotlin.ExtensionFunctionType] Function1<CaffeineFeature, Unit>
 * @param authenticationInitializer [@kotlin.ExtensionFunctionType] Function1<Configuration, Unit>
 * @param corsInitializer [@kotlin.ExtensionFunctionType] Function1<Configuration, Unit>?
 * @param extraServiceInitializer [@kotlin.ExtensionFunctionType] Function1<Application, Unit>?
 * @param useCurrentUserProvider Boolean
 */
fun Application.entrance(
	cacheInitializer: CaffeineManager.CaffeineFeature.() -> Unit,
	authenticationInitializer: (Authentication.Configuration.() -> Unit)?,
	corsInitializer: (CORS.Configuration.() -> Unit)? = null,
	extraServiceInitializer: (Application.() -> Unit)? = null,
	useCurrentUserProvider: Boolean = false
) {


	//region 框架组件安装并初始化
	//region 数据库 ORM
	install(Hikari)

	//move mapper config to here #1
	install(ContentNegotiation) {
		jackson {
			JacksonMapper.initMapper(this)//注册并初始化全局JacksonObjectMapper
		}
	}
	if (useCurrentUserProvider) {
		install(EbeanProvider) {
			createUserProvider(0L)
		}
	}
	install(EbeanORM) {
		configEbean {
			this.dataSource = Hikari.datasource
			if (useCurrentUserProvider) this.currentUserProvider = EbeanProvider.getCurrentUserProvider()
			this.objectMapper = JacksonMapper.mapper //so we can use mapper here #1
		}
	}
	install(Liquibase) //调整顺序,先执行Ebean初始化,再执行Liquibase

	//endregion
	//region 官方组件
	corsInitializer?.let { ci ->
		install(CORS) {
			ci.invoke(this)
			/*anyHost()
			listOf(
				HttpHeaders.AccessControlRequestMethod,
				HttpHeaders.AccessControlExposeHeaders,
				HttpHeaders.Authorization
			).forEach {
				header(it)
			}
			methods.addAll(listOf(HttpMethod.Get, HttpMethod.Post, HttpMethod.Put))
			allowCredentials = true
			allowNonSimpleContentTypes = true
			maxAgeInSeconds = Duration.ofDays(1).seconds*/
		}
	}

	install(DefaultHeaders) {
		header(HttpHeaders.Server, System.currentTimeMillis().toString())
	}
	install(CallLogging) {
		this.level = Level.INFO //用Info打印
	}
	install(Compression)

	//endregion

	//region Caffeine 缓存注册配置
	install(CaffeineManager) {
		cacheInitializer(this)
	}
	//endregion
	//region 权限验证
	authenticationInitializer?.let { ci ->
		install(Auth)
		install(Authentication) {
			ci(this)
			/*token(name = "account") {
				validate { tk ->
					AuthService.validateToken(tk).apply {
						EbeanProvider.setUserId(id)
					}
				}
			}*/
		}
	}
	//endregion
	//region 全局错误处理
	install(StatusPages) {
		exception<Throwable> { cause ->
			ExceptionHelper.onException(cause, this, log)
		}
	}
	//endregion
	//endregion

	//region 初始化内部服务
	extraServiceInitializer?.invoke(this)
	//endregion

	//region 生命周期监听
	environment.monitor.subscribe(ApplicationStarted) {
		run {

		}
	}
	environment.monitor.subscribe(ApplicationStopped) {

	}
	environment.log.info("Initialize completely.")
	//endregion


	//region 注册接口路由
	this.apiRoutes()
	//endregion

}
