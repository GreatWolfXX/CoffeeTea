package com.gwolf.coffeetea.util

import com.gwolf.coffeetea.data.dto.CategoryDto
import com.gwolf.coffeetea.data.dto.ProductDto
import com.gwolf.coffeetea.data.dto.ProfileDto
import com.gwolf.coffeetea.data.dto.PromotionDto
import com.gwolf.coffeetea.domain.model.Category
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

fun CategoryDto.toDomain() = Category(
    id = this.id,
    name = this.name
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
    imageUrl = imageUrl
)

fun ProfileDto.toDomain(imageUrl: String) = Profile(
    id = UUID.fromString(this.id),
    name = this.name,
    email = this.email,
    imageUrl = imageUrl
)