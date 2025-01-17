package com.gwolf.coffeetea.data.local.database.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.gwolf.coffeetea.util.FAVORITES_TABLE
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = FAVORITES_TABLE)
data class LocalFavoriteEntity(
    @PrimaryKey(autoGenerate = true)
    val localFavoriteId: Int = 0,
    val id: String = "",
    val productId: Int = -1,
    val userId: String = "",
    val imageUrl: String = ""
)

@Serializable
data class FavoriteWithProductEntity(
    @Embedded val favorite: LocalFavoriteEntity,
    @Relation(
        entity = LocalProductEntity::class,
        parentColumn = "productId",
        entityColumn = "id"
    )
    val product: LocalProductEntity
)