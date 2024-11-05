package com.gwolf.coffeetea.presentation.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gwolf.coffeetea.R
import com.gwolf.coffeetea.ui.theme.PrimaryDarkColor
import com.gwolf.coffeetea.ui.theme.robotoFontFamily

@Composable
fun PromotionsComponent() {
    // temp
    val pagerState = rememberPagerState(
        pageCount = { 8 }
    )
    Box {
        HorizontalPager(
            state = pagerState
        ) { position ->
            PromotionScreen()
        }
        PagerIndicator(pagerState = pagerState)
    }
}

@Composable
private fun BoxScope.PagerIndicator(
    pagerState: PagerState
) {
    Row(
        Modifier
            .wrapContentHeight()
            .align(Alignment.BottomStart)
            .padding(bottom = 32.dp, start = 16.dp),
        horizontalArrangement = Arrangement.Center
    ) {

        repeat(pagerState.pageCount) { iteration ->
            val width by animateDpAsState(targetValue = if (pagerState.currentPage == iteration) 24.dp else 8.dp)
            val color =
                if (pagerState.currentPage == iteration) PrimaryDarkColor else Color.White
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .clip(CircleShape)
                    .background(color)
                    .size(width = width, height = 8.dp)
            )
        }
    }
}

@Composable
private fun PromotionScreen() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(252.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                modifier = Modifier.fillMaxWidth(),
                painter = painterResource(R.drawable.image_promotion_mock),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .padding(start = 36.dp, bottom = 80.dp)
                    .align(Alignment.BottomStart)
            ) {
                Text(
                    modifier = Modifier,
                    text = "Знижка на перше замовлення!",
                    fontFamily = robotoFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = Color.White
                )
                Spacer(Modifier.size(4.dp))
                Text(
                    modifier = Modifier.padding(end = 120.dp),
                    text = "Отримайте знижку 15% на переше замовлення.",
                    fontFamily = robotoFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PromotionScreenPreview() {
    PromotionScreen()
}