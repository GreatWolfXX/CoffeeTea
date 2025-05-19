package com.gwolf.coffeetea.domain.usecase.database.get

import com.gwolf.coffeetea.domain.entities.Favorite
import com.gwolf.coffeetea.domain.repository.remote.supabase.FavoriteRepository
import com.gwolf.coffeetea.util.HOURS_EXPIRES_IMAGE_URL
import com.gwolf.coffeetea.util.DataResult
import com.gwolf.coffeetea.domain.toDomain
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import kotlin.time.Duration.Companion.hours

class GetFavoritesUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
    private val storage: Storage
) {
    operator fun invoke(): Flow<DataResult<List<Favorite>>> = callbackFlow {
        try {
            favoriteRepository.getFavorites().collect { response ->
                val data = response.map { favorite ->
                    val productImageUrl = storage.from(favorite.product?.bucketId!!)
                        .createSignedUrl(favorite.product.imagePath, HOURS_EXPIRES_IMAGE_URL.hours)
                    return@map favorite.toDomain(productImageUrl)
                }
                trySend(DataResult.Success(data = data))
            }
        } catch (e: Exception) {
            trySend(DataResult.Error(exception = e))
        } finally {
            close()
        }
        awaitClose()
    }
}