package com.gwolf.coffeetea.presentation.screen.productinfo

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.gwolf.coffeetea.R
import com.gwolf.coffeetea.navigation.Screen
import com.gwolf.coffeetea.presentation.component.CustomButton
import com.gwolf.coffeetea.presentation.component.ErrorOrEmptyComponent
import com.gwolf.coffeetea.presentation.component.ErrorOrEmptyStyle
import com.gwolf.coffeetea.presentation.component.LoadingComponent
import com.gwolf.coffeetea.ui.theme.BackgroundGradient
import com.gwolf.coffeetea.ui.theme.LightRedColor
import com.gwolf.coffeetea.ui.theme.OnSurfaceColor
import com.gwolf.coffeetea.ui.theme.OutlineColor
import com.gwolf.coffeetea.ui.theme.WhiteAlpha06
import com.gwolf.coffeetea.ui.theme.robotoFontFamily
import com.gwolf.coffeetea.util.ConnectionState
import com.gwolf.coffeetea.util.connectivityState
import timber.log.Timber

@Composable
fun ProductInfoScreen(
    navController: NavController,
    viewModel: ProductInfoViewModel = hiltViewModel()
) {
    val connection by connectivityState()
    val isNetworkConnected = connection === ConnectionState.Available

    val state by viewModel.state.collectAsState()
    val event by viewModel.event.collectAsState(initial = ProductInfoEvent.Idle)

    LaunchedEffect(event) {
        when (event) {
            is ProductInfoEvent.Idle -> {}
        }
    }

    ProductInfoContent(
        state = state,
        isNetworkConnected = isNetworkConnected,
        navigateToOtherScreen = { screen ->
            navController.navigate(screen)
        },
        navigateBack = {
            navController.navigateUp()
        },
        onIntent = { intent ->
            viewModel.onIntent(intent)
        }
    )

    LoadingComponent(state.isLoading)
}

@Composable
private fun ProductInfoContent(
    state: ProductInfoScreenState,
    isNetworkConnected: Boolean,
    navigateToOtherScreen: (Screen) -> Unit = {},
    navigateBack: () -> Unit = {},
    onIntent: (ProductInfoIntent) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGradient)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopMenu(
                categoryName = state.product?.categoryName.orEmpty(),
                navigateBack = navigateBack
            )

            if (state.error.asString().isNotBlank() || !isNetworkConnected) {
                val style =
                    if (isNetworkConnected) ErrorOrEmptyStyle.ERROR else ErrorOrEmptyStyle.NETWORK
                val title = if (isNetworkConnected) R.string.title_error else R.string.title_network
                val desc = if (isNetworkConnected) R.string.desc_error else R.string.desc_network
                ErrorOrEmptyComponent(
                    style = style,
                    title = title,
                    desc = desc
                )
                Timber.d(state.error.asString())
            } else {
                ProductInfoMainSection(
                    state = state,
                    navigateToOtherScreen = navigateToOtherScreen,
                    onIntent = onIntent
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopMenu(
    categoryName: String,
    navigateBack: () -> Unit
) {
    TopAppBar(
        modifier = Modifier,
        title = {
            Text(
                modifier = Modifier.padding(start = 4.dp),
                text = categoryName,
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 22.sp,
                color = OnSurfaceColor
            )
        },
        navigationIcon = {
            Icon(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .clickable(onClick = navigateBack),
                imageVector = Icons.AutoMirrored.Filled.KeyboardBackspace,
                contentDescription = null,
                tint = OnSurfaceColor
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = WhiteAlpha06
        )
    )
}

@Composable
private fun ProductInfoMainSection(
    state: ProductInfoScreenState,
    navigateToOtherScreen: (Screen) -> Unit,
    onIntent: (ProductInfoIntent) -> Unit,
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
                        .height(360.dp)
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp)),
                    model = state.product?.imageUrl,
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
                        .border(
                            width = 1.dp,
                            color = OnSurfaceColor,
                            shape = CircleShape
                        )
                        .padding(4.dp)
                        .clickable {
                            if (state.isFavorite) {
                                onIntent(ProductInfoIntent.RemoveFavorite)
                            } else {
                                onIntent(ProductInfoIntent.AddFavorite)
                            }
                        },
                    imageVector = if (state.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
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
                color = OnSurfaceColor
            )
//            Spacer(modifier = Modifier.size(4.dp))
//            Row(
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Icon(
//                    modifier = Modifier,
//                    imageVector = Icons.Default.Star,
//                    contentDescription = null,
//                    tint = PrimaryColor
//                )
//                Spacer(modifier = Modifier.size(2.dp))
//                if (state.product?.rating != 0.0) {
//                    Text(
//                        modifier = Modifier,
//                        text = state.product?.rating.toString(),
//                        fontFamily = robotoFontFamily,
//                        fontWeight = FontWeight.Medium,
//                        fontSize = 14.sp,
//                        lineHeight = TextUnit(20f, TextUnitType.Sp),
//                        color = OutlineColor
//                    )
//                } else {
//                    Text(
//                        modifier = Modifier,
//                        text = stringResource(R.string.title_no_reviews),
//                        fontFamily = robotoFontFamily,
//                        fontWeight = FontWeight.Medium,
//                        fontSize = 14.sp,
//                        lineHeight = TextUnit(20f, TextUnitType.Sp),
//                        color = OutlineColor
//                    )
//                }
//            }
            Spacer(modifier = Modifier.size(8.dp))
            val minimumLineLength = 5
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
                    if (textLayoutResult.lineCount > minimumLineLength - 1) {
                        if (textLayoutResult.isLineEllipsized(minimumLineLength - 1)) {
                            showReadMoreButtonState = true
                        }
                    } else {
                        showReadMoreButtonState = false
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
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                )
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
                    text = "${state.product?.price ?: 0}₴",
                    fontFamily = robotoFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 22.sp,
                    lineHeight = TextUnit(28f, TextUnitType.Sp),
                    color = OnSurfaceColor
                )
            }
            Spacer(modifier = Modifier.size(32.dp))
            val btnTitle = if (state.isInCart) R.string.title_go_to_cart else R.string.title_bought
            CustomButton(
                text = btnTitle
            ) {
                if (state.isInCart) {
                    navigateToOtherScreen(Screen.Cart)
                } else {
                    onIntent(ProductInfoIntent.AddToCart)
                }
            }
        }
    }
}

@Preview
@Composable
private fun ProductInfoScreenPreview() {
    ProductInfoContent(
        state = ProductInfoScreenState(),
        isNetworkConnected = true
    )
}