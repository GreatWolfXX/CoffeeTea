package com.gwolf.coffeetea.data.local.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gwolf.coffeetea.data.local.database.entities.LocalPromotionEntity
import com.gwolf.coffeetea.util.PROMOTIONS_TABLE

@Dao
interface PromotionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPromotions(favorites: List<LocalPromotionEntity>)

    @Query("SELECT * FROM $PROMOTIONS_TABLE")
    fun getPromotions(): PagingSource<Int, LocalPromotionEntity>

    @Query("DELETE FROM $PROMOTIONS_TABLE")
    suspend fun clearPromotions()
}