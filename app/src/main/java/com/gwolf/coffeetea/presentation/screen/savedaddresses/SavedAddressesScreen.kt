package com.gwolf.coffeetea.presentation.screen.savedaddresses

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.gwolf.coffeetea.R
import com.gwolf.coffeetea.domain.entities.Address
import com.gwolf.coffeetea.navigation.Screen
import com.gwolf.coffeetea.presentation.component.CustomButton
import com.gwolf.coffeetea.presentation.component.ErrorOrEmptyComponent
import com.gwolf.coffeetea.presentation.component.ErrorOrEmptyStyle
import com.gwolf.coffeetea.presentation.component.LoadingComponent
import com.gwolf.coffeetea.presentation.component.SavedDeliveryAddressCard
import com.gwolf.coffeetea.ui.theme.BackgroundGradient
import com.gwolf.coffeetea.ui.theme.OnSurfaceColor
import com.gwolf.coffeetea.ui.theme.WhiteAlpha06
import com.gwolf.coffeetea.ui.theme.robotoFontFamily
import com.gwolf.coffeetea.util.ConnectionState
import com.gwolf.coffeetea.util.LOGGER_TAG
import com.gwolf.coffeetea.util.connectivityState

@Composable
fun SavedAddressesScreen(
    navController: NavController,
    viewModel: SavedAddressesViewModel = hiltViewModel()
) {
    val state by viewModel.savedAddressesScreenState

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
                SavedAddressesScreenContent(
                    navController = navController,
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
        modifier = Modifier,
        title = {
            Text(
                modifier = Modifier.padding(start = 4.dp),
                text = stringResource(R.string.title_saved_address),
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
                        navController.popBackStack()
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

@Composable
private fun SavedAddressesScreenContent(
    navController: NavController,
    state: SavedAddressesUiState,
    viewModel: SavedAddressesViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(bottom = 20.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SavedAddressesList(
            viewModel = viewModel,
            listAddresses = state.listAddresses
        )
        Spacer(modifier = Modifier.size(8.dp))
        CustomButton(
            text = R.string.btn_add_new_address
        ) {
            navController.navigate(Screen.AddAddress(state.listAddresses.isEmpty()))
        }
    }
}

@Composable
private fun ColumnScope.SavedAddressesList(
    viewModel: SavedAddressesViewModel,
    listAddresses: List<Address>
) {
    LazyVerticalGrid(
        modifier = Modifier.weight(0.8f),
        columns = GridCells.Adaptive(400.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(bottom = 12.dp)
    ) {
        items(listAddresses) { address ->
            SavedDeliveryAddressCard(
                typeString = address.deliveryType,
                city = address.city,
                address = address.address,
                isDefault = address.isDefault,
                onClick =  {
                    viewModel.onEvent(SavedAddressesEvent.SelectDefaultAddress(address))
                },
                onRemove = {
                    viewModel.onEvent(SavedAddressesEvent.RemoveAddress(address.id))
                }
            )
        }
    }
}