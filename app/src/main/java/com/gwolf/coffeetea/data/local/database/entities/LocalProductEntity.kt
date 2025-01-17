package com.gwolf.coffeetea.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gwolf.coffeetea.util.PRODUCTS_TABLE
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = PRODUCTS_TABLE)
data class LocalProductEntity(
    @PrimaryKey(autoGenerate = true)
    val localProductId: Int = 0,
    val id: Int = -1,
    val name: String = "",
    val amount: Double = 0.0,
    val unit: String = "",
    val featuresDescription: String = "",
    val fullDescription: String = "",
    val price: Double = 0.0,
    val rating: Double = 0.0,
    val bucketId: String = "",
    val imagePath: String = "",
    val imageUrl:String = "",
    val categoryId: Int = -1,
)

//@Serializable
//data class ProductWithCartEntity(
//    @Embedded val product: LocalProductEntity,
//    @Relation(
//        entity = LocalCategoryEntity::class,
//        parentColumn = "categoryId",
//        entityColumn = "id"
//    )
//    val category: LocalCategoryEntity
//)