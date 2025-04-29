package com.gwolf.coffeetea.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.gwolf.coffeetea.R
import com.gwolf.coffeetea.domain.entities.Cart
import com.gwolf.coffeetea.domain.entities.Product
import com.gwolf.coffeetea.ui.theme.LightRedColor
import com.gwolf.coffeetea.ui.theme.OnSurfaceColor
import com.gwolf.coffeetea.ui.theme.OutlineColor
import com.gwolf.coffeetea.ui.theme.PrimaryDarkColor
import com.gwolf.coffeetea.ui.theme.robotoFontFamily
import kotlinx.coroutines.delay

@Composable
fun CartProductCard(
    modifier: Modifier = Modifier,
    cart: Cart,
    onClickDelete: () -> Unit,
    onClick: (product: Product) -> Unit,
    saveQuantity: (quantity: Int) -> Unit
) {
    var quantity by rememberSaveable { mutableIntStateOf(cart.quantity) }
    var lastSentQuantity by rememberSaveable { mutableIntStateOf(cart.quantity) }
    val product = cart.product
    val context = LocalContext.current

    LaunchedEffect(quantity) {
        delay(1000)
        if (quantity != lastSentQuantity) {
            saveQuantity(quantity)
            lastSentQuantity = quantity
        }
    }

    Card(
        modifier = modifier
            .height(98.dp)
            .fillMaxWidth()
            .clickable {
                onClick.invoke(product)
            },
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
                        .width(116.dp)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(8.dp)),
                    model = ImageRequest.Builder(context)
                        .data(product.imageUrl)
                        .crossfade(true)
                        .build(),
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
                    CartProductChangeCount(
                        onAdd = {
                            quantity = quantity.inc()
                        },
                        onMinus = {
                            quantity = quantity.dec()
                        },
                        stockQuantity = cart.product.stockQuantity,
                        quantity = quantity
                    )
                }
            }
            Column(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(end = 8.dp),
                horizontalAlignment = Alignment.End
            ) {
                CartProductDeleteBtn {
                    onClickDelete.invoke()
                }
                Spacer(Modifier.size(10.dp))
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
            }
        }
    }
}

@Composable
private fun CartProductDeleteBtn(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .background(
                color = LightRedColor,
                shape = RoundedCornerShape(4.dp)
            )
            .clickable {
                onClick.invoke()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            imageVector = ImageVector.vectorResource(R.drawable.delete_ic),
            contentDescription = null,
            tint = Color.White
        )
    }
}

@Composable
private fun CartProductChangeCount(
    onAdd: () -> Unit,
    onMinus: () -> Unit,
    stockQuantity: Int,
    quantity: Int
) {
    Row(
        modifier = Modifier.wrapContentWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val enabledMinus = quantity > 1
        val enabledAdd = quantity < stockQuantity
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(
                    color = if (enabledMinus) PrimaryDarkColor else Color.Transparent,
                    shape = RoundedCornerShape(4.dp)
                )
                .border(
                    1.dp,
                    color = if (enabledMinus) Color.Transparent else PrimaryDarkColor,
                    shape = RoundedCornerShape(4.dp)
                )
                .clickable {
                    if (enabledMinus) {
                        onMinus.invoke()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(20.dp),
                imageVector = Icons.Outlined.Remove,
                contentDescription = null,
                tint = if (enabledMinus) Color.White else PrimaryDarkColor
            )
        }
        Spacer(Modifier.size(14.dp))
        Text(
            modifier = Modifier,
            text = quantity.toString(),
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = OnSurfaceColor
        )
        Spacer(Modifier.size(14.dp))
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(
                    color = if (enabledAdd) PrimaryDarkColor else Color.Transparent,
                    shape = RoundedCornerShape(4.dp)
                )
                .border(
                    1.dp,
                    color = if (enabledAdd) Color.Transparent else PrimaryDarkColor,
                    shape = RoundedCornerShape(4.dp)
                )
                .clickable {
                    if (enabledAdd) {
                        onAdd.invoke()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(20.dp),
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = if (enabledAdd) Color.White else PrimaryDarkColor
            )
        }
    }
}
