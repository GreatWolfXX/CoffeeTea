package com.gwolf.coffeetea.domain.usecase.database.get

import android.util.Log
import androidx.paging.PagingData
import androidx.paging.map
import com.gwolf.coffeetea.domain.model.Category
import com.gwolf.coffeetea.domain.repository.remote.CategoryRepository
import com.gwolf.coffeetea.util.LOGGER_TAG
import com.gwolf.coffeetea.util.toDomain
import com.gwolf.coffeetea.util.toEntity
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class GetCategoriesListUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    operator fun invoke(): Flow<PagingData<Category>> = callbackFlow {
        try {
            categoryRepository.getCategories().collect { response ->
                val data = response.map { categoryPagingData ->
                    val category = categoryPagingData.toEntity()
                    return@map category.toDomain(categoryPagingData.imageUrl)
                }
                trySend(data)
            }
        } catch (e: Exception) {
            Log.d(LOGGER_TAG, "Category Paging Data Error! : $e")
        } finally {
            close()
        }
        awaitClose()
    }
}