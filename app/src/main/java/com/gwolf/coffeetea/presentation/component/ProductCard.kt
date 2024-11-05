package com.gwolf.coffeetea.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.LocalMall
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gwolf.coffeetea.R
import com.gwolf.coffeetea.ui.theme.OnSurfaceColor
import com.gwolf.coffeetea.ui.theme.OutlineColor
import com.gwolf.coffeetea.ui.theme.PrimaryDarkColor
import com.gwolf.coffeetea.ui.theme.robotoFontFamily

@Composable
fun ProductCard() {
    Card(
        modifier = Modifier
            .size(
                width = 180.dp,
                height = 220.dp
            ),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // image
                Image(
                    modifier = Modifier.fillMaxWidth(),
                    painter = painterResource(R.drawable.image_product_mock),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.size(4.dp))
                // title
                Text(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    text = "Кенія Kiambu АА Kirura",
                    fontFamily = robotoFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = OnSurfaceColor
                )
                Spacer(Modifier.size(4.dp))
                // features desc
                Text(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    text = "Фрукти, Цитрусові, Ягоди",
                    fontFamily = robotoFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp,
                    color = OutlineColor
                )
                Spacer(Modifier.size(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        // price
                        Text(
                            modifier = Modifier,
                            text = "667₴",
                            fontFamily = robotoFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = OnSurfaceColor
                        )
                        // amount
                        Text(
                            modifier = Modifier,
                            text = "0.5 кг",
                            fontFamily = robotoFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp,
                            color = OutlineColor
                        )
                    }
                    Spacer(Modifier.size(16.dp))
                    // buy btn
                    if(true) {
                        ProductCardBuyBtn()
                    } else {
                        ProductCardChangeCount()
                    }
                }
                Spacer(Modifier.size(4.dp))
            }
            Icon(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.TopEnd)
                    .size(20.dp),
                imageVector = Icons.Default.FavoriteBorder,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

@Composable
private fun ProductCardBuyBtn() {
    Box(
        modifier = Modifier
            .size(28.dp)
            .background(
                color = PrimaryDarkColor,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable {
                //click
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
private fun ProductCardChangeCount() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(
                    color = PrimaryDarkColor,
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable {
                    //click
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(20.dp),
                imageVector = Icons.Outlined.Remove,
                contentDescription = null,
                tint = Color.White
            )
        }
        Text(
            modifier = Modifier,
            text = "1",
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = OnSurfaceColor
        )
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(
                    color = PrimaryDarkColor,
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable {
                    //click
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(20.dp),
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductCardPreview() {
    ProductCard()
}