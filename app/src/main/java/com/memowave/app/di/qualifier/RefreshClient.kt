package com.memowave.app.di.qualifier

import javax.inject.Qualifier

/**
 * Marks the dedicated OkHttp/Retrofit/[com.memowave.app.data.remote.api.RefreshApiService]
 * stack used by [com.memowave.app.data.remote.interceptor.TokenAuthenticator] to call
 * `/auth/refresh`. The qualified client has no [com.memowave.app.data.remote.interceptor.AuthInterceptor]
 * and no `Authenticator`, so a 401 from refresh cannot reenter the authenticator.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RefreshClient
