package ${package}.app.api.user


import ${package}.app.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.*

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class UserApiTest {
	@Test
	fun userApi() = withTestApplication({
		this.main()
	}) {
		//todo some test
	}
}
