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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.gwolf.coffeetea.R
import com.gwolf.coffeetea.domain.model.Category
import com.gwolf.coffeetea.domain.model.Product
import com.gwolf.coffeetea.presentation.component.BlockTitleComponent
import com.gwolf.coffeetea.presentation.component.CategorySmallCard
import com.gwolf.coffeetea.presentation.component.LoadingComponent
import com.gwolf.coffeetea.presentation.component.ProductCard
import com.gwolf.coffeetea.presentation.component.PromotionsComponent
import com.gwolf.coffeetea.ui.theme.BackgroundGradient
import com.gwolf.coffeetea.ui.theme.OnSurfaceColor
import com.gwolf.coffeetea.ui.theme.robotoFontFamily

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
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
                state = state
            )
        }
    }
    LoadingComponent(state.isLoading)
}

@Composable
private fun HomeScreenContent(
    navController: NavController,
    state: HomeUiState
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        SearchBarComponent()
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            Spacer(modifier = Modifier.size(16.dp))
            PromotionsComponent(state.promotionsList)
            Spacer(modifier = Modifier.size(16.dp))
            CategoriesList(categoriesList = state.categoriesList)
            Spacer(modifier = Modifier.size(16.dp))
            ProductsList(productsList = state.productsList)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBarComponent() {
    var searchQuery by remember { mutableStateOf("") }
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
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
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
                                        searchQuery = ""
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
                                    searchQuery = ""
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
                containerColor = Color.White
            ),
            shadowElevation = 4.dp
        ) {

        }
    }
}

@Composable
private fun CategoriesList(
    categoriesList: List<Category>
) {
    BlockTitleComponent(
        text = R.string.title_categories
    )
    Spacer(modifier = Modifier.size(8.dp))
    LazyRow(
        modifier = Modifier
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(categoriesList) { category ->
            CategorySmallCard(
                category = category
            )
        }
    }
}

@Composable
private fun ProductsList(
    productsList: List<Product>
) {
    BlockTitleComponent(
        text = R.string.title_popular_products
    )
    Spacer(modifier = Modifier.size(8.dp))
    LazyVerticalGrid(
        modifier = Modifier,
        columns = GridCells.FixedSize(180.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(bottom = 12.dp)
    ) {
        items(productsList) { product ->
            ProductCard(product = product)
        }
    }

    Spacer(modifier = Modifier.size(8.dp))
}