package com.memowave.app.di

import android.content.Context
import coil3.ImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import com.memowave.app.core.media.ImageUrlResolver
import com.memowave.app.data.remote.api.MediaApiService
import com.memowave.app.data.repository.MediaRepositoryImpl
import com.memowave.app.domain.repository.MediaRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MediaModule {

    @Provides
    @Singleton
    fun provideMediaApiService(retrofit: Retrofit): MediaApiService =
        retrofit.create(MediaApiService::class.java)

    @Provides
    @Singleton
    fun provideMediaRepository(
        apiService: MediaApiService
    ): MediaRepository = MediaRepositoryImpl(apiService)

    @Provides
    @Singleton
    fun provideImageUrlResolver(baseUrl: String): ImageUrlResolver =
        ImageUrlResolver(baseUrl)

    @Provides
    @Singleton
    fun provideImageLoader(
        @ApplicationContext context: Context,
        okHttpClient: OkHttpClient
    ): ImageLoader = ImageLoader.Builder(context)
        .components {
            add(OkHttpNetworkFetcherFactory(callFactory = { okHttpClient }))
        }
        .build()
}
