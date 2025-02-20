package com.gwolf.coffeetea.domain.usecase.database.get

import com.gwolf.coffeetea.domain.model.Favorite
import com.gwolf.coffeetea.domain.repository.remote.supabase.FavoriteRepository
import com.gwolf.coffeetea.util.HOURS_EXPIRES_IMAGE_URL
import com.gwolf.coffeetea.util.UiResult
import com.gwolf.coffeetea.util.toDomain
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import kotlin.time.Duration.Companion.hours

class GetFavoritesListUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
    private val storage: Storage
) {
    operator fun invoke(): Flow<UiResult<List<Favorite>>> = callbackFlow {
        try {
            favoriteRepository.getFavorites().collect { response ->
                val data = response.map { favorite ->
                    val productImageUrl = storage.from(favorite.product?.bucketId!!)
                        .createSignedUrl(favorite.product.imagePath, HOURS_EXPIRES_IMAGE_URL.hours)
                    return@map favorite.toDomain(productImageUrl)
                }
                trySend(UiResult.Success(data = data))
            }
        } catch (e: Exception) {
            trySend(UiResult.Error(exception = e))
        } finally {
            close()
        }
        awaitClose()
    }
}