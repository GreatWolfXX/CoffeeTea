package com.gwolf.coffeetea.domain.usecase.database

import com.gwolf.coffeetea.domain.model.Category
import com.gwolf.coffeetea.domain.repository.remote.CategoryRepository
import com.gwolf.coffeetea.util.UiResult
import com.gwolf.coffeetea.util.toDomain
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class GetCategoriesListUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    operator fun invoke(): Flow<UiResult<List<Category>?>> = callbackFlow {
        categoryRepository.getCategories().collect { result ->
            when(result) {
                is UiResult.Success -> {
                    val data = result.data?.map { category ->
                        return@map category.toDomain()
                    }
                    trySend(UiResult.Success(data = data))
                    close()
                }
                is UiResult.Error -> {
                    trySend(result)
                    close()
                }
            }
        }
        awaitClose()
    }
}