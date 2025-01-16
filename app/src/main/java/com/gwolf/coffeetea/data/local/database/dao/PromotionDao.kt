package com.gwolf.coffeetea.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gwolf.coffeetea.data.entities.PromotionEntity
import com.gwolf.coffeetea.util.PROMOTIONS_TABLE
import kotlinx.coroutines.flow.Flow

@Dao
interface PromotionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPromotions(favorites: List<PromotionEntity>)

    @Query("SELECT * FROM $PROMOTIONS_TABLE")
    fun getPromotions(): Flow<List<PromotionEntity>>

    @Query("DELETE FROM $PROMOTIONS_TABLE")
    suspend fun clearPromotions()
}