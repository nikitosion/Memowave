package com.memowave.app.data.mapper

import com.memowave.app.data.remote.dto.security.SessionDto
import com.memowave.app.domain.model.security.UserSession

class SessionMapper {

    fun dtoToDomain(dto: SessionDto): UserSession {
        return UserSession(
            sessionId = dto.sessionId,
            name = dto.name,
        )
    }
}
