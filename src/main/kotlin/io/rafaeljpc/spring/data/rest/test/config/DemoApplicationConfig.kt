package io.rafaeljpc.spring.data.rest.test.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.hateoas.mediatype.hal.Jackson2HalModule
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder


@Configuration
class DemoApplicationConfig {

    @Bean
    fun objectMapperBuilder(): Jackson2ObjectMapperBuilder? {
        val builder = Jackson2ObjectMapperBuilder()
        builder.modules(Jackson2HalModule())
            .featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .serializationInclusion(JsonInclude.Include.NON_NULL)
        return builder
    }
}