package com.gwolf.coffeetea.data

import com.gwolf.coffeetea.data.dto.novapost.NovaPostCityDto
import com.gwolf.coffeetea.data.dto.novapost.NovaPostDepartmentsDto
import com.gwolf.coffeetea.data.dto.supabase.AddressEntity
import com.gwolf.coffeetea.data.dto.supabase.CartItemEntity
import com.gwolf.coffeetea.data.dto.supabase.CategoryEntity
import com.gwolf.coffeetea.data.dto.supabase.FavoriteEntity
import com.gwolf.coffeetea.data.dto.supabase.ProductEntity
import com.gwolf.coffeetea.data.dto.supabase.ProfileEntity
import com.gwolf.coffeetea.data.dto.supabase.PromotionEntity
import com.gwolf.coffeetea.domain.entities.Address
import com.gwolf.coffeetea.domain.entities.CartItem
import com.gwolf.coffeetea.domain.entities.Category
import com.gwolf.coffeetea.domain.entities.City
import com.gwolf.coffeetea.domain.entities.Department
import com.gwolf.coffeetea.domain.entities.Favorite
import com.gwolf.coffeetea.domain.entities.Product
import com.gwolf.coffeetea.domain.entities.Profile
import com.gwolf.coffeetea.domain.entities.Promotion

fun PromotionEntity.toDomain(imageUrl: String = "") = Promotion(
    id = this.id,
    title = this.title,
    description = this.description,
    startDate = this.startDate,
    endDate = this.endDate,
    imageUrl = imageUrl
)

fun CategoryEntity.toDomain(imageUrl: String = "") = Category(
    id = this.id,
    name = this.name,
    imageUrl = imageUrl
)

fun ProductEntity.toDomain(imageUrl: String = "") = Product(
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
    favoriteId = this.favorite.takeIf { it.isNotEmpty() }?.first()?.id.orEmpty(),
    cartItemId = this.cartItem.takeIf { it.isNotEmpty() }?.first()?.id.orEmpty()
)

fun ProfileEntity.toDomain(imageUrl: String = "") = Profile(
    id = this.id,
    firstName = this.firstName,
    lastName = this.lastName,
    patronymic = this.patronymic,
    phone = if(this.phone.isNotEmpty()) "+${this.phone}" else "",
    email = this.email,
    imageUrl = imageUrl
)

fun FavoriteEntity.toDomain(productImageUrl: String = "") = Favorite(
    id = this.id,
    product = this.product?.toDomain(productImageUrl)!!
)

fun CartItemEntity.toDomain(productImageUrl: String = "") = CartItem(
    id = this.id,
    product = product?.toDomain(productImageUrl)!!,
    quantity = this.quantity,
)

fun NovaPostCityDto.toDomain() = City(
    ref = ref,
    name = name
)

fun NovaPostDepartmentsDto.toDomain() = Department(
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