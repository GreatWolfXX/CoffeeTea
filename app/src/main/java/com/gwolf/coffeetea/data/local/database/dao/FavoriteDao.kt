package com.gwolf.coffeetea.data.local.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.gwolf.coffeetea.data.local.database.entities.FavoriteWithProductEntity
import com.gwolf.coffeetea.data.local.database.entities.LocalFavoriteEntity
import com.gwolf.coffeetea.util.FAVORITES_TABLE

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorites(favorites: List<LocalFavoriteEntity>)

    @Transaction
    @Query("SELECT * FROM $FAVORITES_TABLE")
    fun getFavorites(): PagingSource<Int, FavoriteWithProductEntity>

    @Query("DELETE FROM $FAVORITES_TABLE")
    suspend fun clearFavorites()
}