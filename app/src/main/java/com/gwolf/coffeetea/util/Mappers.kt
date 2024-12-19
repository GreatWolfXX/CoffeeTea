package com.gwolf.coffeetea.util

import com.gwolf.coffeetea.data.dto.CartDto
import com.gwolf.coffeetea.data.dto.CategoryDto
import com.gwolf.coffeetea.data.dto.FavoriteDto
import com.gwolf.coffeetea.data.dto.ProductDto
import com.gwolf.coffeetea.data.dto.ProfileDto
import com.gwolf.coffeetea.data.dto.PromotionDto
import com.gwolf.coffeetea.domain.model.Cart
import com.gwolf.coffeetea.domain.model.Category
import com.gwolf.coffeetea.domain.model.Favorite
import com.gwolf.coffeetea.domain.model.Product
import com.gwolf.coffeetea.domain.model.Profile
import com.gwolf.coffeetea.domain.model.Promotion
import java.util.UUID

fun PromotionDto.toDomain(imageUrl: String) = Promotion(
    id = this.id,
    title = this.title,
    description = this.description,
    startDate = this.startDate,
    endDate = this.endDate,
    imageUrl = imageUrl
)

fun CategoryDto.toDomain(imageUrl: String) = Category(
    id = this.id,
    name = this.name,
    imageUrl = imageUrl
)

fun ProductDto.toDomain(imageUrl: String) = Product(
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

fun ProfileDto.toDomain(imageUrl: String) = Profile(
    id = UUID.fromString(this.id),
    name = this.name,
    email = this.email,
    bucketId = this.bucketId,
    imageUrl = imageUrl
)

fun FavoriteDto.toDomain(productImageUrl: String) = Favorite(
    id = this.id,
    product = product?.toDomain(productImageUrl)!!
)

fun CartDto.toDomain(productImageUrl: String) = Cart(
    cartId = this.id,
    productId = this.productId,
    quantity = quantity,
    product = product?.toDomain(productImageUrl)!!
)
