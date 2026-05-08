package com.memowave.app.ui.common.category

import androidx.annotation.DrawableRes
import com.memowave.app.R

/**
 * Реестр поддерживаемых клиентом иконок категорий. Бэкенд хранит просто строку
 * (`iconName`), сопоставление со drawable-ресурсами — здесь. Если приходит
 * неизвестное имя, отдаём [DEFAULT].
 *
 * Чтобы добавить новую иконку: положить drawable в `res/drawable`, добавить
 * запись в [ALL] с уникальным `name` и подходящим [labelResId].
 */
object CategoryIcons {

    data class CategoryIcon(
        val name: String,
        @DrawableRes val drawableRes: Int,
        val labelResId: Int
    )

    const val DEFAULT = "default"

    val ALL: List<CategoryIcon> = listOf(
        CategoryIcon(
            name = DEFAULT,
            drawableRes = R.drawable.round_local_library_24,
            labelResId = R.string.category_icon_default
        ),
        CategoryIcon(
            name = "language",
            drawableRes = R.drawable.round_translate_24,
            labelResId = R.string.category_icon_language
        ),
        CategoryIcon(
            name = "globe",
            drawableRes = R.drawable.round_public_24,
            labelResId = R.string.category_icon_globe
        ),
        CategoryIcon(
            name = "fire",
            drawableRes = R.drawable.round_fire_24,
            labelResId = R.string.category_icon_fire
        ),
        CategoryIcon(
            name = "star",
            drawableRes = R.drawable.round_stars_24,
            labelResId = R.string.category_icon_star
        ),
        CategoryIcon(
            name = "crown",
            drawableRes = R.drawable.round_crown_24,
            labelResId = R.string.category_icon_crown
        ),
        CategoryIcon(
            name = "palette",
            drawableRes = R.drawable.round_palette_24,
            labelResId = R.string.category_icon_palette
        ),
        CategoryIcon(
            name = "emoji",
            drawableRes = R.drawable.round_emoji_symbols_24,
            labelResId = R.string.category_icon_emoji
        ),
        CategoryIcon(
            name = "cards",
            drawableRes = R.drawable.playing_cards_24,
            labelResId = R.string.category_icon_cards
        ),
        CategoryIcon(
            name = "bolt",
            drawableRes = R.drawable.electric_bolt_24,
            labelResId = R.string.category_icon_bolt
        ),
        CategoryIcon(
            name = "extension",
            drawableRes = R.drawable.baseline_extension_24,
            labelResId = R.string.category_icon_extension
        ),
        CategoryIcon(
            name = "shop",
            drawableRes = R.drawable.round_local_mall_24,
            labelResId = R.string.category_icon_shop
        )
    )

    private val byName: Map<String, CategoryIcon> = ALL.associateBy { it.name }

    /** Drawable for given name. Falls back to default if name is null/unknown. */
    @DrawableRes
    fun resolveDrawable(name: String?): Int =
        byName[name]?.drawableRes ?: byName[DEFAULT]!!.drawableRes

    /** Full icon entry, or default. */
    fun resolve(name: String?): CategoryIcon =
        byName[name] ?: byName[DEFAULT]!!
}
