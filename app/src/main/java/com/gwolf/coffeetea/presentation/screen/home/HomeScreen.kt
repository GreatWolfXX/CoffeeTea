package com.gwolf.coffeetea.presentation.screen.home

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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.gwolf.coffeetea.R
import com.gwolf.coffeetea.domain.entities.Category
import com.gwolf.coffeetea.domain.entities.Product
import com.gwolf.coffeetea.navigation.Screen
import com.gwolf.coffeetea.presentation.component.BlockTitleComponent
import com.gwolf.coffeetea.presentation.component.CategorySmallCard
import com.gwolf.coffeetea.presentation.component.ErrorOrEmptyComponent
import com.gwolf.coffeetea.presentation.component.ErrorOrEmptyStyle
import com.gwolf.coffeetea.presentation.component.LoadingComponent
import com.gwolf.coffeetea.presentation.component.ProductCard
import com.gwolf.coffeetea.presentation.component.ProductSmallCard
import com.gwolf.coffeetea.presentation.component.PromotionsComponent
import com.gwolf.coffeetea.ui.theme.BackgroundColor
import com.gwolf.coffeetea.ui.theme.BackgroundGradient
import com.gwolf.coffeetea.ui.theme.OnSurfaceColor
import com.gwolf.coffeetea.ui.theme.robotoFontFamily
import com.gwolf.coffeetea.util.ConnectionState
import com.gwolf.coffeetea.util.connectivityState

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val connection by connectivityState()
    val isNetworkConnected = connection === ConnectionState.Available

    val state by viewModel.state.collectAsState()
    val event by viewModel.event.collectAsState(initial = HomeEvent.Idle)

    LaunchedEffect(event) {
        when (event) {
            is HomeEvent.Idle -> {}
            is HomeEvent.NavigateToCart -> {
                navController.navigate(Screen.Cart)
            }
        }
    }

    HomeContent(
        state = state,
        isNetworkConnected = isNetworkConnected,
        navigateToOtherScreen = { screen ->
            navController.navigate(screen)
        },
        onIntent = { intent ->
            viewModel.onIntent(intent)
        }
    )

    LoadingComponent(state.isLoading)
}

@Composable
private fun HomeContent(
    state: HomeScreenState,
    isNetworkConnected: Boolean,
    navigateToOtherScreen: (Screen) -> Unit = {},
    onIntent: (HomeIntent) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGradient),
        contentAlignment = Alignment.Center
    ) {
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
        } else {
            HomeMainSection(
                state = state,
                navigateToOtherScreen = navigateToOtherScreen,
                onIntent = onIntent
            )
        }
    }
}

@Composable
private fun HomeMainSection(
    state: HomeScreenState,
    navigateToOtherScreen: (Screen) -> Unit,
    onIntent: (HomeIntent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        SearchBarComponent(
            state = state,
            navigateToOtherScreen = navigateToOtherScreen,
            onIntent = onIntent
        )
        Spacer(modifier = Modifier.size(16.dp))
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            PromotionsComponent(state.promotionsList)
            Spacer(modifier = Modifier.size(16.dp))
            CategoriesList(
                categoriesList = state.categoriesList,
                navigateToOtherScreen = navigateToOtherScreen
            )
            Spacer(modifier = Modifier.size(16.dp))
            ProductsList(
                productsList = state.productsList,
                navigateToOtherScreen = navigateToOtherScreen,
                onIntent = onIntent
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBarComponent(
    state: HomeScreenState,
    navigateToOtherScreen: (Screen) -> Unit,
    onIntent: (HomeIntent) -> Unit = {}
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
                        onIntent(HomeIntent.Input.Search(query))
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
                                        onIntent(HomeIntent.ClearSearch)
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
                                    onIntent(HomeIntent.ClearSearch)
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
                                navigateToOtherScreen(Screen.ProductInfo(productId = product.id))
                            },
                            onClickBuy = {
                                onIntent(HomeIntent.ButtonClick.AddToCart(product = product))
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
    categoriesList: List<Category>,
    navigateToOtherScreen: (Screen) -> Unit
) {
    BlockTitleComponent(
        text = R.string.title_categories
    ) {
        navigateToOtherScreen(Screen.Category)
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
                navigateToOtherScreen(
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
    productsList: List<Product>,
    navigateToOtherScreen: (Screen) -> Unit,
    onIntent: (HomeIntent) -> Unit,
) {
    val screenHeight = ((productsList.size + 1) / 2 * 240).dp
    BlockTitleComponent(
        text = R.string.title_popular_products
    ) { }
    Spacer(modifier = Modifier.size(8.dp))
    LazyVerticalGrid(
        modifier = Modifier.height(screenHeight),
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
                    navigateToOtherScreen(Screen.ProductInfo(productId = product.id))
                },
                onClickBuy = {
                    onIntent(HomeIntent.ButtonClick.AddToCart(product))
                },
                onClickToCart = {
                    navigateToOtherScreen(Screen.Cart)
                }
            )
        }
    }
    Spacer(modifier = Modifier.size(8.dp))
}

@Preview
@Composable
private fun HomeScreenPreview() {
    HomeContent(
        state = HomeScreenState(),
        isNetworkConnected = true
    )
}