package com.gwolf.coffeetea.presentation.screen.checkout

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gwolf.coffeetea.R
import com.gwolf.coffeetea.domain.model.City
import com.gwolf.coffeetea.domain.model.Department
import com.gwolf.coffeetea.presentation.component.PostComponent
import com.gwolf.coffeetea.presentation.component.SearchBarBottomSheet
import com.gwolf.coffeetea.ui.theme.NovaPostColor
import com.gwolf.coffeetea.ui.theme.OnSurfaceColor
import com.gwolf.coffeetea.ui.theme.OutlineColor
import com.gwolf.coffeetea.ui.theme.robotoFontFamily
import com.gwolf.coffeetea.util.NOVA_POST_CABINE_REF
import com.gwolf.coffeetea.util.NOVA_POST_DEPARTMENT_REF


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryPage(
    snackbarHostState: SnackbarHostState,
    state: CheckoutUiState,
    viewModel: CheckoutViewModel
) {
    Column {
        val sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )
        var showDepartmentSearchBarBottomSheet by remember { mutableStateOf(false) }
        val postEnabled = state.selectedCity != null

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
            departmentName = if (state.selectedDepartment != null) state.selectedDepartment.name else stringResource(
                R.string.placeholder_department
            ),
            priceTitle = stringResource(R.string.nova_post_departments_price),
            selected = state.selectedNovaPostDepartments,
            enabled = postEnabled,
            onSelectedChange = {
                viewModel.onEvent(CheckoutEvent.SetTypeDepartment(NOVA_POST_DEPARTMENT_REF))
                viewModel.onEvent(CheckoutEvent.SelectNovaPostDepartments)
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
            departmentName = if (state.selectedDepartment != null) state.selectedDepartment.name else stringResource(
                R.string.placeholder_department_cabin
            ),
            priceTitle = stringResource(R.string.nova_post_price),
            selected = state.selectedNovaPost,
            enabled = postEnabled,
            onSelectedChange = {
                viewModel.onEvent(CheckoutEvent.SetTypeDepartment(NOVA_POST_CABINE_REF))
                viewModel.onEvent(CheckoutEvent.SelectNovaPost)
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
                    viewModel.onEvent(CheckoutEvent.SearchDepartment(query))
                },
                onClear = {
                    viewModel.onEvent(CheckoutEvent.SearchDepartment(""))
                },
                onDismiss = {
                    showDepartmentSearchBarBottomSheet = false
                },
                onClickItem = { department ->
                    viewModel.onEvent(CheckoutEvent.SelectDepartment(department))
                    showDepartmentSearchBarBottomSheet = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddressBlock(
    state: CheckoutUiState,
    viewModel: CheckoutViewModel
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
                viewModel.onEvent(CheckoutEvent.ClearSelected)
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
                viewModel.onEvent(CheckoutEvent.SearchCity(query))
            },
            onClear = {
                viewModel.onEvent(CheckoutEvent.SearchCity(""))
            },
            onDismiss = {
                showAddressSearchBarBottomSheet = false
            },
            onClickItem = { city ->
                viewModel.onEvent(CheckoutEvent.SelectCity(city))
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