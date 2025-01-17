package com.gwolf.coffeetea.data.local.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.gwolf.coffeetea.data.local.database.entities.LocalProductEntity
import com.gwolf.coffeetea.util.PRODUCTS_TABLE

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addProducts(products: List<LocalProductEntity>)

    @Transaction
    @Query("SELECT * FROM $PRODUCTS_TABLE")
    fun getProducts(): PagingSource<Int, LocalProductEntity>

    @Query("DELETE FROM $PRODUCTS_TABLE")
    suspend fun clearProducts()
}