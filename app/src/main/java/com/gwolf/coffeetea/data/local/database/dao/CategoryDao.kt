package com.gwolf.coffeetea.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gwolf.coffeetea.data.entities.CategoryEntity
import com.gwolf.coffeetea.util.CATEGORIES_TABLE
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Query("SELECT * FROM $CATEGORIES_TABLE")
    fun getCategories(): Flow<List<CategoryEntity>>

    @Query("DELETE FROM $CATEGORIES_TABLE")
    suspend fun clearCategories()
}