package com.gwolf.coffeetea.presentation.screen.savedaddresses

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gwolf.coffeetea.domain.entities.Address
import com.gwolf.coffeetea.domain.usecase.database.get.GetAddressListUseCase
import com.gwolf.coffeetea.domain.usecase.database.remove.RemoveSavedDeliveryUseCase
import com.gwolf.coffeetea.domain.usecase.database.update.UpdateSavedAddressUseCase
import com.gwolf.coffeetea.util.LOGGER_TAG
import com.gwolf.coffeetea.util.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SavedAddressesUiState(
    val listAddresses: List<Address> = listOf<Address>(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

sealed class SavedAddressesEvent {
    data class SelectDefaultAddress(val address: Address) : SavedAddressesEvent()
    data class RemoveAddress(val addressId: String) : SavedAddressesEvent()
}

@HiltViewModel
class SavedAddressesViewModel @Inject constructor(
    private val getAddressListUseCase: GetAddressListUseCase,
    private val updateSavedAddressUseCase: UpdateSavedAddressUseCase,
    private val removeSavedDeliveryUseCase: RemoveSavedDeliveryUseCase,
) : ViewModel() {

    private val _savedAddressesScreenState = mutableStateOf(SavedAddressesUiState())
    val savedAddressesScreenState: State<SavedAddressesUiState> = _savedAddressesScreenState

    fun onEvent(event: SavedAddressesEvent) {
        when (event) {
            is SavedAddressesEvent.SelectDefaultAddress -> {
                updateAddresses(event.address)
            }

            is SavedAddressesEvent.RemoveAddress -> {
                removeAddress(event.addressId)
            }
        }
    }

    private fun removeAddress(addressId: String) {
        viewModelScope.launch {
            removeSavedDeliveryUseCase.invoke(
                addressId = addressId
            ).collect { response ->
                when (response) {
                    is DataResult.Success -> {
                        val updatedAddressesList =
                            _savedAddressesScreenState.value.listAddresses.toMutableList().apply {
                                removeIf { it.id == addressId }
                            }
                        _savedAddressesScreenState.value = _savedAddressesScreenState.value.copy(
                            listAddresses = updatedAddressesList
                        )
                    }

                    is DataResult.Error -> {
                        _savedAddressesScreenState.value =
                            _savedAddressesScreenState.value.copy(
                                error = response.exception.message.toString(),
                                isLoading = false
                            )
                    }
                }
            }
        }
    }

    private fun updateAddresses(address: Address) {
        viewModelScope.launch {
            updateSavedAddressUseCase.invoke(
                addressId = address.id,
                type = address.deliveryType,
                refCity = address.refCity,
                refAddress = address.refAddress,
                city = address.city,
                address = address.address,
                isDefault = !address.isDefault
            ).collect { response ->
                when (response) {
                    is DataResult.Success -> {
                        val updatedAddressesList =
                            _savedAddressesScreenState.value.listAddresses.map { existingAddress ->
                                existingAddress.copy(isDefault = false)
                            }.toMutableList().apply {
                                indexOfFirst { it.id == address.id }
                                    .takeIf { it != -1 }
                                    ?.let { index -> set(index, response.data) }

                            }
                        _savedAddressesScreenState.value = _savedAddressesScreenState.value.copy(
                            listAddresses = updatedAddressesList
                        )
                    }

                    is DataResult.Error -> {
                        _savedAddressesScreenState.value =
                            _savedAddressesScreenState.value.copy(
                                error = response.exception.message.toString(),
                                isLoading = false
                            )
                    }
                }
            }
        }
    }

    private suspend fun getAddresses() {
        getAddressListUseCase.invoke().collect { response ->
            when (response) {
                is DataResult.Success -> {
                    _savedAddressesScreenState.value =
                        _savedAddressesScreenState.value.copy(
                            listAddresses = response.data
                        )
                }

                is DataResult.Error -> {
                    _savedAddressesScreenState.value =
                        _savedAddressesScreenState.value.copy(
                            error = response.exception.message.toString(),
                            isLoading = false
                        )
                }
            }
        }
    }

    init {
        _savedAddressesScreenState.value = _savedAddressesScreenState.value.copy(isLoading = true)
        viewModelScope.launch {
            val addresses = async { getAddresses() }

            try {
                awaitAll(addresses)
                _savedAddressesScreenState.value =
                    _savedAddressesScreenState.value.copy(isLoading = false)
            } catch (e: Exception) {
                Log.e(LOGGER_TAG, "Error loading saved addresses screen data: ${e.message}")
            }
        }
    }
}