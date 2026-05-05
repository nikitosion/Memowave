package com.memowave.app.data.mapper

import com.memowave.app.data.local.entity.WordEntity
import com.memowave.app.data.remote.dto.library.WordDto
import com.memowave.app.domain.model.Word
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class WordMapperTest {

    private val mapper = WordMapper()

    @Test
    fun `imageUrl survives domain to entity round trip`() {
        val word = Word(
            id = 7L,
            original = "apple",
            translation = "яблоко",
            imageUrl = "abc123.jpg"
        )

        val entity = mapper.domainToEntity(word)
        val back = mapper.entityToDomain(entity)

        assertEquals("abc123.jpg", entity.imageUrl)
        assertEquals("abc123.jpg", back.imageUrl)
    }

    @Test
    fun `imageUrl survives dto to entity to dto round trip`() {
        val dto = WordDto(
            id = 1L,
            text = "ocean",
            translate = "океан",
            imageUrl = "wave.png"
        )

        val entity = mapper.dtoToEntity(dto)
        val backDto = mapper.entityToDto(entity)

        assertEquals("wave.png", entity.imageUrl)
        assertEquals("wave.png", backDto.imageUrl)
    }

    @Test
    fun `null imageUrl stays null through every mapping`() {
        val word = Word(original = "x", translation = "y", imageUrl = null)
        val entity = mapper.domainToEntity(word)
        val dto = mapper.entityToDto(entity)
        val backWord = mapper.entityToDomain(entity)

        assertNull(entity.imageUrl)
        assertNull(dto.imageUrl)
        assertNull(backWord.imageUrl)
    }

    @Test
    fun `dtoToDomain reads imageUrl from server payload`() {
        val dto = WordDto(text = "cat", translate = "кот", imageUrl = "cat.jpg")
        val word = mapper.dtoToDomain(dto)
        assertEquals("cat.jpg", word.imageUrl)
    }

    @Test
    fun `domainToDto writes imageUrl to server payload`() {
        val word = Word(original = "cat", translation = "кот", imageUrl = "cat.jpg")
        val dto = mapper.domainToDto(word)
        assertEquals("cat.jpg", dto.imageUrl)
    }

    @Test
    fun `existing entity fields survive alongside imageUrl`() {
        val entity = WordEntity(
            id = 5L,
            original = "dog",
            translation = "пёс",
            categoryId = 2L,
            example = listOf("good dog"),
            imageUrl = "dog.png",
            note = "n",
            isFavorite = true,
            isSynced = true,
            remoteId = 99L,
            createdAt = 1L,
            updatedAt = 2L
        )

        val word = mapper.entityToDomain(entity)
        val backEntity = mapper.domainToEntity(word)

        assertEquals("dog.png", word.imageUrl)
        assertEquals("dog.png", backEntity.imageUrl)
        assertEquals("пёс", word.translation)
        assertEquals(listOf("good dog"), word.examples)
        assertEquals("n", word.note)
        assertEquals(true, word.isFavorite)
    }
}
