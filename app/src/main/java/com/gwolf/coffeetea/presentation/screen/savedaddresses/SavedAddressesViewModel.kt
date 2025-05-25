package com.gwolf.coffeetea.presentation.screen.savedaddresses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gwolf.coffeetea.domain.entities.Address
import com.gwolf.coffeetea.domain.usecase.database.get.GetAddressesUseCase
import com.gwolf.coffeetea.domain.usecase.database.remove.RemoveSavedDeliveryUseCase
import com.gwolf.coffeetea.domain.usecase.database.update.UpdateSavedAddressUseCase
import com.gwolf.coffeetea.util.DataResult
import com.gwolf.coffeetea.util.LocalizedText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class SavedAddressesScreenState(
    val listAddresses: List<Address> = listOf(),
    val isLoading: Boolean = false,
    val error: LocalizedText = LocalizedText.DynamicString(""),
)

sealed class SavedAddressesIntent {
    data class SelectDefaultAddress(val address: Address) : SavedAddressesIntent()
    data class RemoveAddress(val addressId: String) : SavedAddressesIntent()
}

@HiltViewModel
class SavedAddressesViewModel @Inject constructor(
    private val getAddressListUseCase: GetAddressesUseCase,
    private val updateSavedAddressUseCase: UpdateSavedAddressUseCase,
    private val removeSavedDeliveryUseCase: RemoveSavedDeliveryUseCase,
) : ViewModel() {

    private var _state = MutableStateFlow(SavedAddressesScreenState())
    val state: StateFlow<SavedAddressesScreenState> = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = SavedAddressesScreenState()
    )

    fun onIntent(intent: SavedAddressesIntent) {
        when (intent) {
            is SavedAddressesIntent.SelectDefaultAddress -> {
                updateAddresses(intent.address)
            }

            is SavedAddressesIntent.RemoveAddress -> {
                removeAddress(intent.addressId)
            }
        }
    }

    private fun removeAddress(addressId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            removeSavedDeliveryUseCase.invoke(
                addressId = addressId
            ).collect { response ->
                when (response) {
                    is DataResult.Success -> {
                        val updatedAddressesList =
                            _state.value.listAddresses.toMutableList().apply {
                                removeIf { it.id == addressId }
                            }
                        _state.update { it.copy(listAddresses = updatedAddressesList) }
                    }

                    is DataResult.Error -> {
                        _state.update { it.copy(error = LocalizedText.DynamicString(response.exception.message.orEmpty())) }
                    }
                }
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun updateAddresses(address: Address) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
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
                            _state.value.listAddresses.map { existingAddress ->
                                existingAddress.copy(isDefault = false)
                            }.toMutableList().apply {
                                indexOfFirst { it.id == address.id }
                                    .takeIf { it != -1 }
                                    ?.let { index -> set(index, response.data) }

                            }
                        _state.update { it.copy(listAddresses = updatedAddressesList) }
                    }

                    is DataResult.Error -> {
                        _state.update { it.copy(error = LocalizedText.DynamicString(response.exception.message.orEmpty())) }
                    }
                }
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    private suspend fun getAddresses() {
        getAddressListUseCase.invoke().collect { response ->
            when (response) {
                is DataResult.Success -> {
                    _state.update { it.copy(listAddresses = response.data) }
                }

                is DataResult.Error -> {
                    _state.update { it.copy(error = LocalizedText.DynamicString(response.exception.message.orEmpty())) }
                }
            }
        }
    }

    init {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val addresses = async { getAddresses() }

            try {
                awaitAll(addresses)
                _state.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                Timber.d("Error loading saved addresses screen data: ${e.message}")
            }
        }
    }
}