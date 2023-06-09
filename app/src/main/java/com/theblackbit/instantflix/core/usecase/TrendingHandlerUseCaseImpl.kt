package com.theblackbit.instantflix.core.usecase

import com.theblackbit.instantflix.core.data.external.repository.RemoteRepository
import com.theblackbit.instantflix.core.data.internal.repository.LocalDataRepository
import com.theblackbit.instantflix.core.data.model.MovieTvEntity
import com.theblackbit.instantflix.core.data.model.RequestCategory
import com.theblackbit.instantflix.core.data.model.TypeRequest
import com.theblackbit.instantflix.core.data.model.toMovieTvEntity
import com.theblackbit.instantflix.core.utils.ApiResultHandle

class TrendingHandlerUseCaseImpl(
    private val localDataRepository: LocalDataRepository,
    private val remoteRepository: RemoteRepository,
) : TrendingHandlerUseCase {

    override suspend fun getTrendingMovieTvShow(typeRequest: TypeRequest): MovieTvEntity? {
        val trendingMovies = remoteRepository.requestTrendingMoviesSeries(typeRequest)
        if (trendingMovies is ApiResultHandle.Success) {
            val movieEntity = trendingMovies.value.result.map { movieTv ->
                val genres = movieTv.genreIds.map {
                    localDataRepository.getGenre(it)?.name ?: ""
                }

                movieTv.toMovieTvEntity(
                    requestCategory = RequestCategory.TRENDING,
                    typeRequest = typeRequest,
                    page = trendingMovies.value.page,
                    totalResult = trendingMovies.value.totalPages,
                    genres = genres,
                )
            }
            localDataRepository.clearByCategoryAndTypeRequest(
                requestCategory = RequestCategory.TRENDING,
                typeRequest = TypeRequest.ALL,
            )
            localDataRepository.upsertMovieOrTvCached(movieEntity)
        }

        val cachedEntity = localDataRepository.getMoviesOrTvShoesByCategoryAndTypeList(
            requestCategory = RequestCategory.TRENDING,
            typeRequest = TypeRequest.ALL,
            pageNumber = 1,
        )
        if (cachedEntity.isEmpty()) return null
        return cachedEntity.maxBy { it.popularity ?: 0.0 }
    }
}
