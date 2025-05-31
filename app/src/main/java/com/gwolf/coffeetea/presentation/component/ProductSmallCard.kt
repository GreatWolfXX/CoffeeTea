package com.gwolf.coffeetea.presentation.component

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalMall
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.gwolf.coffeetea.domain.entities.Product
import com.gwolf.coffeetea.ui.theme.OnSurfaceColor
import com.gwolf.coffeetea.ui.theme.OutlineColor
import com.gwolf.coffeetea.ui.theme.PrimaryDarkColor
import com.gwolf.coffeetea.ui.theme.robotoFontFamily

@Composable
fun ProductSmallCard(
    context: Context,
    modifier: Modifier = Modifier,
    product: Product,
    onClick: () -> Unit,
    onClickBuy: () -> Unit
) {
    Card(
        modifier = modifier
            .height(80.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Absolute.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                AsyncImage(
                    modifier = Modifier
                        .width(96.dp)
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
                            .fillMaxWidth(0.7f),
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
                            .fillMaxWidth(0.6f),
                        text = product.featuresDescription,
                        fontFamily = robotoFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.sp,
                        lineHeight = TextUnit(16f, TextUnitType.Sp),
                        color = OutlineColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.size(4.dp))
                    Row(
                        modifier = Modifier
                            .wrapContentWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier,
                            text = "${product.price}₴",
                            fontFamily = robotoFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            lineHeight = TextUnit(20f, TextUnitType.Sp),
                            color = OnSurfaceColor
                        )
                        Spacer(Modifier.size(16.dp))
                        Text(
                            modifier = Modifier,
                            text = "${product.amount} ${product.unit}",
                            fontFamily = robotoFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp,
                            lineHeight = TextUnit(16f, TextUnitType.Sp),
                            color = OutlineColor
                        )
                    }
                }
            }
            ProductCardBuyBtn(onClickBuy)
        }
    }
}

@Composable
private fun ProductCardBuyBtn(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(end = 16.dp)
            .size(28.dp)
            .background(
                color = PrimaryDarkColor,
                shape = RoundedCornerShape(4.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            imageVector = Icons.Outlined.LocalMall,
            contentDescription = null,
            tint = Color.White
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductSmallCardPreview() {
    val context = LocalContext.current
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

    ProductSmallCard(
        context = context,
        product = product,
        onClick = {},
        onClickBuy = {}
    )
}