package com.memowave.app.domain.usecase.profile

import com.memowave.app.domain.model.user.User
import com.memowave.app.domain.repository.UserRepository
import javax.inject.Inject

class GetUserProfileInfoUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(userId: Long): Result<User> =
        repository.getUserProfileInfo(userId)
}