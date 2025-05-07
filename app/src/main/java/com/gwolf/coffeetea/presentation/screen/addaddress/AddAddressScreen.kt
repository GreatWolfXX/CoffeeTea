package com.gwolf.coffeetea.presentation.screen.addaddress

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardBackspace
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.gwolf.coffeetea.LocalSnackbarHostState
import com.gwolf.coffeetea.R
import com.gwolf.coffeetea.presentation.component.CitySelector
import com.gwolf.coffeetea.presentation.component.CustomButton
import com.gwolf.coffeetea.presentation.component.CustomSearchBar
import com.gwolf.coffeetea.presentation.component.ErrorOrEmptyComponent
import com.gwolf.coffeetea.presentation.component.ErrorOrEmptyStyle
import com.gwolf.coffeetea.presentation.component.LoadingComponent
import com.gwolf.coffeetea.presentation.component.PostComponent
import com.gwolf.coffeetea.ui.theme.BackgroundGradient
import com.gwolf.coffeetea.ui.theme.NovaPostColor
import com.gwolf.coffeetea.ui.theme.OnSurfaceColor
import com.gwolf.coffeetea.ui.theme.WhiteAlpha06
import com.gwolf.coffeetea.ui.theme.robotoFontFamily
import com.gwolf.coffeetea.util.ConnectionState
import com.gwolf.coffeetea.util.NOVA_POST_CABINE_REF
import com.gwolf.coffeetea.util.NOVA_POST_DEPARTMENT_REF
import com.gwolf.coffeetea.util.connectivityState

@Composable
fun AddAddressScreen(
    navController: NavController,
    viewModel: AddAddressViewModel = hiltViewModel()
) {
    val connection by connectivityState()
    val isNetworkConnected = connection === ConnectionState.Available

    val state by viewModel.state.collectAsState()
    val event by viewModel.event.collectAsState(initial = AddAddressEvent.Idle)

    LaunchedEffect(event) {
        when(event) {
            is AddAddressEvent.Idle -> {}
            is AddAddressEvent.Navigate -> {
                navController.navigateUp()
            }
        }
    }

    AddAddressContent(
        state = state,
        isNetworkConnected = isNetworkConnected,
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
private fun AddAddressContent(
    state: AddAddressScreenState,
    isNetworkConnected: Boolean,
    navigateBack: () -> Unit = {},
    onIntent: (AddAddressIntent) -> Unit = {}
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
                navigateBack = navigateBack
            )

            if (state.error.asString().isNotBlank() || !isNetworkConnected) {
                val style = if (isNetworkConnected) ErrorOrEmptyStyle.ERROR else ErrorOrEmptyStyle.NETWORK
                val title = if (isNetworkConnected) R.string.title_error else R.string.title_network
                val desc = if (isNetworkConnected) R.string.desc_error else R.string.desc_network
                ErrorOrEmptyComponent(
                    style = style,
                    title = title,
                    desc = desc
                )
            } else {
                AddAddressForm(
                    state = state,
                    onIntent = onIntent
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopMenu(
    navigateBack: () -> Unit
) {
    TopAppBar(
        modifier = Modifier,
        title = {
            Text(
                modifier = Modifier.padding(start = 4.dp),
                text = stringResource(R.string.btn_add_new_address),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddAddressForm(
    state: AddAddressScreenState,
    onIntent: (AddAddressIntent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(bottom = 20.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        var showDepartmentSearchBarBottomSheet by remember { mutableStateOf(false) }
        val postEnabled = state.selection.selectedCity != null
        Column(
            modifier = Modifier.weight(0.8f)
        ) {
            CityBlock(
                state = state,
                onIntent = onIntent
            )
            Spacer(modifier = Modifier.size(16.dp))
            PostComponent(
                icon = ImageVector.vectorResource(R.drawable.nova_post),
                iconTint = NovaPostColor,
                title = stringResource(R.string.nova_post_departments),
                desc = stringResource(R.string.nova_post_departments_desc),
                departmentName = if (state.selection.selectedDepartment != null) state.selection.selectedDepartment.name else stringResource(
                    R.string.placeholder_department
                ),
                priceTitle = stringResource(R.string.nova_post_departments_price),
                selected = state.selection.selectedNovaPostDepartments,
                enabled = postEnabled,
                onSelectedChange = {
                    onIntent(AddAddressIntent.ButtonClick.SetTypeDepartment(NOVA_POST_DEPARTMENT_REF))
                    onIntent(AddAddressIntent.ButtonClick.SelectNovaPostDepartments)
                },
                onAddressClick = {
                    showDepartmentSearchBarBottomSheet = true
                }
            )
            Spacer(modifier = Modifier.size(16.dp))
            PostComponent(
                icon = ImageVector.vectorResource(R.drawable.nova_post),
                iconTint = NovaPostColor,
                title = stringResource(R.string.nova_post),
                desc = stringResource(R.string.nova_post_desc),
                departmentName = if (state.selection.selectedDepartment != null) state.selection.selectedDepartment.name else stringResource(
                    R.string.placeholder_department_cabin
                ),
                priceTitle = stringResource(R.string.nova_post_price),
                selected = state.selection.selectedNovaPostCabin,
                enabled = postEnabled,
                onSelectedChange = {
                    onIntent(AddAddressIntent.ButtonClick.SetTypeDepartment(NOVA_POST_CABINE_REF))
                    onIntent(AddAddressIntent.ButtonClick.SelectNovaPost)
                },
                onAddressClick = {
                    showDepartmentSearchBarBottomSheet = true
                }
            )
            val titlePost =
                if (state.selection.selectedNovaPostDepartments) stringResource(R.string.title_department) else stringResource(
                    R.string.title_department_cabin
                )
            val placeholderPost =
                if (state.selection.selectedNovaPostDepartments) stringResource(R.string.placeholder_department) else stringResource(
                    R.string.placeholder_department_cabin
                )
            if (showDepartmentSearchBarBottomSheet) {
                CustomSearchBar(
                    title = titlePost,
                    placeholder = placeholderPost,
                    sheetState = sheetState,
                    list = state.search.searchDepartmentsList,
                    query = state.search.searchDepartment,
                    onQueryChange = { value ->
                        onIntent(AddAddressIntent.Input.SearchDepartment(value))
                    },
                    onClear = {
                        onIntent(AddAddressIntent.Input.SearchDepartment(""))
                    },
                    onDismiss = {
                        showDepartmentSearchBarBottomSheet = false
                    },
                    onClickItem = { department ->
                        onIntent(AddAddressIntent.ButtonClick.SelectDepartment(department))
                        showDepartmentSearchBarBottomSheet = false
                    },
                    itemText = { department -> department.name }
                )
            }
        }
        Spacer(modifier = Modifier.size(16.dp))
        val btnEnabled = state.selection.selectedDepartment != null
        CustomButton(
            text = R.string.btn_add_new_address,
            isEnabled = btnEnabled
        ) {
            onIntent(AddAddressIntent.ButtonClick.Submit)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CityBlock(
    state: AddAddressScreenState,
    onIntent: (AddAddressIntent) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var showAddressSearchBarBottomSheet by remember { mutableStateOf(false) }
    CitySelector(
        cityName = state.selection.selectedCity?.name ?: "",
        onClick = {
            onIntent(AddAddressIntent.ButtonClick.ClearSelected)
            showAddressSearchBarBottomSheet = !showAddressSearchBarBottomSheet
        }
    )
    if (showAddressSearchBarBottomSheet) {
        CustomSearchBar(
            title = stringResource(R.string.title_city),
            placeholder = stringResource(R.string.placeholder_city),
            sheetState = sheetState,
            list = state.search.searchCitiesList,
            query = state.search.searchCity,
            onQueryChange = { value ->
                onIntent(AddAddressIntent.Input.SearchCity(value))
            },
            onClear = {
                onIntent(AddAddressIntent.Input.SearchCity(""))
            },
            onDismiss = {
                showAddressSearchBarBottomSheet = false
            },
            onClickItem = { city ->
                onIntent(AddAddressIntent.ButtonClick.SelectCity(city))
                showAddressSearchBarBottomSheet = false
            },
            itemText = { city -> city.name }
        )
    }
}

@Preview
@Composable
private fun AddAddressScreenPreview() {
    val snackbarHostState = remember { SnackbarHostState() }

    CompositionLocalProvider(
        LocalSnackbarHostState provides snackbarHostState,
    ) {
        AddAddressContent(
            state = AddAddressScreenState(),
            isNetworkConnected = true
        )
    }
}