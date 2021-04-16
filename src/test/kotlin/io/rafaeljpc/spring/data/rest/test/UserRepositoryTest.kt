package io.rafaeljpc.spring.data.rest.test

import io.rafaeljpc.spring.data.rest.test.model.UserEntity
import io.rafaeljpc.spring.data.rest.test.repository.UserRepository
import org.junit.jupiter.api.Order
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.ParameterizedTypeReference
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.MediaTypes
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import javax.transaction.Transactional
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue


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
        val rnd = (1..100).random()

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
    fun `should list all users`() {
        // Given
        repeat(5) {
            val rnd = (1..100).random()
            val user = UserEntity(
                name = "test_${rnd}",
                email = "test_${rnd}@test.com"
            )

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

        userList.forEach {
            assertTrue(it.name.startsWith("test"))
        }
    }
}