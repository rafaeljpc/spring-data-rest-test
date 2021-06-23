package io.rafaeljpc.spring.data.rest.test

import io.rafaeljpc.spring.data.rest.test.model.UserEntity
import io.rafaeljpc.spring.data.rest.test.repository.UserRepository
import mu.KotlinLogging
import org.junit.jupiter.api.Order
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.ParameterizedTypeReference
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.MediaTypes
import org.springframework.hateoas.PagedModel
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.util.UriComponentsBuilder
import javax.transaction.Transactional
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue


private val log = KotlinLogging.logger { }

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [DemoApplicationTestConfig::class, DemoApplication::class]
)
class UserRepositoryTest @Autowired constructor(
    private val webTestClient: WebTestClient,
    private val userRepository: UserRepository
) {

    @Test
    @Order(1)
    fun `should register some users`() {
        // Given
        val rnd = generateId(1..1000)

        val user = UserEntity(
            name = "test_${rnd}",
            email = "test_${rnd}@test.com"
        )

        // When
        webTestClient.post()
            .uri("/api/users").body(BodyInserters.fromValue(user))
            .exchange().expectStatus().is2xxSuccessful

        // Then
        val registeredUser = userRepository.findAll().first { it.name == user.name }

        assertEquals(user.name, registeredUser.name)
        assertEquals(user.email, registeredUser.email)
    }

    @Test
    @Transactional
    @Order(2)
    fun `should list all users unpaged`() {
        // Given
        val expectedEntities = mutableListOf<String>()
        repeat(5) {
            val rnd = generateId(1..1000)
            val user = UserEntity(
                name = "test_${rnd}",
                email = "test_${rnd}@test.com"
            )

            expectedEntities.add(user.email)

            webTestClient.post()
                .uri("/api/users").body(BodyInserters.fromValue(user))
                .exchange().expectStatus().is2xxSuccessful
        }

        // When
        val parameterizedTypeReference =
            object : ParameterizedTypeReference<CollectionModel<EntityModel<UserEntity>>>() {}

        val userList = webTestClient.get().uri("/api/users").accept(MediaTypes.HAL_JSON).exchange()
            .expectStatus().is2xxSuccessful.expectBody(parameterizedTypeReference)
            .returnResult().responseBody?.content?.map { it.content!! }?.toList()
            ?: error("UNEXPECTED")

        // Then
        assertFalse(userList.isEmpty())

//        expectedEntities.forEach { email ->
//            assertNotNull(userList.find { it.email == email }, "$email not returned")
//        }
    }

    @Test
    @Transactional
    @Order(3)
    fun `should list all users with page`() {
        // Given
        val pageSize = 50
        repeat(200) {
            val rnd = generateId(1..1000)
            val user = UserEntity(
                name = "test_${rnd}",
                email = "test_${rnd}@test.com"
            )

            webTestClient.post()
                .uri("/api/users").body(BodyInserters.fromValue(user))
                .exchange().expectStatus().is2xxSuccessful
        }

        // Two Pages
        repeat(2) { pageIndex ->
            // When
            val parameterizedTypeReference =
                object : ParameterizedTypeReference<PagedModel<EntityModel<UserEntity>>>() {}


            val uri = UriComponentsBuilder.fromPath("/api/users")
                .replaceQueryParam("page", pageIndex)
                .replaceQueryParam("size", pageSize)
                .build().toUriString()
            log.info { "uri = $uri" }

            val userList = webTestClient.get().uri(uri).accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful.expectBody(parameterizedTypeReference)
                .returnResult().responseBody?.content?.map { it.content!! }?.toList()
                ?: error("UNEXPECTED")

            // Then
            assertFalse(userList.isEmpty(), "page $pageIndex")
            assertEquals(pageSize, userList.size)

            userList.forEach {
                assertTrue(it.name.startsWith("test"), "page $pageIndex")
            }
        }
    }

    companion object {
        @JvmStatic
        private val GENERATED_IDS_SET = mutableSetOf<Int>()

        @JvmStatic
        private fun generateId(range: IntRange): Int {
            var rnd: Int
            do {
                rnd = range.random()
            } while (GENERATED_IDS_SET.contains(rnd))
            GENERATED_IDS_SET.add(rnd)
            return rnd
        }
    }
}