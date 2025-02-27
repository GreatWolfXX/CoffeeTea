package com.gwolf.coffeetea.presentation.screen.addaddress

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.KeyboardBackspace
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.gwolf.coffeetea.R
import com.gwolf.coffeetea.domain.model.City
import com.gwolf.coffeetea.domain.model.Department
import com.gwolf.coffeetea.presentation.component.CustomButton
import com.gwolf.coffeetea.presentation.component.ErrorOrEmptyComponent
import com.gwolf.coffeetea.presentation.component.ErrorOrEmptyStyle
import com.gwolf.coffeetea.presentation.component.LoadingComponent
import com.gwolf.coffeetea.presentation.component.PostComponent
import com.gwolf.coffeetea.presentation.component.SearchBarBottomSheet
import com.gwolf.coffeetea.ui.theme.BackgroundGradient
import com.gwolf.coffeetea.ui.theme.NovaPostColor
import com.gwolf.coffeetea.ui.theme.OnSurfaceColor
import com.gwolf.coffeetea.ui.theme.OutlineColor
import com.gwolf.coffeetea.ui.theme.robotoFontFamily
import com.gwolf.coffeetea.util.ConnectionState
import com.gwolf.coffeetea.util.LOGGER_TAG
import com.gwolf.coffeetea.util.NOVA_POST_CABINE_REF
import com.gwolf.coffeetea.util.NOVA_POST_DEPARTMENT_REF
import com.gwolf.coffeetea.util.connectivityState

@Composable
fun AddAddressScreen(
    snackbarHostState: SnackbarHostState,
    navController: NavController,
    viewModel: AddAddressViewModel = hiltViewModel()
) {
    val state by viewModel.addAddressEventScreenState

    LaunchedEffect(state.isAddressAdded) {
        if(state.isAddressAdded) {
            navController.popBackStack()
        }
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
                navController = navController
            )

            val connection by connectivityState()

            val isConnected = connection === ConnectionState.Available
            if (state.error != null || !isConnected) {
                Log.d(LOGGER_TAG, "Error: ${state.error}")
                val style = if (isConnected) ErrorOrEmptyStyle.ERROR else ErrorOrEmptyStyle.NETWORK
                val title = if (isConnected) R.string.title_error else R.string.title_network
                val desc = if (isConnected) R.string.desc_error else R.string.desc_network
                ErrorOrEmptyComponent(
                    style = style,
                    title = title,
                    desc = desc
                )
            } else {
                AddAddressScreenContent(
                    snackbarHostState = snackbarHostState,
                    state = state,
                    viewModel = viewModel
                )
            }
        }
    }
    LoadingComponent(state.isLoading)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopMenu(
    navController: NavController
) {
    TopAppBar(
        modifier = Modifier.padding(horizontal = 8.dp),
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddAddressScreenContent(
    snackbarHostState: SnackbarHostState,
    state: AddAddressUiState,
    viewModel: AddAddressViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(bottom = 20.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )
        var showDepartmentSearchBarBottomSheet by remember { mutableStateOf(false) }
        val postEnabled = state.selectedCity != null
        Column(
            modifier = Modifier.weight(0.8f)
        ) {
            AddressBlock(
                state = state,
                viewModel = viewModel
            )
            Spacer(modifier = Modifier.size(16.dp))
            PostComponent(
                snackbarHostState = snackbarHostState,
                icon = ImageVector.vectorResource(R.drawable.nova_post),
                iconTint = NovaPostColor,
                title = stringResource(R.string.nova_post_departments),
                desc = stringResource(R.string.nova_post_departments_desc),
                departmentName = if (state.selectedDepartment != null) state.selectedDepartment!!.name else stringResource(
                    R.string.placeholder_department
                ),
                priceTitle = stringResource(R.string.nova_post_departments_price),
                selected = state.selectedNovaPostDepartments,
                enabled = postEnabled,
                onSelectedChange = {
                    viewModel.onEvent(AddAddressEvent.SetTypeDepartment(NOVA_POST_DEPARTMENT_REF))
                    viewModel.onEvent(AddAddressEvent.SelectNovaPostDepartments)
                },
                onAddressClick = {
                    showDepartmentSearchBarBottomSheet = true
                }
            )
            Spacer(modifier = Modifier.size(16.dp))
            PostComponent(
                snackbarHostState = snackbarHostState,
                icon = ImageVector.vectorResource(R.drawable.nova_post),
                iconTint = NovaPostColor,
                title = stringResource(R.string.nova_post),
                desc = stringResource(R.string.nova_post_desc),
                departmentName = if (state.selectedDepartment != null) state.selectedDepartment!!.name else stringResource(
                    R.string.placeholder_department_cabin
                ),
                priceTitle = stringResource(R.string.nova_post_price),
                selected = state.selectedNovaPostCabin,
                enabled = postEnabled,
                onSelectedChange = {
                    viewModel.onEvent(AddAddressEvent.SetTypeDepartment(NOVA_POST_CABINE_REF))
                    viewModel.onEvent(AddAddressEvent.SelectNovaPost)
                },
                onAddressClick = {
                    showDepartmentSearchBarBottomSheet = true
                }
            )
            val titlePost =
                if (state.selectedNovaPostDepartments) stringResource(R.string.title_department) else stringResource(
                    R.string.title_department_cabin
                )
            val placeholderPost =
                if (state.selectedNovaPostDepartments) stringResource(R.string.placeholder_department) else stringResource(
                    R.string.placeholder_department_cabin
                )
            if (showDepartmentSearchBarBottomSheet) {
                DepartmentsSearchBar(
                    title = titlePost,
                    placeholder = placeholderPost,
                    sheetState = sheetState,
                    list = state.searchDepartmentsList,
                    query = state.searchDepartment,
                    onQueryChange = { query ->
                        viewModel.onEvent(AddAddressEvent.SearchDepartment(query))
                    },
                    onClear = {
                        viewModel.onEvent(AddAddressEvent.SearchDepartment(""))
                    },
                    onDismiss = {
                        showDepartmentSearchBarBottomSheet = false
                    },
                    onClickItem = { department ->
                        viewModel.onEvent(AddAddressEvent.SelectDepartment(department))
                        showDepartmentSearchBarBottomSheet = false
                    }
                )
            }
        }
        Spacer(modifier = Modifier.size(16.dp))
        val btnEnabled = state.selectedDepartment != null
        CustomButton(
            text = R.string.btn_add_new_address,
            isEnabled = btnEnabled
        ) {
            viewModel.onEvent(AddAddressEvent.Submit)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddressBlock(
    state: AddAddressUiState,
    viewModel: AddAddressViewModel
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var showAddressSearchBarBottomSheet by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .padding(top = 16.dp)
            .border(
                width = 1.dp,
                color = OnSurfaceColor,
                shape = RoundedCornerShape(4.dp)
            )
            .padding(16.dp)
            .clickable {
                viewModel.onEvent(AddAddressEvent.ClearSelected)
                showAddressSearchBarBottomSheet = !showAddressSearchBarBottomSheet
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier.weight(0.1f),
            contentAlignment = Alignment.CenterStart
        ) {
            Icon(
                modifier = Modifier
                    .size(24.dp),
                imageVector = Icons.Outlined.LocationOn,
                contentDescription = null,
                tint = OnSurfaceColor
            )
        }
        Column(
            modifier = Modifier.weight(0.8f),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                modifier = Modifier,
                text = stringResource(R.string.title_your_city),
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = OutlineColor
            )
            val city =
                if (state.selectedCity != null) state.selectedCity.name else stringResource(R.string.choose_city)
            Text(
                modifier = Modifier,
                text = city,
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = OnSurfaceColor
            )
        }
        Box(
            modifier = Modifier.weight(0.1f),
            contentAlignment = Alignment.CenterEnd
        ) {
            Icon(
                modifier = Modifier
                    .size(16.dp),
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = OnSurfaceColor
            )
        }
    }
    if (showAddressSearchBarBottomSheet) {
        AddressSearchBar(
            sheetState = sheetState,
            list = state.searchCitiesList,
            query = state.searchCity,
            onQueryChange = { query ->
                viewModel.onEvent(AddAddressEvent.SearchCity(query))
            },
            onClear = {
                viewModel.onEvent(AddAddressEvent.SearchCity(""))
            },
            onDismiss = {
                showAddressSearchBarBottomSheet = false
            },
            onClickItem = { city ->
                viewModel.onEvent(AddAddressEvent.SelectCity(city))
                showAddressSearchBarBottomSheet = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddressSearchBar(
    sheetState: SheetState,
    list: List<City>,
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    onDismiss: () -> Unit,
    onClickItem: (City) -> Unit
) {
    SearchBarBottomSheet(
        modifier = Modifier,
        sheetState = sheetState,
        title = stringResource(R.string.title_city),
        placeholder = stringResource(R.string.placeholder_city),
        query = query,
        onQueryChange = onQueryChange,
        onClear = onClear,
        onDismiss = onDismiss
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight(0.75f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(list) { city ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onClickItem(city)
                        }
                ) {
                    Text(
                        modifier = Modifier,
                        text = city.name,
                        textAlign = TextAlign.Start,
                        fontFamily = robotoFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = OnSurfaceColor
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    HorizontalDivider(
                        modifier = Modifier,
                        thickness = 1.dp,
                        color = OnSurfaceColor
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DepartmentsSearchBar(
    title: String,
    placeholder: String,
    sheetState: SheetState,
    list: List<Department>,
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    onDismiss: () -> Unit,
    onClickItem: (Department) -> Unit
) {
    SearchBarBottomSheet(
        modifier = Modifier,
        sheetState = sheetState,
        title = title,
        placeholder = placeholder,
        query = query,
        onQueryChange = onQueryChange,
        onClear = onClear,
        onDismiss = onDismiss
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight(0.75f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(list) { department ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onClickItem(department)
                        }
                ) {
                    Text(
                        modifier = Modifier,
                        text = department.name,
                        textAlign = TextAlign.Start,
                        fontFamily = robotoFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = OnSurfaceColor
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    HorizontalDivider(
                        modifier = Modifier,
                        thickness = 1.dp,
                        color = OnSurfaceColor
                    )
                }
            }
        }
    }
}