package ${package}.domain.user.usecase


import com.fasterxml.jackson.databind.*
import ${package}.domain.port.*
import ${package}.domain.user.schema.entity.*
import org.junit.jupiter.api.*
import java.util.concurrent.*

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class UserUsecaseFactoryTest {

	val mapper = ObjectMapper()

	init {
		mapper.findAndRegisterModules()
	}

	val mockSMS = object : SmsPort {
		override fun send(phone: String, code: String) = run {
			println("$phone => $code")
			true
		}
	}
	private val cache = ConcurrentHashMap<String, String>()
	val mockCache = object : CachePort {
		override fun putCode(phone: String, code: String) {
			cache.put(phone, code)
		}

		override fun fetchCode(phone: String) =
			cache[phone]
				?.apply { cache.remove(phone) }
	}
	val mockHasher: PasswordHashPort = object : PasswordHashPort {
		override fun hash(password: String): String = password
	}
	val usecase by lazy {
		UserUsecaseFactory.newUserUsecase(
			mockCache,
			mockSMS,
			mockHasher
		)
	}

	@BeforeAll
	fun initData() {
		val tenant = Tenant("a tenant", "some place").apply { save() }
		val department = Department("department1", tenant).apply { save() }
		val department2 = Department("department2", tenant).apply { save() }
		val department3 = Department("department3", tenant).apply { save() }
		User("a user", "13012345678", "username", "password", department, mutableListOf(department, department3))
			.apply { save() }
	}

	@Order(1)
	@Test
	fun structure() {
		Tenant.all().apply {
			assert(size == 1)
		}.first().apply {
			assert(name == "a tenant")
			assert(departments.size == 3)
		}
		Department.all().apply {
			assert(size == 3)
		}
		User.all().apply {
			assert(size == 1)
		}.first().apply {
			assert(name == "a user")
			assert(primaryDepartment.name == "department1")
			assert(department.size == 2)
			assert(department.find { it.name == "department2" } == null)
		}
	}

	@Order(2)
	@Test
	fun passwordLogin() {
		usecase.passwordLogin("username", "password").let {
			assert(it.isPresent)
		}
		usecase.passwordLogin("username", "password1").let {
			assert(!it.isPresent)
		}
	}

	@Order(3)
	@Test
	fun smsLogin() {
		usecase.smsCode("13012345678").apply {
			assert(this)
		}
		usecase.smsLogin("13012345678", cache["13012345678"] ?: "")
			.apply {
				assert(isPresent)
			}
	}

}
