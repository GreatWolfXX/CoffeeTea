package com.gwolf.coffeetea.presentation.screen.searchbycategory

import android.util.Log
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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
import com.gwolf.coffeetea.util.LOGGER_TAG
import com.gwolf.coffeetea.util.connectivityState

@Composable
fun SearchProductScreen(
    navController: NavController,
    viewModel: SearchProductViewModel = hiltViewModel()
) {
    val state by viewModel.searchProductScreenState
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
                    navController = navController,
                    state = state
                )
                FiltersComponent(
                    onSortingStateChanged = { lowToHighSelected, highToLowSelected ->

                    },
                    priceRange = 0f..100f,
                    onValueChange = { lowPrice, highPrice ->

                    }
                )
            }
            val connection by connectivityState()

            val isConnected = connection === ConnectionState.Available
            if (state.error != null || !isConnected) {
                Log.d(LOGGER_TAG, "Error: ${state.error}")
                val style = if(isConnected) ErrorOrEmptyStyle.ERROR else ErrorOrEmptyStyle.NETWORK
                val title = if(isConnected) R.string.title_error else R.string.title_network
                val desc = if(isConnected) R.string.desc_error else R.string.desc_network
                ErrorOrEmptyComponent(
                    style = style,
                    title = title,
                    desc = desc
                )
            } else {
                FavoriteScreenContent(
                    navController = navController,
                    state = state
                )
            }
        }
    }
    LoadingComponent(state.isLoading)
}

@Composable
private fun FavoriteScreenContent(
    navController: NavController,
    state: SearchProductUiState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        if (state.productsList.isNotEmpty()) {
            ProductsList(
                navController = navController,
                productsList = state.productsList
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
    navController: NavController,
    productsList: List<Product>
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
            Log.d(LOGGER_TAG, "Product: ${product.categoryName}")
            ProductCard(
                modifier = Modifier.animateItem(),
                product = product,
                onClick = {
                    navController.navigate(Screen.ProductInfo(productId = product.id))
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopMenu(
    state: SearchProductUiState,
    navController: NavController
) {
    TopAppBar(
        modifier = Modifier,
        title = {
            Text(
                modifier = Modifier.padding(start = 4.dp),
                text = state.categoryName,
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
                    .clickable {
                        navController.popBackStack()
                    },
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