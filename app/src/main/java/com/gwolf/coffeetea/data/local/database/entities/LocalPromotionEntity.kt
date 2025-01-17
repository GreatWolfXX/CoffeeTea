package com.gwolf.coffeetea.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gwolf.coffeetea.util.PROMOTIONS_TABLE
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = PROMOTIONS_TABLE)
data class LocalPromotionEntity(
    @PrimaryKey(autoGenerate = true)
    val localPromotionId: Int = 0,
    val id: Int = -1,
    val title: String = "",
    val description: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val bucketId: String = "",
    val imagePath: String = "",
    val imageUrl:String = ""
)