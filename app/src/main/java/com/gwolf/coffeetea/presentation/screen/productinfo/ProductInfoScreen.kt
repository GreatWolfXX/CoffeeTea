package com.gwolf.coffeetea.presentation.screen.productinfo

import android.util.Log
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardBackspace
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.gwolf.coffeetea.R
import com.gwolf.coffeetea.presentation.component.CustomButton
import com.gwolf.coffeetea.presentation.component.LoadingComponent
import com.gwolf.coffeetea.ui.theme.BackgroundGradient
import com.gwolf.coffeetea.ui.theme.LightRedColor
import com.gwolf.coffeetea.ui.theme.OnSurfaceColor
import com.gwolf.coffeetea.ui.theme.OutlineColor
import com.gwolf.coffeetea.ui.theme.PrimaryColor
import com.gwolf.coffeetea.ui.theme.robotoFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductInfoScreen(
    navController: NavController,
    viewModel: ProductInfoViewModel = hiltViewModel()
) {
    val state by viewModel.productInfoScreenState

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGradient)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopAppBar(
                modifier = Modifier.padding(horizontal = 16.dp),
                title = {
                    Text(
                        modifier = Modifier.padding(start = 4.dp),
                        text = state.product?.category?.name.orEmpty(),
                        fontFamily = robotoFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 22.sp,
                        color = OnSurfaceColor
                    )
                },
                navigationIcon = {
                    Icon(
                        modifier = Modifier
                            .clickable {
                                navController.popBackStack()
                            },
                        imageVector = Icons.AutoMirrored.Filled.KeyboardBackspace,
                        contentDescription = null,
                        tint = OnSurfaceColor
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
            if (state.error != null) {
                Log.d("Coffee&TeaLogger", "Error: ${state.error}")
            } else {
                ProductInfoScreenContent(
                    navController = navController,
                    state = state
                )
            }
        }
    }
    LoadingComponent(state.isLoading)
}

@Composable
private fun ProductInfoScreenContent(
    navController: NavController,
    state: ProductInfoUiState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(bottom = 20.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
        ) {
            Image(
                modifier = Modifier
                    .height(348.dp)
                    .wrapContentWidth()
                    .clip(RoundedCornerShape(8.dp)),
                painter = rememberAsyncImagePainter(
                    model = state.product?.imageUrl
                ),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.size(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier,
                    text = state.product?.name.orEmpty(),
                    fontFamily = robotoFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 28.sp,
                    lineHeight = TextUnit(36f, TextUnitType.Sp),
                    color = OnSurfaceColor
                )
                Log.d("LLOGG", "test ${state.product?.isFavorite}")
                Icon(
                    modifier = Modifier.padding(start = 4.dp, end = 4.dp),
                    imageVector = if(state.product?.isFavorite == true) Icons.Default.Favorite else Icons.Default.FavoriteBorder ,
                    contentDescription = null,
                    tint = if(state.product?.isFavorite == true) LightRedColor else OnSurfaceColor
                )
            }
            Spacer(modifier = Modifier.size(8.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier,
                    text = state.product?.featuresDescription.orEmpty(),
                    fontFamily = robotoFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    lineHeight = TextUnit(20f, TextUnitType.Sp),
                    color = OutlineColor
                )
                Spacer(modifier = Modifier.size(16.dp))
                Icon(
                    modifier = Modifier,
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = PrimaryColor
                )
                Spacer(modifier = Modifier.size(2.dp))
                if(state.product?.rating != null) {
                    Text(
                        modifier = Modifier,
                        text = state.product.rating.toString(),
                        fontFamily = robotoFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        lineHeight = TextUnit(20f, TextUnitType.Sp),
                        color = OutlineColor
                    )
                } else {
                    Text(
                        modifier = Modifier,
                        text = stringResource(R.string.title_no_reviews),
                        fontFamily = robotoFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        lineHeight = TextUnit(20f, TextUnitType.Sp),
                        color = OutlineColor
                    )
                }
            }
            Spacer(modifier = Modifier.size(8.dp))
            val minimumLineLength = 6
            var expandedState by remember { mutableStateOf(false) }
            var showReadMoreButtonState by remember { mutableStateOf(true) }
            val maxLines = if (expandedState) 200 else minimumLineLength
            Text(
                modifier = Modifier
                    .heightIn(130.dp, 180.dp)
                    .verticalScroll(rememberScrollState()),
                text = state.product?.fullDescription.orEmpty(),
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                maxLines = maxLines,
                overflow = TextOverflow.Ellipsis,
                lineHeight = TextUnit(20f, TextUnitType.Sp),
                color = OutlineColor,
                onTextLayout = { textLayoutResult: TextLayoutResult ->
                    if (textLayoutResult.lineCount > minimumLineLength-1) {           //Adding this check to avoid ArrayIndexOutOfBounds Exception
                        if (textLayoutResult.isLineEllipsized(minimumLineLength-1)) showReadMoreButtonState = true
                    }
                }
            )
            if (showReadMoreButtonState) {
                Text(
                    modifier = Modifier.clickable {
                        expandedState = !expandedState
                    },
                    text = stringResource(if (expandedState) R.string.title_read_less else R.string.title_read_more),
                    fontFamily = robotoFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    lineHeight = TextUnit(20f, TextUnitType.Sp),
                    color = OnSurfaceColor,
                    textDecoration = TextDecoration.Underline
                )
            }
        }
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    modifier = Modifier,
                    text = stringResource(R.string.title_price),
                    fontFamily = robotoFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    lineHeight = TextUnit(24f, TextUnitType.Sp),
                    color = OnSurfaceColor
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    modifier = Modifier,
                    text = "667₴",
                    fontFamily = robotoFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 22.sp,
                    lineHeight = TextUnit(28f, TextUnitType.Sp),
                    color = OnSurfaceColor
                )
            }
            Spacer(modifier = Modifier.size(32.dp))
            CustomButton(
                text = R.string.title_bought
            ) { }
        }
    }
}