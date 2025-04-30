package com.gwolf.coffeetea.presentation.screen.addaddress

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.gwolf.coffeetea.LocalSnackbarHostState
import com.gwolf.coffeetea.R
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
                    .clickable {
                        navigateBack()
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
            AddressBlock(
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
                SearchBar(
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
                    itemText = { city -> city.name }
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
private fun AddressBlock(
    state: AddAddressScreenState,
    onIntent: (AddAddressIntent) -> Unit
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
                onIntent(AddAddressIntent.ButtonClick.ClearSelected)
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
                if (state.selection.selectedCity != null) state.selection.selectedCity.name else stringResource(R.string.choose_city)
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
        SearchBar(
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
                onIntent(AddAddressIntent.Input.SelectCity(city))
                showAddressSearchBarBottomSheet = false
            },
            itemText = { city -> city.name }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T>SearchBar(
    title: String,
    placeholder: String,
    sheetState: SheetState,
    list: List<T>,
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    onDismiss: () -> Unit,
    onClickItem: (T) -> Unit,
    itemText: (T) -> String
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
            items(list) { item ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onClickItem(item)
                        }
                ) {
                    Text(
                        modifier = Modifier,
                        text = itemText(item),
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