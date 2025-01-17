package com.gwolf.coffeetea.data.local.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gwolf.coffeetea.data.local.database.entities.LocalCategoryEntity
import com.gwolf.coffeetea.util.CATEGORIES_TABLE

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCategories(categories: List<LocalCategoryEntity>)

    @Query("SELECT * FROM $CATEGORIES_TABLE")
    fun getCategories(): PagingSource<Int, LocalCategoryEntity>

    @Query("DELETE FROM $CATEGORIES_TABLE")
    suspend fun clearCategories()
}