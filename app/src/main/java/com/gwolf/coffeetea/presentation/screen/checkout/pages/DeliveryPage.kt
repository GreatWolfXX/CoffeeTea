package com.gwolf.coffeetea.presentation.screen.checkout.pages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gwolf.coffeetea.LocalSnackbarHostState
import com.gwolf.coffeetea.R
import com.gwolf.coffeetea.presentation.component.CitySelector
import com.gwolf.coffeetea.presentation.component.CustomButton
import com.gwolf.coffeetea.presentation.component.CustomSearchBar
import com.gwolf.coffeetea.presentation.component.PostComponent
import com.gwolf.coffeetea.presentation.component.SavedDeliveryAddressSmallCard
import com.gwolf.coffeetea.ui.theme.NovaPostColor
import com.gwolf.coffeetea.ui.theme.OnSurfaceColor
import com.gwolf.coffeetea.ui.theme.robotoFontFamily
import com.gwolf.coffeetea.util.NOVA_POST_CABINE_REF
import com.gwolf.coffeetea.util.NOVA_POST_DEPARTMENT_REF

@Composable
fun DeliveryPage(
    viewModel: DeliveryViewModel = hiltViewModel(),
    nextStep: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val event by viewModel.event.collectAsState(initial = DeliveryEvent.Idle)

    LaunchedEffect(event) {
        when (event) {
            is DeliveryEvent.Idle -> {}
            is DeliveryEvent.Navigate -> {
                nextStep()
            }
        }
    }

    DeliveryContent(
        state = state,
        onIntent = { intent ->
            viewModel.onIntent(intent)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeliveryContent(
    state: DeliveryScreenState,
    onIntent: (DeliveryIntent) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(0.9f)
                .verticalScroll(rememberScrollState())
        ) {
            val sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            )
            var showDepartmentSearchBarBottomSheet by remember { mutableStateOf(false) }
            val postEnabled = state.selection.selectedCity != null
            AnimatedVisibility(state.listAddresses.isNotEmpty()) {
                SavedAddressBlock(
                    state = state,
                    onIntent = onIntent
                )
            }
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
                    onIntent(DeliveryIntent.ButtonClick.SetTypeDepartment(NOVA_POST_DEPARTMENT_REF))
                    onIntent(DeliveryIntent.ButtonClick.SelectNovaPostDepartments)
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
                    onIntent(DeliveryIntent.ButtonClick.SetTypeDepartment(NOVA_POST_CABINE_REF))
                    onIntent(DeliveryIntent.ButtonClick.SelectNovaPost)
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
                    onQueryChange = { query ->
                        onIntent(DeliveryIntent.Input.SearchDepartment(query))
                    },
                    onClear = {
                        onIntent(DeliveryIntent.Input.SearchDepartment(""))
                    },
                    onDismiss = {
                        showDepartmentSearchBarBottomSheet = false
                    },
                    onClickItem = { department ->
                        onIntent(DeliveryIntent.ButtonClick.SelectDepartment(department))
                        showDepartmentSearchBarBottomSheet = false
                    },
                    itemText = { department ->
                        department.name
                    }
                )
            }
        }
        Spacer(modifier = Modifier.size(16.dp))
        val btnEnabled = state.selection.selectedDepartment != null
        CustomButton(
            text = R.string.title_continue,
            isEnabled = btnEnabled
        ) {
            onIntent(DeliveryIntent.ButtonClick.Submit)
        }
    }
}

@Composable
private fun SavedAddressBlock(
    state: DeliveryScreenState,
    onIntent: (DeliveryIntent) -> Unit
) {
    Column {
        Text(
            modifier = Modifier,
            text = stringResource(R.string.title_saved_address),
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            color = OnSurfaceColor
        )
        Spacer(modifier = Modifier.size(8.dp))
        LazyRow(
            modifier = Modifier
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(state.listAddresses) { address ->
                SavedDeliveryAddressSmallCard(
                    modifier = Modifier.animateItem(),
                    typeString = address.deliveryType,
                    address = "${address.city}, ${address.address}",
                    isSelected = state.selection.selectedDepartment?.name == address.address
                ) {
                    onIntent(DeliveryIntent.ButtonClick.SetSelectAddress(address))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CityBlock(
    state: DeliveryScreenState,
    onIntent: (DeliveryIntent) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var showAddressSearchBarBottomSheet by remember { mutableStateOf(false) }
    CitySelector(
        cityName = state.selection.selectedCity?.name ?: "",
        onClick = {
            onIntent(DeliveryIntent.ButtonClick.ClearSelected)
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
            onQueryChange = { query ->
                onIntent(DeliveryIntent.Input.SearchCity(query))
            },
            onClear = {
                onIntent(DeliveryIntent.Input.SearchCity(""))
            },
            onDismiss = {
                showAddressSearchBarBottomSheet = false
            },
            onClickItem = { city ->
                onIntent(DeliveryIntent.ButtonClick.SelectCity(city))
                showAddressSearchBarBottomSheet = false
            },
            itemText = { city -> city.name }
        )
    }
}

@Preview
@Composable
private fun DeliveryPagePreview() {
    val snackbarHostState = remember { SnackbarHostState() }

    CompositionLocalProvider(
        LocalSnackbarHostState provides snackbarHostState,
    ) {
        DeliveryContent(
            state = DeliveryScreenState()
        )
    }
}