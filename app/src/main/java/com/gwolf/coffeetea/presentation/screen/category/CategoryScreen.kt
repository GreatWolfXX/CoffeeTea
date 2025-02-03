package com.gwolf.coffeetea.presentation.screen.category

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.gwolf.coffeetea.R
import com.gwolf.coffeetea.domain.model.Category
import com.gwolf.coffeetea.navigation.Screen
import com.gwolf.coffeetea.presentation.component.CategoryFullCard
import com.gwolf.coffeetea.presentation.component.ErrorOrEmptyComponent
import com.gwolf.coffeetea.presentation.component.ErrorOrEmptyStyle
import com.gwolf.coffeetea.presentation.component.LoadingComponent
import com.gwolf.coffeetea.ui.theme.BackgroundGradient
import com.gwolf.coffeetea.ui.theme.OnSurfaceColor
import com.gwolf.coffeetea.ui.theme.robotoFontFamily
import com.gwolf.coffeetea.util.LOGGER_TAG

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    navController: NavController,
    viewModel: CategoryViewModel = hiltViewModel()
) {
    val state by viewModel.categoryScreenState
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
                        text = stringResource(id = R.string.title_categories),
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
                ErrorOrEmptyComponent(
                    style = ErrorOrEmptyStyle.ERROR,
                    title = R.string.title_error,
                    desc = R.string.desc_error
                )
            } else {
                CategoryScreenContent(
                    navController = navController,
                    state = state
                )
            }
        }
    }
    LoadingComponent(state.isLoading)
}

@Composable
private fun CategoryScreenContent(
    navController: NavController,
    state: CategoryUiState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        CategoryList(
            navController = navController,
            categoriesList = state.categoriesList
        )
    }
}


@Composable
private fun CategoryList(
    navController: NavController,
    categoriesList: List<Category>
) {
    Spacer(modifier = Modifier.size(8.dp))
    LazyVerticalGrid(
        modifier = Modifier,
        columns = GridCells.FixedSize(112.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 12.dp)
    ) {
        items(categoriesList) { category ->
            CategoryFullCard(
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

    Spacer(modifier = Modifier.size(8.dp))
}
