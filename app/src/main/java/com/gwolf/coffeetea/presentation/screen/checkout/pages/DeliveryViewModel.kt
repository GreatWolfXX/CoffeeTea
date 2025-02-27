package com.gwolf.coffeetea.presentation.screen.checkout.pages

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gwolf.coffeetea.domain.model.Address
import com.gwolf.coffeetea.domain.model.City
import com.gwolf.coffeetea.domain.model.Department
import com.gwolf.coffeetea.domain.usecase.database.add.AddAddressUseCase
import com.gwolf.coffeetea.domain.usecase.database.get.GetAddressListUseCase
import com.gwolf.coffeetea.domain.usecase.novapost.GetCityBySearchUseCase
import com.gwolf.coffeetea.domain.usecase.novapost.GetDepartmentsUseCase
import com.gwolf.coffeetea.presentation.component.SavedDeliveryAddressType
import com.gwolf.coffeetea.util.LOGGER_TAG
import com.gwolf.coffeetea.util.UiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DeliveryUiState(
    val listAddresses: List<Address> = listOf<Address>(),
    val selectedNovaPostDepartments: Boolean = false,
    val selectedNovaPostCabin: Boolean = false,
    val typeByRef: String = "",
    val searchCitiesList: List<City> = listOf<City>(),
    val searchDepartmentsList: List<Department> = listOf<Department>(),
    val searchCity: String = "",
    val searchDepartment: String = "",
    val selectedCity: City? = null,
    val selectedDepartment: Department? = null,
    val isAddressAdded: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
)

sealed class DeliveryEvent {
    data class SetTypeDepartment(val typeByRef: String) : DeliveryEvent()
    data class SearchCity(val query: String) : DeliveryEvent()
    data class SelectCity(val city: City) : DeliveryEvent()
    data class SearchDepartment(val query: String) : DeliveryEvent()
    data class SelectDepartment(val department: Department) : DeliveryEvent()
    data object ClearSelected : DeliveryEvent()
    data object SelectNovaPostDepartments : DeliveryEvent()
    data object SelectNovaPost : DeliveryEvent()
    data class SetSelectAddress(val address: Address) : DeliveryEvent()
    data object Submit : DeliveryEvent()
}

@HiltViewModel
class DeliveryViewModel @Inject constructor(
    private val getCityBySearchUseCase: GetCityBySearchUseCase,
    private val getDepartmentsUseCase: GetDepartmentsUseCase,
    private val getAddressUseCase: GetAddressListUseCase,
    private val addAddressUseCase: AddAddressUseCase
) : ViewModel() {

    private val _deliveryScreenState = mutableStateOf(DeliveryUiState())
    val deliveryScreenState: State<DeliveryUiState> = _deliveryScreenState

    fun onEvent(event: DeliveryEvent) {
        when (event) {
            is DeliveryEvent.SetTypeDepartment -> {
                if (_deliveryScreenState.value.typeByRef != event.typeByRef) {
                    viewModelScope.launch {
                        getDepartments("", event.typeByRef)
                    }
                }
                _deliveryScreenState.value = _deliveryScreenState.value.copy(
                    typeByRef = event.typeByRef
                )
            }

            is DeliveryEvent.SearchCity -> {
                _deliveryScreenState.value = _deliveryScreenState.value.copy(
                    searchCity = event.query
                )
            }

            is DeliveryEvent.SelectCity -> {
                _deliveryScreenState.value = _deliveryScreenState.value.copy(
                    selectedCity = event.city
                )
            }

            is DeliveryEvent.SearchDepartment -> {
                _deliveryScreenState.value = _deliveryScreenState.value.copy(
                    searchDepartment = event.query
                )
            }

            is DeliveryEvent.SelectDepartment -> {
                _deliveryScreenState.value = _deliveryScreenState.value.copy(
                    selectedDepartment = event.department
                )
            }

            is DeliveryEvent.ClearSelected -> {
                _deliveryScreenState.value = _deliveryScreenState.value.copy(
                    selectedNovaPostDepartments = false,
                    selectedNovaPostCabin = false,
                    typeByRef = ""
                )
            }

            is DeliveryEvent.SelectNovaPostDepartments -> {
                _deliveryScreenState.value = _deliveryScreenState.value.copy(
                    selectedNovaPostDepartments = true,
                    selectedNovaPostCabin = false,
                )
            }

            is DeliveryEvent.SelectNovaPost -> {
                _deliveryScreenState.value = _deliveryScreenState.value.copy(
                    selectedNovaPostDepartments = false,
                    selectedNovaPostCabin = true
                )
            }

            is DeliveryEvent.SetSelectAddress -> {
                setSelectedAddress(event.address)
            }

            is DeliveryEvent.Submit -> {
                val selectedCity = _deliveryScreenState.value.selectedCity != null
                val selectedDepartment = _deliveryScreenState.value.selectedDepartment != null
                if (selectedCity && selectedDepartment) {
                    addAddress()
                }
            }
        }
    }

    private fun addAddress() {
        val selectedCity = _deliveryScreenState.value.selectedCity
        val selectedDepartment = _deliveryScreenState.value.selectedDepartment
        val isDefault = _deliveryScreenState.value.listAddresses.isEmpty()
        val type = when {
            _deliveryScreenState.value.selectedNovaPostDepartments -> {
                SavedDeliveryAddressType.NovaPostDepartment.value
            }

            _deliveryScreenState.value.selectedNovaPostCabin -> {
                SavedDeliveryAddressType.NovaPostCabin.value
            }

            else -> {
                ""
            }
        }
        viewModelScope.launch {
            addAddressUseCase.invoke(
                type = type,
                refCity = selectedCity?.ref.orEmpty(),
                refAddress = selectedDepartment?.ref.orEmpty(),
                city = selectedCity?.name.orEmpty(),
                address = selectedDepartment?.name.orEmpty(),
                isDefault = isDefault
            ).collect { response ->
                when (response) {
                    is UiResult.Success -> {
                        _deliveryScreenState.value =
                            _deliveryScreenState.value.copy(
                                isAddressAdded = true
                            )
                    }

                    is UiResult.Error -> {
                        _deliveryScreenState.value =
                            _deliveryScreenState.value.copy(
                                error = response.exception.message.toString(),
                                isLoading = false
                            )
                    }
                }
            }
        }
    }

    private suspend fun getAddresses() {
        getAddressUseCase.invoke().collect { response ->
            when (response) {
                is UiResult.Success -> {
                     val list = response.data.sortedBy { address ->
                         !address.isDefault
                     }
                    _deliveryScreenState.value =
                        _deliveryScreenState.value.copy(
                            listAddresses = list,
                        )
                    if(list.isNotEmpty()) {
                        val default = _deliveryScreenState.value.listAddresses.find { address ->
                            address.isDefault
                        }
                        if(default != null) {
                            setSelectedAddress(default)
                        }
                    }
                }

                is UiResult.Error -> {
                    _deliveryScreenState.value =
                        _deliveryScreenState.value.copy(
                            error = response.exception.message.toString(),
                            isLoading = false
                        )
                }
            }
        }
    }
    private fun setSelectedAddress(
        address: Address
    ) {
        val selectedCity = City(
            ref = address.refCity,
            name = address.city
        )
        val selectedDepartment = Department(
            ref = address.refAddress,
            name = address.address
        )

        if(address.deliveryType == SavedDeliveryAddressType.NovaPostDepartment.value) {
            _deliveryScreenState.value = _deliveryScreenState.value.copy(
                selectedNovaPostDepartments = true,
                selectedNovaPostCabin = false
            )
        } else {
            _deliveryScreenState.value = _deliveryScreenState.value.copy(
                selectedNovaPostCabin = true,
                selectedNovaPostDepartments = false
            )
        }
        _deliveryScreenState.value = _deliveryScreenState.value.copy(
            selectedCity = selectedCity,
            selectedDepartment = selectedDepartment,
        )
    }

    @OptIn(FlowPreview::class)
    private suspend fun setupSearchAddressDebounce() {
        snapshotFlow { _deliveryScreenState.value.searchCity }
            .debounce(500)
            .distinctUntilChanged()
            .onEach {
                _deliveryScreenState.value =
                    _deliveryScreenState.value.copy(
                        searchCitiesList = listOf<City>()
                    )
            }
            .filter { it.isNotBlank() }
            .collect { query ->
                getCities(query)
            }
    }

    @OptIn(FlowPreview::class)
    private suspend fun setupSearchDepartmentDebounce() {
        snapshotFlow { _deliveryScreenState.value.searchDepartment }
            .debounce(500)
            .distinctUntilChanged()
            .onEach {
                _deliveryScreenState.value =
                    _deliveryScreenState.value.copy(
                        searchDepartmentsList = listOf<Department>()
                    )
            }
            .filter { it.isNotBlank() }
            .collect { query ->
                getDepartments(query)
            }
    }

    private suspend fun getCities(query: String) {
        getCityBySearchUseCase.invoke(query).collect { response ->
            when (response) {
                is UiResult.Success -> {
                    _deliveryScreenState.value =
                        _deliveryScreenState.value.copy(
                            searchCitiesList = response.data
                        )
                }

                is UiResult.Error -> {
                    _deliveryScreenState.value =
                        _deliveryScreenState.value.copy(
                            error = response.exception.message.toString()
                        )
                }
            }
        }
    }

    private suspend fun getDepartments(query: String, type: String = "") {
        val typeByRef = type.ifBlank { _deliveryScreenState.value.typeByRef }
        val cityRef = _deliveryScreenState.value.selectedCity?.ref
        getDepartmentsUseCase.invoke(typeByRef, cityRef.orEmpty(), query).collect { response ->
            when (response) {
                is UiResult.Success -> {
                    _deliveryScreenState.value =
                        _deliveryScreenState.value.copy(
                            searchDepartmentsList = response.data
                        )
                }

                is UiResult.Error -> {
                    _deliveryScreenState.value =
                        _deliveryScreenState.value.copy(
                            error = response.exception.message.toString()
                        )
                }
            }
        }
    }

    init {
        _deliveryScreenState.value = _deliveryScreenState.value.copy(isLoading = true)
        viewModelScope.launch {
            val addresses = async { getAddresses() }

            try {
                awaitAll(addresses)

                _deliveryScreenState.value = _deliveryScreenState.value.copy(isLoading = false)
                setupSearchAddressDebounce()
                setupSearchDepartmentDebounce()
            } catch (e: Exception) {
                Log.e(LOGGER_TAG, "Error loading delivery page data: ${e.message}")
            }
        }
    }
}