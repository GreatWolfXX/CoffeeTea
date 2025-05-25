package com.gwolf.coffeetea.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.gwolf.coffeetea.R
import com.gwolf.coffeetea.domain.entities.CartItem
import com.gwolf.coffeetea.domain.entities.Product
import com.gwolf.coffeetea.ui.theme.GrayAlpha05
import com.gwolf.coffeetea.ui.theme.OnSurfaceColor
import com.gwolf.coffeetea.ui.theme.OutlineColor
import com.gwolf.coffeetea.ui.theme.robotoFontFamily

@Composable
fun OrderProductCard(
    modifier: Modifier = Modifier,
    cartItem: CartItem,
    onClick: (product: Product) -> Unit
) {
    val product = cartItem.product
    val context = LocalContext.current

    Card(
        modifier = modifier
            .height(140.dp)
            .fillMaxWidth()
            .clickable {
                onClick(product)
            },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .height(98.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Absolute.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    AsyncImage(
                        modifier = Modifier
                            .width(116.dp)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(8.dp)),
                        model = product.imageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.size(16.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .wrapContentWidth(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth(0.6f),
                            text = product.name,
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
                                .fillMaxWidth(0.5f),
                            text = product.featuresDescription,
                            fontFamily = robotoFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp,
                            lineHeight = TextUnit(16f, TextUnitType.Sp),
                            color = OutlineColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            modifier = Modifier
                                .fillMaxWidth(0.5f),
                            text = stringResource(R.string.quantity, cartItem.quantity),
                            fontFamily = robotoFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp,
                            lineHeight = TextUnit(16f, TextUnitType.Sp),
                            color = OnSurfaceColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Spacer(Modifier.size(4.dp))
                    }
                }
                Column(
                    modifier = Modifier
                        .wrapContentWidth()
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(
                        modifier = Modifier,
                        text = "${product.amount} ${product.unit}",
                        fontFamily = robotoFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.sp,
                        lineHeight = TextUnit(16f, TextUnitType.Sp),
                        color = OutlineColor
                    )
                    Text(
                        modifier = Modifier,
                        text = "${product.price}₴",
                        fontFamily = robotoFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        lineHeight = TextUnit(24f, TextUnitType.Sp),
                        color = OnSurfaceColor
                    )
                    Spacer(Modifier.size(4.dp))
                }
            }
            Spacer(Modifier.size(4.dp))
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = GrayAlpha05
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier,
                    text = stringResource(R.string.title_cart_price),
                    fontFamily = robotoFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 18.sp,
                    lineHeight = TextUnit(16f, TextUnitType.Sp),
                    color = OnSurfaceColor
                )
                val totalPrice = product.price * cartItem.quantity
                Text(
                    modifier = Modifier,
                    text = "$totalPrice₴",
                    fontFamily = robotoFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    lineHeight = TextUnit(24f, TextUnitType.Sp),
                    color = OnSurfaceColor
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OrderProductCardPreview() {
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
    val cartItem = CartItem(
        id = "",
        product = product,
        quantity = 5
    )

    OrderProductCard(
        cartItem = cartItem,
        onClick = {}
    )
}