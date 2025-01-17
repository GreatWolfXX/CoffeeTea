package com.gwolf.coffeetea.util

import com.gwolf.coffeetea.data.entities.CartEntity
import com.gwolf.coffeetea.data.entities.CategoryEntity
import com.gwolf.coffeetea.data.entities.FavoriteEntity
import com.gwolf.coffeetea.data.entities.ProductEntity
import com.gwolf.coffeetea.data.entities.ProfileEntity
import com.gwolf.coffeetea.data.entities.PromotionEntity
import com.gwolf.coffeetea.data.local.database.entities.FavoriteWithProductEntity
import com.gwolf.coffeetea.data.local.database.entities.LocalCategoryEntity
import com.gwolf.coffeetea.data.local.database.entities.LocalFavoriteEntity
import com.gwolf.coffeetea.data.local.database.entities.LocalProductEntity
import com.gwolf.coffeetea.data.local.database.entities.LocalPromotionEntity
import com.gwolf.coffeetea.domain.model.Cart
import com.gwolf.coffeetea.domain.model.Category
import com.gwolf.coffeetea.domain.model.Favorite
import com.gwolf.coffeetea.domain.model.Product
import com.gwolf.coffeetea.domain.model.Profile
import com.gwolf.coffeetea.domain.model.Promotion
import java.util.UUID

fun PromotionEntity.toDomain(imageUrl: String) = Promotion(
    id = this.id,
    title = this.title,
    description = this.description,
    startDate = this.startDate,
    endDate = this.endDate,
    imageUrl = imageUrl
)

fun CategoryEntity.toDomain(imageUrl: String) = Category(
    id = this.id,
    name = this.name,
    imageUrl = imageUrl
)

fun ProductEntity.toDomain(imageUrl: String) = Product(
    id = this.id,
    name = this.name,
    amount = this.amount,
    unit = this.unit,
    featuresDescription = this.featuresDescription,
    fullDescription = this.fullDescription,
    price = this.price,
    rating = this.rating,
    imageUrl = imageUrl,
    categoryName = this.category?.name.orEmpty(),
    favoriteId = this.favorite.firstOrNull()?.id.let { "" },
    cartId = this.cart.firstOrNull()?.id.let { "" }
)

fun ProfileEntity.toDomain(imageUrl: String) = Profile(
    id = UUID.fromString(this.id),
    name = this.name,
    email = this.email,
    bucketId = this.bucketId,
    imageUrl = imageUrl
)

fun FavoriteEntity.toDomain(productImageUrl: String) = Favorite(
    id = this.id,
    productId = this.productId,
    product = this.product?.toDomain(productImageUrl)!!
)

fun CartEntity.toDomain(productImageUrl: String) = Cart(
    cartId = this.id,
    productId = this.productId,
    quantity = this.quantity,
    product = product?.toDomain(productImageUrl)!!
)

fun CategoryEntity.toLocalEntity(imageUrl: String) = LocalCategoryEntity(
    id = this.id,
    name = this.name,
    bucketId = this.bucketId,
    imagePath = this.imagePath,
    imageUrl = imageUrl
)

fun FavoriteEntity.toLocalEntity(imageUrl: String) = LocalFavoriteEntity(
    id = this.id,
    productId = this.productId,
    userId = this.userId,
    imageUrl = imageUrl
)

fun ProductEntity.toLocalEntity(imageUrl: String) = LocalProductEntity(
    id = this.id,
    name = this.name,
    amount = this.amount,
    unit = this.unit,
    featuresDescription = this.featuresDescription,
    fullDescription = this.fullDescription,
    price = this.price,
    rating = this.rating,
    bucketId = this.bucketId,
    imagePath = this.imagePath,
    imageUrl = imageUrl
)

fun PromotionEntity.toLocalEntity(imageUrl: String) = LocalPromotionEntity(
    id = this.id,
    title = this.title,
    description = this.description,
    startDate = this.startDate,
    endDate = this.endDate,
    bucketId = this.bucketId,
    imagePath = this.imagePath,
    imageUrl = imageUrl
)

fun LocalCategoryEntity.toEntity() = CategoryEntity(
    id = this.id,
    name = this.name,
    bucketId = this.bucketId,
    imagePath = this.imagePath,
)

fun FavoriteWithProductEntity.toEntity() = FavoriteEntity(
    id = this.favorite.id,
    productId = this.product.id,
    userId = this.favorite.userId,
    product = this.product.toEntity()
)

fun LocalProductEntity.toEntity() = ProductEntity(
    id = this.id,
    name = this.name,
    amount = this.amount,
    unit = this.unit,
    featuresDescription = this.featuresDescription,
    fullDescription = this.fullDescription,
    price = this.price,
    rating = this.rating,
    bucketId = this.bucketId,
    imagePath = this.imagePath,
    categoryId = this.categoryId
)

fun LocalPromotionEntity.toEntity() = PromotionEntity(
    id = this.id,
    title = this.title,
    description = this.description,
    startDate = this.startDate,
    endDate = this.endDate,
    bucketId = this.bucketId,
    imagePath = this.imagePath,
)

//fun ProductWithCategoryEntity.toEntity() = ProductEntity(
//    id = this.product.id,
//    name = this.product.name,
//    amount = this.product.amount,
//    unit = this.product.unit,
//    featuresDescription = this.product.featuresDescription,
//    fullDescription = this.product.fullDescription,
//    price = this.product.price,
//    rating = this.product.rating,
//    bucketId = this.product.bucketId,
//    imagePath = this.product.imagePath,
//    categoryId = this.product.categoryId,
//    cart = listOf(this.category)
//)