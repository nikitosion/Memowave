package com.memowave.app.data.repository

import com.memowave.app.data.remote.api.MediaApiService
import com.memowave.app.data.remote.dto.media.UploadImageResponseDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class MediaRepositoryImplTest {

    private val api: MediaApiService = mockk()
    private val repository = MediaRepositoryImpl(api)

    @Test
    fun `uploadImage returns fileName on 2xx`() = runTest {
        coEvery { api.uploadImage(any()) } returns
            Response.success(UploadImageResponseDto(fileName = "abc.jpg"))

        val result = repository.uploadImage(byteArrayOf(1, 2, 3), "image/jpeg")

        assertTrue(result.isSuccess)
        assertEquals("abc.jpg", result.getOrNull())
    }

    @Test
    fun `uploadImage returns failure on non-2xx`() = runTest {
        coEvery { api.uploadImage(any()) } returns
            Response.error(500, "boom".toResponseBody())

        val result = repository.uploadImage(byteArrayOf(1), "image/png")

        assertTrue(result.isFailure)
    }

    @Test
    fun `uploadImage returns failure on empty body`() = runTest {
        val emptyResponse: Response<UploadImageResponseDto> = Response.success(null)
        coEvery { api.uploadImage(any<MultipartBody.Part>()) } returns emptyResponse

        val result = repository.uploadImage(byteArrayOf(1), "image/png")

        assertTrue(result.isFailure)
    }

    @Test
    fun `uploadImage wraps exceptions into Result failure`() = runTest {
        val boom = RuntimeException("network is gone")
        coEvery { api.uploadImage(any()) } throws boom

        val result = repository.uploadImage(byteArrayOf(0), "image/jpeg")

        assertTrue(result.isFailure)
        assertEquals(boom, result.exceptionOrNull())
    }

    @Test
    fun `deleteImage succeeds on 2xx`() = runTest {
        coEvery { api.deleteImage("abc.jpg") } returns Response.success(Unit)

        val result = repository.deleteImage("abc.jpg")

        assertTrue(result.isSuccess)
    }

    @Test
    fun `deleteImage treats 404 as success`() = runTest {
        coEvery { api.deleteImage("missing.jpg") } returns
            Response.error(404, "not found".toResponseBody())

        val result = repository.deleteImage("missing.jpg")

        assertTrue(result.isSuccess)
    }

    @Test
    fun `deleteImage returns failure on 5xx`() = runTest {
        coEvery { api.deleteImage(any()) } returns
            Response.error(500, "boom".toResponseBody())

        val result = repository.deleteImage("x.jpg")

        assertTrue(result.isFailure)
    }
}
