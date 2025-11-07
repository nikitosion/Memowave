package com.memowave.app.data.mapper

import com.memowave.app.data.local.entity.UserEntity
import com.memowave.app.domain.model.User
import com.memowave.app.domain.model.UserRegistration

class UserMapper {

    fun entityToDomainUser(entity: UserEntity): User {
        return User(
            id = entity.id,
            username = entity.username,
            email = entity.email
        )
    }


    fun domainToEntity(domain: User, password: String): UserEntity {
        return UserEntity(
            id = domain.id,
            username = domain.username,
            email = domain.email,
            password = password
        )
    }

    fun userRegistrationToEntity(
        userRegistration: UserRegistration
    ): UserEntity {
        return UserEntity(
            username = userRegistration.username,
            email = userRegistration.email,
            password = userRegistration.password
        )
    }
}