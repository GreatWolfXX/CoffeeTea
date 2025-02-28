package com.gwolf.coffeetea.util

import com.gwolf.coffeetea.data.entities.novapost.NovaPostCityEntity
import com.gwolf.coffeetea.data.entities.novapost.NovaPostDepartmentsEntity
import com.gwolf.coffeetea.data.entities.supabase.AddressEntity
import com.gwolf.coffeetea.data.entities.supabase.CartEntity
import com.gwolf.coffeetea.data.entities.supabase.CategoryEntity
import com.gwolf.coffeetea.data.entities.supabase.FavoriteEntity
import com.gwolf.coffeetea.data.entities.supabase.ProductEntity
import com.gwolf.coffeetea.data.entities.supabase.ProfileEntity
import com.gwolf.coffeetea.data.entities.supabase.PromotionEntity
import com.gwolf.coffeetea.domain.model.Address
import com.gwolf.coffeetea.domain.model.Cart
import com.gwolf.coffeetea.domain.model.Category
import com.gwolf.coffeetea.domain.model.City
import com.gwolf.coffeetea.domain.model.Department
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
    stockQuantity = this.stockQuantity,
    amount = this.amount,
    unit = this.unit,
    featuresDescription = this.featuresDescription,
    fullDescription = this.fullDescription,
    price = this.price,
    rating = this.rating,
    imageUrl = imageUrl,
    categoryName = this.category?.name.orEmpty(),
    favoriteId = this.favorite.firstOrNull()?.id.orEmpty(),
    cartId = this.cart.firstOrNull()?.id.orEmpty()
)

fun ProfileEntity.toDomain(imageUrl: String) = Profile(
    id = UUID.fromString(this.id),
    firstName = this.firstName,
    lastName = this.lastName,
    patronymic = this.patronymic,
    phone = if(this.phone.isNotEmpty()) "+${this.phone}" else "",
    email = this.email,
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

fun NovaPostCityEntity.toDomain() = City(
    ref = ref,
    name = name
)

fun NovaPostDepartmentsEntity.toDomain() = Department(
    ref = ref,
    name = name
)

fun AddressEntity.toDomain() = Address(
    id = id,
    userId = userId,
    deliveryType = deliveryType,
    refCity = refCity,
    refAddress = refAddress,
    city = city,
    address = address,
    isDefault = isDefault
)