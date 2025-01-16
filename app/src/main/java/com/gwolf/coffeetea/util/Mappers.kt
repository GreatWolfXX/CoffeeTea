package com.gwolf.coffeetea.util

import com.gwolf.coffeetea.data.entities.CartEntity
import com.gwolf.coffeetea.data.entities.CategoryEntity
import com.gwolf.coffeetea.data.entities.FavoriteEntity
import com.gwolf.coffeetea.data.entities.ProductEntity
import com.gwolf.coffeetea.data.entities.ProfileEntity
import com.gwolf.coffeetea.data.entities.PromotionEntity
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
    favoriteId = this.favorite.let {
        if (it.isNotEmpty()) {
            it.first().id
        } else {
            -1
        }
    },
    cartId = this.cart.let {
        if (it.isNotEmpty()) {
            it.first().id
        } else {
            -1
        }
    }
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
    product = product?.toDomain(productImageUrl)!!
)

fun CartEntity.toDomain(productImageUrl: String) = Cart(
    cartId = this.id,
    productId = this.productId,
    quantity = quantity,
    product = product?.toDomain(productImageUrl)!!
)
