package io.rafaeljpc.spring.data.rest.test.repository

import io.rafaeljpc.spring.data.rest.test.model.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource


@RepositoryRestResource(path = "users")
interface UserRepository : CrudRepository<UserEntity, Long> {
    fun findByNameLike(@Param("name") name: String?): List<UserEntity>
}