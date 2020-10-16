package io.rafaeljpc.spring.data.rest.test

import io.rafaeljpc.spring.data.rest.test.model.UserEntity
import io.rafaeljpc.spring.data.rest.test.repository.UserRepository
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import kotlin.test.assertEquals


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [DemoApplicationTestConfig::class, DemoApplication::class])
class UserRepositoryTest @Autowired constructor(
    private val webTestClient: WebTestClient,
    private val userRepository: UserRepository
){

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
}