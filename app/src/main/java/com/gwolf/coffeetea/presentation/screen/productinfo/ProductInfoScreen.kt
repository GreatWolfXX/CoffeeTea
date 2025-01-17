package com.gwolf.coffeetea.presentation.screen.productinfo

import android.content.Context
import android.util.Log
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.gwolf.coffeetea.R
import com.gwolf.coffeetea.navigation.Screen
import com.gwolf.coffeetea.presentation.component.CustomButton
import com.gwolf.coffeetea.presentation.component.LoadingComponent
import com.gwolf.coffeetea.ui.theme.BackgroundGradient
import com.gwolf.coffeetea.ui.theme.LightRedColor
import com.gwolf.coffeetea.ui.theme.OnSurfaceColor
import com.gwolf.coffeetea.ui.theme.OutlineColor
import com.gwolf.coffeetea.ui.theme.PrimaryColor
import com.gwolf.coffeetea.ui.theme.robotoFontFamily
import com.gwolf.coffeetea.util.LOGGER_TAG

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductInfoScreen(
    navController: NavController,
    viewModel: ProductInfoViewModel = hiltViewModel()
) {
    val state by viewModel.productInfoScreenState
    val context = LocalContext.current

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
                        text = state.product?.categoryName.orEmpty(),
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
                Log.d(LOGGER_TAG, "Error: ${state.error}")
            } else {
                ProductInfoScreenContent(
                    navController = navController,
                    state = state,
                    viewModel = viewModel,
                    context = context
                )
            }
        }
    }
    LoadingComponent(state.isLoading)
}

@Composable
private fun ProductInfoScreenContent(
    navController: NavController,
    state: ProductInfoUiState,
    viewModel: ProductInfoViewModel,
    context: Context
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(0.88f)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Box {
                AsyncImage(
                    modifier = Modifier
                        .height(348.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp)),
                    model  = ImageRequest.Builder(context)
                        .data(state.product?.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
                Icon(
                    modifier = Modifier
                        .align(alignment = Alignment.BottomEnd)
                        .padding(bottom = 12.dp, end = 12.dp)
                        .scale(1.3f)
                        .clip(CircleShape)
                        .background(
                            color = Color.White
                        )
                        .padding(4.dp)
                        .clickable {
                            if(state.isFavorite) {
                                viewModel.onEvent(ProductInfoEvent.RemoveFavorite)
                            } else {
                                viewModel.onEvent(ProductInfoEvent.AddFavorite)
                            }
                        },
                    imageVector = if(state.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder ,
                    contentDescription = null,
                    tint = LightRedColor
                )
            }
            Spacer(modifier = Modifier.size(16.dp))
            Text(
                modifier = Modifier,
                text = state.product?.name.orEmpty(),
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 28.sp,
                lineHeight = TextUnit(36f, TextUnitType.Sp),
                color = OnSurfaceColor
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                modifier = Modifier,
                text = state.product?.featuresDescription.orEmpty(),
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = TextUnit(20f, TextUnitType.Sp),
                color = OutlineColor
            )
            Spacer(modifier = Modifier.size(4.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier,
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = PrimaryColor
                )
                Spacer(modifier = Modifier.size(2.dp))
                if(state.product?.rating != 0.0) {
                    Text(
                        modifier = Modifier,
                        text = state.product?.rating.toString(),
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
            Spacer(modifier = Modifier.size(4.dp))
            val minimumLineLength = 6
            var expandedState by remember { mutableStateOf(false) }
            var showReadMoreButtonState by remember { mutableStateOf(true) }
            val maxLines = if (expandedState) 200 else minimumLineLength
            Text(
                modifier = Modifier
                    .animateContentSize(),
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
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .weight(0.12f)
                .background(Color.White)
                .padding(top = 8.dp)
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp),
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
            val btnTitle = if(state.isInCart) R.string.title_go_to_cart else R.string.title_bought
            CustomButton(
                text = btnTitle
            ) {
                if(state.isInCart) {
                    navController.navigate(Screen.Cart)
                } else {
                    viewModel.onEvent(ProductInfoEvent.AddToCart)
                }

            }
        }
    }
}