package com.gwolf.coffeetea.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Receipt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gwolf.coffeetea.R
import com.gwolf.coffeetea.domain.entities.Address
import com.gwolf.coffeetea.domain.entities.Order
import com.gwolf.coffeetea.domain.entities.OrderItem
import com.gwolf.coffeetea.domain.entities.Product
import com.gwolf.coffeetea.ui.theme.OnSurfaceColor
import com.gwolf.coffeetea.ui.theme.robotoFontFamily
import com.gwolf.coffeetea.util.DateTimeUtils

@Composable
fun OrderComponent(
    order: Order
) {
    var isOpened by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .clickable {
                isOpened = !isOpened
            },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .height(120.dp)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .size(48.dp),
                    imageVector = Icons.Rounded.Receipt,
                    tint = OnSurfaceColor,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.size(8.dp))
                Column(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = stringResource(R.string.order_number, order.orderNumber),
                        fontFamily = robotoFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        lineHeight = TextUnit(20f, TextUnitType.Sp),
                        color = OnSurfaceColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    val address = "${order.address.city}, ${order.address.address}"
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = stringResource(R.string.order_address, address),
                        fontFamily = robotoFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        lineHeight = TextUnit(20f, TextUnitType.Sp),
                        color = OnSurfaceColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = stringResource(R.string.order_price, order.totalPrice),
                        fontFamily = robotoFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        lineHeight = TextUnit(20f, TextUnitType.Sp),
                        color = OnSurfaceColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    val date = DateTimeUtils.formatDateTimeFromString(order.createdAt)
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = stringResource(R.string.order_date, date),
                        fontFamily = robotoFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        lineHeight = TextUnit(20f, TextUnitType.Sp),
                        color = OnSurfaceColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            val icon =
                if (isOpened) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown
            Icon(
                imageVector = icon,
                tint = OnSurfaceColor,
                contentDescription = null
            )
        }
        AnimatedVisibility(isOpened) {
            ListOderItem(order.orderItems)
        }
    }
}

@Composable
private fun ListOderItem(
    list: List<OrderItem>
) {
    LazyColumn(
        modifier = Modifier
            .heightIn(max = 312.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        items(list) { orderItem ->
            OrderProductCard(
                modifier = Modifier.animateItem(),
                product = orderItem.product,
                quantity = orderItem.quantity,
                onClick = {}
            )
        }
    }
}

@Preview
@Composable
fun PreviewOrderComponent() {

    val product = Product(
        id = "",
        name = "Coffee",
        stockQuantity = 10,
        amount = "10.0",
        unit = "kg",
        featuresDescription = "featuresDescription",
        fullDescription = "fullDescription",
        price = 1099.99,
        rating = 0.0,
        imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTnRlkHl_qadBAMBqFScSWT-C_xhIgZPjlMxQ&s",
        categoryName = "",
        favoriteId = "",
        cartItemId = ""
    )

    val orderItems = listOf(
        OrderItem(
            orderId = "",
            productId = "",
            product = product,
            quantity = 2
        )
    )

    val address = Address(
        id = "",
        userId = "",
        deliveryType = "",
        refCity = "",
        refAddress = "",
        city = "Boryslav",
        address = "Відділення №3",
        isDefault = false
    )

    val order = Order(
        orderNumber = 1254,
        userId = "",
        totalPrice = 900.00,
        createdAt = "2025-05-31T10:38:28+00:00",
        orderItems = orderItems,
        address = address
    )

    OrderComponent(
        order = order
    )
}