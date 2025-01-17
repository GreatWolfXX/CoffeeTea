package com.gwolf.coffeetea.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gwolf.coffeetea.util.CATEGORIES_TABLE
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = CATEGORIES_TABLE)
data class LocalCategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val localCategoryId: Int = 0,
    val id: Int = -1,
    val name: String = "",
    val bucketId: String = "",
    val imagePath: String = "",
    val imageUrl:String = ""
)