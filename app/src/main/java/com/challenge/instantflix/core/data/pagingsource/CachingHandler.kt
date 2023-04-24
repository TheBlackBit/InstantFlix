package com.challenge.instantflix.core.data.pagingsource

import com.challenge.instantflix.core.data.internal.repository.LocalDataRepository
import com.challenge.instantflix.core.data.model.MovieTvResponse
import com.challenge.instantflix.core.data.model.RequestCategory
import com.challenge.instantflix.core.data.model.TypeRequest
import com.challenge.instantflix.core.data.model.toMovieTvEntity

interface CachingHandler {
    suspend fun saveDataToCache(
        moviesResponse: MovieTvResponse,
        currentPage: Int,
        typeRequest: TypeRequest,
        requestCategory: RequestCategory,
        localDataRepository: LocalDataRepository,
    ) {
        if (currentPage == 1) localDataRepository.clearByCategory(RequestCategory.POPULAR)

        val entities = moviesResponse.result.map { movieTv ->
            val genres = movieTv.genreIds.map {
                localDataRepository.getGenre(it)?.name ?: ""
            }
            movieTv.toMovieTvEntity(
                requestCategory = RequestCategory.POPULAR,
                typeRequest = TypeRequest.MOVIE,
                page = currentPage,
                totalResult = moviesResponse.totalResults,
                genres = genres,
            )
        }
        localDataRepository.upsertMovieOrTvCached(entities)
    }
}
