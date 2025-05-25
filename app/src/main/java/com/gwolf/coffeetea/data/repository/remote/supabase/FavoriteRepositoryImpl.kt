package com.gwolf.coffeetea.data.repository.remote.supabase

import com.gwolf.coffeetea.data.dto.supabase.FavoriteDto
import com.gwolf.coffeetea.data.toDomain
import com.gwolf.coffeetea.domain.entities.Favorite
import com.gwolf.coffeetea.domain.repository.remote.supabase.FavoriteRepository
import com.gwolf.coffeetea.util.FAVORITES_TABLE
import com.gwolf.coffeetea.util.HOURS_EXPIRES_IMAGE_URL
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.time.Duration.Companion.hours

class FavoriteRepositoryImpl @Inject constructor(
    private val auth: Auth,
    private val postgrest: Postgrest,
    private val storage: Storage
) : FavoriteRepository {
    override fun getFavorites(): Flow<List<Favorite>> = flow {
        val id = auth.currentUserOrNull()?.id.orEmpty()
        val response = withContext(Dispatchers.IO) {
            postgrest.from(FAVORITES_TABLE)
                .select(Columns.raw("*, products(*)")) {
                    filter { eq("user_id", id) }
                }
                .decodeList<FavoriteDto>()
        }

        val data = response.map { favorite ->
            val imageUrl = storage.from(favorite.product?.bucketId!!)
                .createSignedUrl(favorite.product.imagePath, HOURS_EXPIRES_IMAGE_URL.hours)
            favorite.toDomain(imageUrl)
        }

        emit(data)
    }

    override fun addFavorite(productId: String): Flow<Favorite> = flow {
        val id = auth.currentUserOrNull()?.id.orEmpty()
        val favorite = FavoriteDto(
            productId = productId,
            userId = id
        )
        val response = withContext(Dispatchers.IO) {
            postgrest.from(FAVORITES_TABLE).insert(favorite) {
                select()
            }
        }.decodeSingle<FavoriteDto>()

        val imageUrl = storage.from(favorite.product?.bucketId!!)
            .createSignedUrl(favorite.product.imagePath, HOURS_EXPIRES_IMAGE_URL.hours)

        emit(response.toDomain(imageUrl))
    }

    override fun removeFavorite(favoriteId: String): Flow<Unit> = flow {
        withContext(Dispatchers.IO) {
            postgrest.from(FAVORITES_TABLE)
                .delete {
                    filter { eq("id", favoriteId) }
                }
        }
        emit(Unit)
    }
}