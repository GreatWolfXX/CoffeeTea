package com.gwolf.coffeetea.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gwolf.coffeetea.data.local.database.dao.CategoryDao
import com.gwolf.coffeetea.data.local.database.dao.FavoriteDao
import com.gwolf.coffeetea.data.local.database.dao.ProductDao
import com.gwolf.coffeetea.data.local.database.dao.PromotionDao
import com.gwolf.coffeetea.data.local.database.entities.LocalCategoryEntity
import com.gwolf.coffeetea.data.local.database.entities.LocalFavoriteEntity
import com.gwolf.coffeetea.data.local.database.entities.LocalProductEntity
import com.gwolf.coffeetea.data.local.database.entities.LocalPromotionEntity

@Database(
    entities = [LocalCategoryEntity::class,
        LocalFavoriteEntity::class,
        LocalProductEntity::class,
        LocalPromotionEntity::class],
    version = 1
)
abstract class LocalDatabase : RoomDatabase() {
    abstract val categoryDao: CategoryDao
    abstract val favoriteDao: FavoriteDao
    abstract val productDao: ProductDao
    abstract val promotionDao: PromotionDao
}