package com.gwolf.coffeetea.presentation.screen.addaddress

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.gwolf.coffeetea.domain.entities.City
import com.gwolf.coffeetea.domain.entities.Department
import com.gwolf.coffeetea.domain.usecase.database.add.AddAddressUseCase
import com.gwolf.coffeetea.domain.usecase.novapost.GetCityBySearchUseCase
import com.gwolf.coffeetea.domain.usecase.novapost.GetDepartmentsUseCase
import com.gwolf.coffeetea.navigation.Screen
import com.gwolf.coffeetea.presentation.component.SavedDeliveryAddressType
import com.gwolf.coffeetea.util.LOGGER_TAG
import com.gwolf.coffeetea.util.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddAddressUiState(
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
    val isDefault: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
)

sealed class AddAddressEvent {
    data class SetTypeDepartment(val typeByRef: String) : AddAddressEvent()
    data class SearchCity(val query: String) : AddAddressEvent()
    data class SelectCity(val city: City) : AddAddressEvent()
    data class SearchDepartment(val query: String) : AddAddressEvent()
    data class SelectDepartment(val department: Department) : AddAddressEvent()
    data object ClearSelected : AddAddressEvent()
    data object SelectNovaPostDepartments : AddAddressEvent()
    data object SelectNovaPost : AddAddressEvent()
    data object Submit : AddAddressEvent()
}

@HiltViewModel
class AddAddressViewModel @Inject constructor(
    private val getCityBySearchUseCase: GetCityBySearchUseCase,
    private val getDepartmentsUseCase: GetDepartmentsUseCase,
    private val addAddressUseCase: AddAddressUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _addAddressEventScreenState = mutableStateOf(AddAddressUiState())
    val addAddressEventScreenState: State<AddAddressUiState> = _addAddressEventScreenState

    fun onEvent(event: AddAddressEvent) {
        when (event) {
            is AddAddressEvent.SetTypeDepartment -> {
                if (_addAddressEventScreenState.value.typeByRef != event.typeByRef) {
                    viewModelScope.launch {
                        getDepartments("", event.typeByRef)
                    }
                }
                _addAddressEventScreenState.value = _addAddressEventScreenState.value.copy(
                    typeByRef = event.typeByRef
                )
            }

            is AddAddressEvent.SearchCity -> {
                _addAddressEventScreenState.value = _addAddressEventScreenState.value.copy(
                    searchCity = event.query
                )
            }

            is AddAddressEvent.SelectCity -> {
                _addAddressEventScreenState.value = _addAddressEventScreenState.value.copy(
                    selectedCity = event.city
                )
            }

            is AddAddressEvent.SearchDepartment -> {
                _addAddressEventScreenState.value = _addAddressEventScreenState.value.copy(
                    searchDepartment = event.query
                )
            }

            is AddAddressEvent.SelectDepartment -> {
                _addAddressEventScreenState.value = _addAddressEventScreenState.value.copy(
                    selectedDepartment = event.department
                )
            }

            is AddAddressEvent.ClearSelected -> {
                _addAddressEventScreenState.value = _addAddressEventScreenState.value.copy(
                    selectedNovaPostDepartments = false,
                    selectedNovaPostCabin = false,
                    typeByRef = ""
                )
            }

            is AddAddressEvent.SelectNovaPostDepartments -> {
                _addAddressEventScreenState.value = _addAddressEventScreenState.value.copy(
                    selectedNovaPostDepartments = true,
                    selectedNovaPostCabin = false,
                )
            }

            is AddAddressEvent.SelectNovaPost -> {
                _addAddressEventScreenState.value = _addAddressEventScreenState.value.copy(
                    selectedNovaPostDepartments = false,
                    selectedNovaPostCabin = true
                )
            }

            is AddAddressEvent.Submit -> {
                val selectedCity = _addAddressEventScreenState.value.selectedCity != null
                val selectedDepartment = _addAddressEventScreenState.value.selectedDepartment != null
                if (selectedCity && selectedDepartment) {
                    addAddress()
                }
            }
        }
    }

    private fun addAddress() {
        val selectedCity = _addAddressEventScreenState.value.selectedCity
        val selectedDepartment = _addAddressEventScreenState.value.selectedDepartment
        val isDefault = _addAddressEventScreenState.value.isDefault
        val type = when {
            _addAddressEventScreenState.value.selectedNovaPostDepartments -> {
                SavedDeliveryAddressType.NovaPostDepartment.value
            }

            _addAddressEventScreenState.value.selectedNovaPostCabin -> {
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
                    is DataResult.Success -> {
                        _addAddressEventScreenState.value =
                            _addAddressEventScreenState.value.copy(
                                isAddressAdded = true
                            )
                    }

                    is DataResult.Error -> {
                        _addAddressEventScreenState.value =
                            _addAddressEventScreenState.value.copy(
                                error = response.exception.message.toString(),
                                isLoading = false
                            )
                    }
                }
            }
        }
    }

    @OptIn(FlowPreview::class)
    private suspend fun setupSearchAddressDebounce() {
        snapshotFlow { _addAddressEventScreenState.value.searchCity }
            .debounce(500)
            .distinctUntilChanged()
            .onEach {
                _addAddressEventScreenState.value =
                    _addAddressEventScreenState.value.copy(
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
        snapshotFlow { _addAddressEventScreenState.value.searchDepartment }
            .debounce(500)
            .distinctUntilChanged()
            .onEach {
                _addAddressEventScreenState.value =
                    _addAddressEventScreenState.value.copy(
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
                is DataResult.Success -> {
                    _addAddressEventScreenState.value =
                        _addAddressEventScreenState.value.copy(
                            searchCitiesList = response.data
                        )
                }

                is DataResult.Error -> {
                    _addAddressEventScreenState.value =
                        _addAddressEventScreenState.value.copy(
                            error = response.exception.message.toString()
                        )
                }
            }
        }
    }

    private suspend fun getDepartments(query: String, type: String = "") {
        val typeByRef = type.ifBlank { _addAddressEventScreenState.value.typeByRef }
        val cityRef = _addAddressEventScreenState.value.selectedCity?.ref
        getDepartmentsUseCase.invoke(typeByRef, cityRef.orEmpty(), query).collect { response ->
            when (response) {
                is DataResult.Success -> {
                    _addAddressEventScreenState.value =
                        _addAddressEventScreenState.value.copy(
                            searchDepartmentsList = response.data
                        )
                }

                is DataResult.Error -> {
                    _addAddressEventScreenState.value =
                        _addAddressEventScreenState.value.copy(
                            error = response.exception.message.toString()
                        )
                }
            }
        }
    }

    init {
        val isDefault = savedStateHandle.toRoute<Screen.AddAddress>().isDefault

        _addAddressEventScreenState.value = _addAddressEventScreenState.value.copy(
            isLoading = true,
            isDefault = isDefault
        )
        viewModelScope.launch {

            try {
                _addAddressEventScreenState.value =
                    _addAddressEventScreenState.value.copy(isLoading = false)
                setupSearchAddressDebounce()
                setupSearchDepartmentDebounce()
            } catch (e: Exception) {
                Log.e(LOGGER_TAG, "Error loading add address screen data: ${e.message}")
            }
        }
    }
}