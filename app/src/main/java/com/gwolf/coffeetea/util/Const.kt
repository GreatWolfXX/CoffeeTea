package com.gwolf.coffeetea.util

import com.google.android.gms.wallet.WalletConstants

const val NOVA_POST_API = "https://api.novaposhta.ua/v2.0/json/"
const val NOVA_POST_DEPARTMENT_REF = "841339c7-591a-42e2-8233-7a0a00f0ed6f"
const val NOVA_POST_CARGO_DEPARTMENT_REF = "9a68df70-0267-42a8-bb5c-37f427e36ee4"
const val NOVA_POST_CABINE_REF = "f9316480-5f2d-425d-bc2c-ac7cd29decf0"
const val NOVA_POST_ADDRESS_MODEL = "AddressGeneral"
const val NOVA_POST_GET_CITIES = "getCities"
const val NOVA_POST_GET_WAREHOUSES = "getWarehouses"
const val DELIVERY_ADDRESS_TYPE_NOVA_POST_DEPARTMENT = "NovaPostDepartment"
const val DELIVERY_ADDRESS_TYPE_NOVA_POST_CABIN = "NovaPostCabin"
const val DELIVERY_ADDRESS_TYPE_UKRPOSHTA = "Ukrposhta"

const val MAX_PRODUCT_COUNT = 100
const val MIN_PASSWORD_LENGTH = 8
const val HOURS_EXPIRES_IMAGE_URL = 5
const val DAYS_EXPIRES_IMAGE_URL = 30
const val MAX_SEARCH_LIST_RESULT = 5L
const val ADD_TO_CART_COUNT = 1
const val PROFILE_IMAGE_QUALITY = 10 // 1 - 100
const val PRODUCT_ADD_CART_QUANTITY = 1

const val PROFILES_BUCKET_ID = "profiles"
const val USERS_TABLE = "users"
const val PROFILES_TABLE = "user_profiles"
const val PRODUCTS_TABLE = "products"
const val FAVORITES_TABLE = "favorites"
const val PROMOTIONS_TABLE = "promotions"
const val CATEGORIES_TABLE = "categories"
const val DELIVERY_ADDRESSES_TABLE = "delivery_addresses"
const val NOTIFICATIONS_TABLE = "notifications"
const val CART_ITEMS_TABLE = "cart_items"
const val ORDERS_TABLE = "orders"
const val ORDER_ITEMS_TABLE = "order_items"
const val PROFILE_USER_IMAGE = "profile_user_image_"
const val PNG_FORMAT = ".png"
const val UKRAINE_PHONE_CODE = "+38"


// Google Pay Constants
const val PAYMENTS_ENVIRONMENT = WalletConstants.ENVIRONMENT_TEST

val SUPPORTED_NETWORKS = listOf(
    "MASTERCARD",
    "VISA")

val SUPPORTED_METHODS = listOf(
    "PAN_ONLY",
    "CRYPTOGRAM_3DS")

const val COUNTRY_CODE = "UA"

const val CURRENCY_CODE = "UAH"

private const val PAYMENT_GATEWAY_TOKENIZATION_NAME = "example"

val PAYMENT_GATEWAY_TOKENIZATION_PARAMETERS = mapOf(
    "gateway" to PAYMENT_GATEWAY_TOKENIZATION_NAME,
    "gatewayMerchantId" to "exampleGatewayMerchantId"
)

//const val DIRECT_TOKENIZATION_PUBLIC_KEY = "REPLACE_ME"

//val DIRECT_TOKENIZATION_PARAMETERS = mapOf(
//    "protocolVersion" to "ECv1",
//    "publicKey" to DIRECT_TOKENIZATION_PUBLIC_KEY
//)