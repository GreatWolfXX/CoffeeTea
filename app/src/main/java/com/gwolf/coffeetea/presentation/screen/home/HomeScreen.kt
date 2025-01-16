package com.gwolf.coffeetea.presentation.screen.home

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.gwolf.coffeetea.R
import com.gwolf.coffeetea.domain.model.Category
import com.gwolf.coffeetea.domain.model.Product
import com.gwolf.coffeetea.navigation.Screen
import com.gwolf.coffeetea.presentation.component.BlockTitleComponent
import com.gwolf.coffeetea.presentation.component.CategorySmallCard
import com.gwolf.coffeetea.presentation.component.LoadingComponent
import com.gwolf.coffeetea.presentation.component.ProductCard
import com.gwolf.coffeetea.presentation.component.ProductSmallCard
import com.gwolf.coffeetea.presentation.component.PromotionsComponent
import com.gwolf.coffeetea.ui.theme.BackgroundColor
import com.gwolf.coffeetea.ui.theme.BackgroundGradient
import com.gwolf.coffeetea.ui.theme.OnSurfaceColor
import com.gwolf.coffeetea.ui.theme.robotoFontFamily

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    //Endless Loading Error
    val state by viewModel.homeScreenState
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGradient)
    ) {
        if (state.error != null) {
            Log.d("Coffee&TeaLogger", "Error: ${state.error}")
        } else {
            HomeScreenContent(
                navController = navController,
                state = state,
                viewModel = viewModel
            )
        }
    }
    LoadingComponent(state.isLoading)
}

@Composable
private fun HomeScreenContent(
    navController: NavController,
    state: HomeUiState,
    viewModel: HomeViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        SearchBarComponent(
            state = state,
            viewModel = viewModel,
            navController = navController
        )
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            Spacer(modifier = Modifier.size(16.dp))
            PromotionsComponent(state.promotionsList)
            Spacer(modifier = Modifier.size(16.dp))
            CategoriesList(
                navController = navController,
                categoriesList = state.categoriesList
            )
            Spacer(modifier = Modifier.size(16.dp))
            Log.d("Coffee&TeaLogger", "Update: ${state.productsList}")
            ProductsList(
                navController = navController,
                viewModel = viewModel,
                productsList = state.productsList
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBarComponent(
    state: HomeUiState,
    viewModel: HomeViewModel,
    navController: NavController
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val padding by animateDpAsState(targetValue = if (expanded) 0.dp else 16.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = padding)
            .padding(top = padding)
    ) {
        SearchBar(
            modifier = Modifier.fillMaxWidth(),
            inputField = {
                SearchBarDefaults.InputField(
                    query = state.searchText,
                    onQueryChange = { query ->
                        viewModel.onEvent(HomeEvent.Search(query))
                    },
                    onSearch = { expanded = false },
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    colors = SearchBarDefaults.inputFieldColors(
                        cursorColor = OnSurfaceColor,
                        focusedTextColor = OnSurfaceColor,
                        unfocusedTextColor = OnSurfaceColor
                    ),
                    placeholder = {
                        Text(
                            modifier = Modifier,
                            text = stringResource(R.string.title_search),
                            fontFamily = robotoFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            color = OnSurfaceColor
                        )
                    },
                    trailingIcon = {
                        Row(
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            AnimatedVisibility(expanded) {
                                Icon(
                                    modifier = Modifier.clickable {
                                        viewModel.onSearchTextChange("")
                                    },
                                    imageVector = Icons.Outlined.Cancel,
                                    contentDescription = null,
                                    tint = OnSurfaceColor
                                )
                            }
                            Spacer(modifier = Modifier.size(16.dp))
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = OnSurfaceColor
                            )
                        }
                    },
                    leadingIcon = if (expanded) {
                        {
                            Icon(
                                modifier = Modifier.clickable {
                                    expanded = false
                                    viewModel.onSearchTextChange("")
                                },
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                tint = OnSurfaceColor
                            )
                        }
                    } else {
                        null
                    }
                )
            },
            expanded = expanded,
            onExpandedChange = { expanded = it },
            colors = SearchBarDefaults.colors(
                containerColor = BackgroundColor
            ),
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .wrapContentHeight(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(state.searchProductsList) { product ->
                        ProductSmallCard(
                            modifier = Modifier.animateItem(),
                            product = product,
                            onClick = {
                                navController.navigate(Screen.ProductInfo(productId = product.id))
                            },
                            onClickBuy = {
                                viewModel.onEvent(HomeEvent.AddToCart(product = product))
                                navController.navigate(Screen.Cart)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoriesList(
    navController: NavController,
    categoriesList: List<Category>
) {
    BlockTitleComponent(
        text = R.string.title_categories
    ) {
        navController.navigate(Screen.Category)
    }
    Spacer(modifier = Modifier.size(8.dp))
    LazyRow(
        modifier = Modifier
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(categoriesList) { category ->
            CategorySmallCard(
                modifier = Modifier.animateItem(),
                category = category
            ) {
                navController.navigate(
                    Screen.SearchByCategory(
                        categoryId = category.id,
                        categoryName = category.name
                    )
                )
            }
        }
    }
}

@Composable
private fun ProductsList(
    navController: NavController,
    viewModel: HomeViewModel,
    productsList: List<Product>
) {
    BlockTitleComponent(
        text = R.string.title_popular_products
    ) { }
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
                modifier = Modifier.animateItem(),
                product = product,
                onClick = {
                    navController.navigate(Screen.ProductInfo(productId = product.id))
                },
                onClickBuy = {
                    viewModel.onEvent(HomeEvent.AddToCart(product))
                },
                onClickToCart = {
                    navController.navigate(Screen.Cart)
                }
            )
        }
    }

    Spacer(modifier = Modifier.size(8.dp))
}