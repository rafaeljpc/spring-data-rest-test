package io.rafaeljpc.spring.data.rest.test.config

import org.springdoc.core.SpringDocUtils
import org.springdoc.core.converters.models.Pageable
import org.springframework.context.annotation.Configuration

@Configuration
class SpringDocConfiguration {

    init {
        SpringDocUtils.getConfig()
            .replaceWithClass(org.springframework.data.domain.Pageable::class.java, Pageable::class.java)
            .replaceWithClass(org.springframework.data.domain.PageRequest::class.java, Pageable::class.java)
    }

}