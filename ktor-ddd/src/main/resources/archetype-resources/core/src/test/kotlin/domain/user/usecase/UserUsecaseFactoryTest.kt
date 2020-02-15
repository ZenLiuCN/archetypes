package ${package}.domain.user.usecase

import com.fasterxml.jackson.databind.*
import org.junit.jupiter.api.*

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class UserUsecaseFactoryTest {
	private val map = mutableMapOf<String, String>()


	val mapper = ObjectMapper()

	init {
		mapper.findAndRegisterModules()
	}


	@BeforeAll
	fun initData() {

	}

	@BeforeEach
	fun setUp() {

	}

	@AfterEach
	fun tearDown() {
	}

	@Order(1)
	@Test
	fun userFirstLogin() {

	}

	@Order(2)
	@Test
	fun userLogin() {

	}

}
