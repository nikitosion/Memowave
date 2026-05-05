package com.memowave.app.domain.usecase.media

import com.memowave.app.domain.repository.MediaRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DeleteImageUseCaseTest {

    private val repository: MediaRepository = mockk()
    private val useCase = DeleteImageUseCase(repository)

    @Test
    fun `delegates delete to repository`() = runTest {
        coEvery { repository.deleteImage("a.jpg") } returns Result.success(Unit)

        val result = useCase("a.jpg")

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { repository.deleteImage("a.jpg") }
    }

    @Test
    fun `propagates failure from repository`() = runTest {
        val error = RuntimeException("nope")
        coEvery { repository.deleteImage(any()) } returns Result.failure(error)

        val result = useCase("a.jpg")

        assertTrue(result.isFailure)
        assertEquals(error, result.exceptionOrNull())
    }
}
