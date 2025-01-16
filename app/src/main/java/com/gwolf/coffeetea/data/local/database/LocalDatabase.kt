package com.gwolf.coffeetea.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gwolf.coffeetea.data.entities.CategoryEntity
import com.gwolf.coffeetea.data.entities.FavoriteEntity
import com.gwolf.coffeetea.data.entities.ProductEntity
import com.gwolf.coffeetea.data.entities.PromotionEntity
import com.gwolf.coffeetea.data.local.database.dao.CategoryDao
import com.gwolf.coffeetea.data.local.database.dao.FavoriteDao
import com.gwolf.coffeetea.data.local.database.dao.ProductDao
import com.gwolf.coffeetea.data.local.database.dao.PromotionDao

@Database(
    entities = [CategoryEntity::class,
        FavoriteEntity::class,
        ProductEntity::class,
        PromotionEntity::class],
    version = 1
)
abstract class LocalDatabase : RoomDatabase() {
    abstract val categoryDao: CategoryDao
    abstract val favoriteDao: FavoriteDao
    abstract val productDao: ProductDao
    abstract val promotionDao: PromotionDao
}