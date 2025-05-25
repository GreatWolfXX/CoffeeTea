package com.gwolf.coffeetea.data

import com.gwolf.coffeetea.data.dto.novapost.NovaPostCityDto
import com.gwolf.coffeetea.data.dto.novapost.NovaPostDepartmentsDto
import com.gwolf.coffeetea.data.dto.supabase.AddressDto
import com.gwolf.coffeetea.data.dto.supabase.CartItemDto
import com.gwolf.coffeetea.data.dto.supabase.CategoryDto
import com.gwolf.coffeetea.data.dto.supabase.FavoriteDto
import com.gwolf.coffeetea.data.dto.supabase.ProductDto
import com.gwolf.coffeetea.data.dto.supabase.ProfileDto
import com.gwolf.coffeetea.data.dto.supabase.PromotionDto
import com.gwolf.coffeetea.domain.entities.Address
import com.gwolf.coffeetea.domain.entities.CartItem
import com.gwolf.coffeetea.domain.entities.Category
import com.gwolf.coffeetea.domain.entities.City
import com.gwolf.coffeetea.domain.entities.Department
import com.gwolf.coffeetea.domain.entities.Favorite
import com.gwolf.coffeetea.domain.entities.Product
import com.gwolf.coffeetea.domain.entities.Profile
import com.gwolf.coffeetea.domain.entities.Promotion

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

fun ProfileDto.toDomain(imageUrl: String) = Profile(
    id = this.id,
    firstName = this.firstName,
    lastName = this.lastName,
    patronymic = this.patronymic,
    phone = if (this.phone.isNotEmpty()) "+${this.phone}" else "",
    email = this.email,
    imageUrl = imageUrl
)

fun FavoriteDto.toDomain(imageUrl: String) = Favorite(
    id = this.id,
    product = this.product?.toDomain(imageUrl)!!
)

fun CartItemDto.toDomain(imageUrl: String) = CartItem(
    id = this.id,
    product = product?.toDomain(imageUrl)!!,
    quantity = this.quantity,
)

fun NovaPostCityDto.toDomain() = City(
    ref = ref,
    name = name
)

fun List<NovaPostCityDto>.toDomain() = this.map { it.toDomain() }

fun NovaPostDepartmentsDto.toDomain() = Department(
    ref = ref,
    name = name
)

fun List<NovaPostDepartmentsDto>.toDomain() = this.map { it.toDomain() }

fun AddressDto.toDomain() = Address(
    id = id,
    userId = userId,
    deliveryType = deliveryType,
    refCity = refCity,
    refAddress = refAddress,
    city = city,
    address = address,
    isDefault = isDefault
)

fun List<AddressDto>.toDomain() = this.map { it.toDomain() }