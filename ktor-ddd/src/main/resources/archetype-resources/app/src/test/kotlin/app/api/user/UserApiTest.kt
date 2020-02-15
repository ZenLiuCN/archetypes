package ${package}.app.api.user


import ${package}.app.*
import ${package}.domain.user.schema.entity.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.*

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class UserApiTest {

	@Test
	fun userApi() = withTestApplication({
		this.main()
		//init data
		val tenant = Tenant("a tenant", "some place").apply { save() }
		val department = Department("department1", tenant).apply { save() }
		val department2 = Department("department2", tenant).apply { save() }
		val department3 = Department("department3", tenant).apply { save() }
		User("a user", "13012345678", "username", "password", department, mutableListOf(department, department3))
			.apply { save() }

	}) {
		//todo some test
		with(handleRequest(HttpMethod.Post,"/user/login") {
			addHeader("content-type","application/json")
			setBody("""{"username":"username","password":"password"}""")
		}){
			assert(response.status()== HttpStatusCode.OK)
		}
	}
}
