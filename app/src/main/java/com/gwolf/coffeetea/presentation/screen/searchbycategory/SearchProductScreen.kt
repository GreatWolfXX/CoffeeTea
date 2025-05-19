package com.gwolf.coffeetea.presentation.screen.searchbycategory

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardBackspace
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material.icons.outlined.SwapVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.gwolf.coffeetea.R
import com.gwolf.coffeetea.domain.entities.Product
import com.gwolf.coffeetea.navigation.Screen
import com.gwolf.coffeetea.presentation.component.ErrorOrEmptyComponent
import com.gwolf.coffeetea.presentation.component.ErrorOrEmptyStyle
import com.gwolf.coffeetea.presentation.component.FilterItem
import com.gwolf.coffeetea.presentation.component.LoadingComponent
import com.gwolf.coffeetea.presentation.component.MenuFilters
import com.gwolf.coffeetea.presentation.component.MenuSort
import com.gwolf.coffeetea.presentation.component.ProductCard
import com.gwolf.coffeetea.ui.theme.BackgroundGradient
import com.gwolf.coffeetea.ui.theme.GrayAlpha05
import com.gwolf.coffeetea.ui.theme.OnSurfaceColor
import com.gwolf.coffeetea.ui.theme.WhiteAlpha06
import com.gwolf.coffeetea.ui.theme.robotoFontFamily
import com.gwolf.coffeetea.util.ConnectionState
import com.gwolf.coffeetea.util.connectivityState

@Composable
fun SearchProductScreen(
    navController: NavController,
    viewModel: SearchProductViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val connection by connectivityState()
    val isNetworkConnected = connection === ConnectionState.Available

    val state by viewModel.state.collectAsState()

    SearchProductContent(
        context = context,
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
private fun SearchProductContent(
    context: Context,
    state: SearchProductScreenState,
    isNetworkConnected: Boolean,
    navigateToOtherScreen: (Screen) -> Unit = {},
    navigateBack: () -> Unit = {},
    onIntent: (SearchProductIntent) -> Unit = {}
) {
    var menuStateSort by remember { mutableStateOf(false) }
    var menuStateFilter by remember { mutableStateOf(false) }


    val sortingDescription = when {
        !state.isDescending -> stringResource(R.string.title_low_to_high)
        state.isDescending -> stringResource(R.string.title_high_to_low)
        else -> stringResource(R.string.title_filter_empty)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGradient)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopMenu(
                categoryName = state.categoryName,
                navigateBack = navigateBack
            )
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = GrayAlpha05
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        WhiteAlpha06
                    )
            ) {
                FilterItem(
                    icon = Icons.Outlined.SwapVert,
                    title = stringResource(R.string.title_sorting),
                    desc = sortingDescription
                ) {
                    menuStateSort = !menuStateSort
                }
                FilterItem(
                    icon = Icons.Outlined.FilterAlt,
                    title = stringResource(R.string.title_filter),
                    desc = "Не обраний"
                ) {
                    menuStateFilter = !menuStateFilter
                }
            }
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = GrayAlpha05
            )

            if (state.error.asString().isNotBlank() || !isNetworkConnected) {
                val style =
                    if (isNetworkConnected) ErrorOrEmptyStyle.ERROR else ErrorOrEmptyStyle.NETWORK
                val title =
                    if (isNetworkConnected) R.string.title_error else R.string.title_network
                val desc =
                    if (isNetworkConnected) R.string.desc_error else R.string.desc_network
                ErrorOrEmptyComponent(
                    style = style,
                    title = title,
                    desc = desc
                )
            } else {
                SearchProductMainSection(
                    context = context,
                    state = state,
                    navigateToOtherScreen = navigateToOtherScreen
                )
            }
        }

        MenuSort(
            isOpen = menuStateSort,
            onSortingStateChanged = { value ->
                onIntent(SearchProductIntent.ChangeSort(isDescending = value))
            },
            onDismiss = {
                menuStateSort = false
            }
        )
        MenuFilters(
            isOpen = menuStateFilter,
            onDismiss = {
                menuStateFilter = false
            },
            minAndMaxPriceRange = state.minAndMaxPriceRange,
            sliderRangeState = state.priceRangeState,
            textLow = state.textLow,
            textHigh = state.textHigh,
            onValueChangesSlider = { currentRange ->
                onIntent(SearchProductIntent.ChangePriceRangeState(priceRangeState = currentRange))
                onIntent(SearchProductIntent.EnterTextLow("%.2f".format(currentRange.start)))
                onIntent(SearchProductIntent.EnterTextHigh("%.2f".format(currentRange.endInclusive)))
            },
            onValueChangeSliderFinished = { currentRange ->
                onIntent(SearchProductIntent.ChangePriceRangeState(priceRangeState = currentRange))
            },
            onValueChangesTextLow = {
                onIntent(SearchProductIntent.EnterTextLow(it))

                val value = it.replace(',', '.')
                val low = value.toFloatOrNull()
                if (low != null) {
                    onIntent(SearchProductIntent.EnterTextLow(value))

                    if (low <= state.priceRangeState.endInclusive && low >= state.minAndMaxPriceRange.start) {
                        onIntent(SearchProductIntent.ChangePriceRangeState(low..state.priceRangeState.endInclusive))
                    }
                }
            },
            onValueChangesTextHigh = {
                onIntent(SearchProductIntent.EnterTextHigh(it))

                val value = it.replace(',', '.')
                val high = value.toFloatOrNull()
                if (high != null) {
                    onIntent(SearchProductIntent.EnterTextHigh(value))

                    if (high >= state.priceRangeState.start && high <= state.minAndMaxPriceRange.endInclusive) {
                        onIntent(SearchProductIntent.ChangePriceRangeState(state.priceRangeState.start..high))
                    }
                }
            }
        )
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
private fun SearchProductMainSection(
    context: Context,
    state: SearchProductScreenState,
    navigateToOtherScreen: (Screen) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 8.dp),
    ) {
        if (state.productsList.isNotEmpty()) {
            ProductsList(
                context = context,
                productsList = state.productsList,
                navigateToOtherScreen = navigateToOtherScreen
            )
        } else {
            ErrorOrEmptyComponent(
                style = ErrorOrEmptyStyle.PRODUCT_EMPTY,
                title = R.string.title_product_empty,
                desc = R.string.desc_product_empty
            )
        }
    }
}

@Composable
private fun ProductsList(
    context: Context,
    productsList: List<Product>,
    navigateToOtherScreen: (Screen) -> Unit,
) {
    Spacer(modifier = Modifier.size(8.dp))
    LazyVerticalGrid(
        modifier = Modifier,
        columns = GridCells.FixedSize(174.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(bottom = 12.dp)
    ) {
        items(productsList) { product ->
            ProductCard(
                context = context,
                modifier = Modifier.animateItem(),
                product = product,
                onClick = {
                    navigateToOtherScreen(Screen.ProductInfo(productId = product.id))
                },
                onClickBuy = {

                },
                onClickToCart = {

                }
            )
        }
    }
    Spacer(modifier = Modifier.size(8.dp))
}

@Preview
@Composable
private fun SearchProductScreenPreview() {
    val context = LocalContext.current

    SearchProductContent(
        context = context,
        state = SearchProductScreenState(),
        isNetworkConnected = true
    )
}