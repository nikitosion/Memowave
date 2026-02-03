package com.memowave.app.data.mapper

import com.memowave.app.data.local.entity.UserEntity
import com.memowave.app.data.remote.dto.user.UserDto
import com.memowave.app.domain.model.user.User
import com.memowave.app.domain.model.user.UserRegistration

class UserMapper {

    fun entityToDomainUser(entity: UserEntity): User {
        return User(
            id = entity.id,
            username = entity.username,
            email = entity.email
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

    fun dtoToDomain(dto: UserDto): User {
        return User(
            id = dto.id,
            username = dto.username,
            email = dto.email
        )
    }
}