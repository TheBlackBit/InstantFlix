package com.theblackbit.instantflix.core.di

import com.theblackbit.instantflix.core.data.external.datasource.TheMovieDBApi
import com.theblackbit.instantflix.core.data.external.repository.RemoteRepository
import com.theblackbit.instantflix.core.data.external.repository.TheMovieDbRepository
import com.theblackbit.instantflix.core.utils.SafeApiRequest
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteRepositoryModule {

    @Singleton
    @Provides
    fun providesRemoteDataSource(
        safeApiRequest: SafeApiRequest,
        theMovieDBApi: TheMovieDBApi,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): RemoteRepository {
        return TheMovieDbRepository(
            safeApiRequest = safeApiRequest,
            theMovieDBApi = theMovieDBApi,
            coroutineDispatcher = ioDispatcher,
        )
    }
}
