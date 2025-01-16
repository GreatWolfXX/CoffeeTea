package com.gwolf.coffeetea.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.gwolf.coffeetea.R
import com.gwolf.coffeetea.domain.model.Product
import com.gwolf.coffeetea.ui.theme.OnSurfaceColor
import com.gwolf.coffeetea.ui.theme.OutlineColor
import com.gwolf.coffeetea.ui.theme.PrimaryDarkColor
import com.gwolf.coffeetea.ui.theme.robotoFontFamily

@Composable
fun ProductCard(
    modifier: Modifier = Modifier,
    product: Product,
    onClick: () -> Unit,
    onClickBuy: () -> Unit,
    onClickToCart: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
            .clickable {
                onClick.invoke()
            },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Box {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                AsyncImage(
                    modifier = Modifier
                        .height(130.dp)
                        .fillMaxWidth(),
                    model = ImageRequest.Builder(context)
                        .data(product.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.size(4.dp))
                Text(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    text = product.name,
                    fontFamily = robotoFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = OnSurfaceColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.size(4.dp))
                Text(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    text = product.featuresDescription,
                    fontFamily = robotoFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp,
                    color = OutlineColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.size(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if(product.cartId < 0) {
                        Column {
                            Text(
                                modifier = Modifier,
                                text = "${product.price}₴",
                                fontFamily = robotoFontFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                lineHeight = TextUnit(20f, TextUnitType.Sp),
                                color = OnSurfaceColor
                            )
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
                        Spacer(Modifier.size(16.dp))
                        ProductCardBuyBtn {
                            onClickBuy.invoke()
                        }
                    } else {
                        ProductCardToCartBtn {
                            onClickToCart.invoke()
                        }
                    }
                }
                Spacer(Modifier.size(4.dp))
            }
//            Icon(
//                modifier = Modifier
//                    .padding(8.dp)
//                    .align(Alignment.TopEnd)
//                    .size(20.dp),
//                imageVector = if(product.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
//                contentDescription = null,
//                tint = if(product.isFavorite) LightRedColor else Color.White
//            )
        }
    }
}

@Composable
private fun ProductCardBuyBtn(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .background(
                color = PrimaryDarkColor,
                shape = RoundedCornerShape(4.dp)
            )
            .clickable {
                onClick.invoke()
            },
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

@Composable
private fun ProductCardToCartBtn(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = PrimaryDarkColor,
                shape = RoundedCornerShape(4.dp)
            )
            .clickable {
                onClick.invoke()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier
                .padding(8.dp),
            text = stringResource(R.string.title_to_cart),
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            lineHeight = TextUnit(16f, TextUnitType.Sp),
            color = Color.White
        )
    }
}

//@Composable
//private fun ProductCardChangeCount(
//    onAdd: () -> Unit,
//    onMinus: () -> Unit,
//    count: Int
//) {
//    Row(
//        modifier = Modifier.fillMaxWidth(),
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.SpaceBetween
//    ) {
//        Box(
//            modifier = Modifier
//                .size(28.dp)
//                .background(
//                    color = PrimaryDarkColor,
//                    shape = RoundedCornerShape(4.dp)
//                )
//                .clickable {
//                    onMinus.invoke()
//                },
//            contentAlignment = Alignment.Center
//        ) {
//            Icon(
//                modifier = Modifier.size(20.dp),
//                imageVector = Icons.Outlined.Remove,
//                contentDescription = null,
//                tint = Color.White
//            )
//        }
//        Text(
//            modifier = Modifier,
//            text = count.toString(),
//            fontFamily = robotoFontFamily,
//            fontWeight = FontWeight.Medium,
//            fontSize = 14.sp,
//            color = OnSurfaceColor
//        )
//        Box(
//            modifier = Modifier
//                .size(28.dp)
//                .background(
//                    color = PrimaryDarkColor,
//                    shape = RoundedCornerShape(4.dp)
//                )
//                .clickable {
//                    onAdd.invoke()
//                },
//            contentAlignment = Alignment.Center
//        ) {
//            Icon(
//                modifier = Modifier.size(20.dp),
//                imageVector = Icons.Default.Add,
//                contentDescription = null,
//                tint = Color.White
//            )
//        }
//    }
//}

//@Preview(showBackground = true)
//@Composable
//private fun ProductCardPreview() {
//    ProductCard()
//}