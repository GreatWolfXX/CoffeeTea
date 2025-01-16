package com.gwolf.coffeetea.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gwolf.coffeetea.data.entities.FavoriteEntity
import com.gwolf.coffeetea.util.FAVORITES_TABLE
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorites(favorites: List<FavoriteEntity>)

    @Query("SELECT * FROM $FAVORITES_TABLE")
    fun getFavorites(): Flow<List<FavoriteEntity>>

    @Query("DELETE FROM $FAVORITES_TABLE")
    suspend fun clearFavorites()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM $FAVORITES_TABLE WHERE id = :favoriteId")
    suspend fun removeByIdFavorite(favoriteId: Int)
}