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
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.gwolf.coffeetea.domain.model.Product
import com.gwolf.coffeetea.navigation.Screen
import com.gwolf.coffeetea.presentation.component.LoadingComponent
import com.gwolf.coffeetea.presentation.component.ProductCard
import com.gwolf.coffeetea.ui.theme.BackgroundGradient
import com.gwolf.coffeetea.ui.theme.OnSurfaceColor
import com.gwolf.coffeetea.ui.theme.robotoFontFamily

@Composable
fun SearchByCategoryScreen(
    navController: NavController,
    viewModel: SearchByCategoryViewModel = hiltViewModel()
) {
    val state by viewModel.searchByCategoryScreenState
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
            }
            if (state.error != null) {
                Log.d("Coffee&TeaLogger", "Error: ${state.error}")
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
    state: SearchByCategoryUiState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        ProductsList(
            navController = navController,
            productsList = state.productsList
        )
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
            Log.d("Coffee&TeaLogger", "Product: ${product.categoryName}")
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
    state: SearchByCategoryUiState,
    navController: NavController
) {
    TopAppBar(
        modifier = Modifier.padding(horizontal = 16.dp),
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
                    .clickable {
                        navController.popBackStack()
                    },
                imageVector = Icons.AutoMirrored.Filled.KeyboardBackspace,
                contentDescription = null,
                tint = OnSurfaceColor
            )
        },
        actions = {
            Icon(
                modifier = Modifier
                    .clickable {

                    },
                imageVector = Icons.Default.SwapVert,
                contentDescription = null,
                tint = OnSurfaceColor
            )
            Icon(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .clickable {

                    },
                imageVector = Icons.Default.FilterAlt,
                contentDescription = null,
                tint = OnSurfaceColor
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )
    )
}