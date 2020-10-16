package io.rafaeljpc.spring.data.rest.test

import io.rafaeljpc.spring.data.rest.test.config.DemoApplicationConfig
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,classes = [DemoApplicationTestConfig::class, DemoApplication::class])
class DemoApplicationTests {

	@Test
	fun contextLoads() {
	}

}

@AutoConfigureWebTestClient
@Import(DemoApplicationConfig::class)
class DemoApplicationTestConfig