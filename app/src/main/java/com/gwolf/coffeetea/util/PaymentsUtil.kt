package com.gwolf.coffeetea.util

import com.gwolf.coffeetea.domain.entities.CartItem
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber

private val baseRequest = JSONObject()
    .put("apiVersion", 2)
    .put("apiVersionMinor", 0)

private val gatewayTokenizationSpecification: JSONObject =
    JSONObject()
        .put("type", "PAYMENT_GATEWAY")
        .put("parameters", JSONObject(PAYMENT_GATEWAY_TOKENIZATION_PARAMETERS))

private val allowedCardNetworks = JSONArray(SUPPORTED_NETWORKS)

private val allowedCardAuthMethods = JSONArray(SUPPORTED_METHODS)

// Optionally, you can add billing address/phone number associated with a CARD payment method.
private fun baseCardPaymentMethod(): JSONObject =
    JSONObject()
        .put("type", "CARD")
        .put(
            "parameters", JSONObject()
                .put("allowedAuthMethods", allowedCardAuthMethods)
                .put("allowedCardNetworks", allowedCardNetworks)
                .put("billingAddressRequired", true)
                .put(
                    "billingAddressParameters", JSONObject()
                        .put("format", "FULL")
                )
        )

private val cardPaymentMethod: JSONObject = baseCardPaymentMethod()
    .put("tokenizationSpecification", gatewayTokenizationSpecification)

val allowedPaymentMethods: JSONArray = JSONArray().put(cardPaymentMethod)

fun isReadyToPayRequest(): JSONObject? =
    try {
        baseRequest
            .put("allowedPaymentMethods", JSONArray().put(baseCardPaymentMethod()))
    } catch (e: JSONException) {
        null
    }

private val merchantInfo: JSONObject =
    JSONObject().put("merchantName", "Example Merchant")

private fun getTransactionInfo(cartItems: List<CartItem>): JSONObject {
    val totalPrice = cartItems.sumOf { it.product.price * it.quantity }

    val displayItems = JSONArray().apply {
        cartItems.forEach { item ->
            val label = "${item.product.name} ${item.product.amount} ${item.product.unit} x${item.quantity}"
            val price = item.product.price * item.quantity
            Timber.d("Google Pay product order label: $label")
            put(JSONObject().apply {
                put("label", label)
                put("type", "LINE_ITEM")
                put("price", "$price")
            })
        }
    }

    return JSONObject()
        .put("displayItems", displayItems)
        .put("countryCode", COUNTRY_CODE)
        .put("currencyCode", CURRENCY_CODE)
        .put("totalPriceStatus", "FINAL")
        .put("totalPrice", "$totalPrice")
        .put("totalPriceLabel", "Total")
}


fun getPaymentDataRequest(products: List<CartItem>): JSONObject =
    baseRequest
        .put("allowedPaymentMethods", allowedPaymentMethods)
        .put("transactionInfo", getTransactionInfo(products))
        .put("merchantInfo", merchantInfo)
//            .put("shippingAddressRequired", false)
//            .put("shippingAddressParameters", JSONObject()
//                    .put("phoneNumberRequired", false)
//                    .put("allowedCountryCodes", JSONArray(listOf("US", "GB")))
//            )
