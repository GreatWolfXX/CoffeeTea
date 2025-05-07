package com.gwolf.coffeetea.presentation.screen.searchbycategory

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardBackspace
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.gwolf.coffeetea.presentation.component.FiltersComponent
import com.gwolf.coffeetea.presentation.component.LoadingComponent
import com.gwolf.coffeetea.presentation.component.ProductCard
import com.gwolf.coffeetea.ui.theme.BackgroundGradient
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
    navigateBack: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGradient)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!state.isLoading) {
                TopMenu(
                    categoryName = state.categoryName,
                    navigateBack = navigateBack
                )
                FiltersComponent(
                    onSortingStateChanged = { lowToHighSelected, highToLowSelected ->

                    },
                    priceRange = 0f..100f,
                    onValueChange = { lowPrice, highPrice ->

                    }
                )
            }

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
            .padding(horizontal = 16.dp),
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