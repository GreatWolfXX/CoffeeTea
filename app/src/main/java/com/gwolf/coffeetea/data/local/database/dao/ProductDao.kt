package com.gwolf.coffeetea.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gwolf.coffeetea.data.entities.ProductEntity
import com.gwolf.coffeetea.util.PRODUCTS_TABLE
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addProducts(products: List<ProductEntity>)

    @Query("SELECT * FROM $PRODUCTS_TABLE")
    fun getProducts(): Flow<List<ProductEntity>>

    @Query("DELETE FROM $PRODUCTS_TABLE")
    suspend fun clearProducts()
}