package com.memowave.app.domain.usecase.media

import com.memowave.app.domain.repository.MediaRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UploadImageUseCaseTest {

    private val repository: MediaRepository = mockk()
    private val useCase = UploadImageUseCase(repository)

    @Test
    fun `delegates upload to repository and returns result`() = runTest {
        val bytes = byteArrayOf(1, 2, 3)
        coEvery { repository.uploadImage(bytes, "image/jpeg") } returns Result.success("file.jpg")

        val result = useCase(bytes, "image/jpeg")

        assertTrue(result.isSuccess)
        assertEquals("file.jpg", result.getOrNull())
        coVerify(exactly = 1) { repository.uploadImage(bytes, "image/jpeg") }
    }

    @Test
    fun `propagates failure from repository`() = runTest {
        val bytes = byteArrayOf(0)
        val error = IllegalStateException("nope")
        coEvery { repository.uploadImage(any(), any()) } returns Result.failure(error)

        val result = useCase(bytes, "image/png")

        assertTrue(result.isFailure)
        assertEquals(error, result.exceptionOrNull())
    }
}
