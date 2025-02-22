package com.gwolf.coffeetea.presentation.screen.checkout.pages

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gwolf.coffeetea.domain.model.City
import com.gwolf.coffeetea.domain.model.Department
import com.gwolf.coffeetea.domain.usecase.novapost.GetCityBySearchUseCase
import com.gwolf.coffeetea.domain.usecase.novapost.GetDepartmentsUseCase
import com.gwolf.coffeetea.util.LOGGER_TAG
import com.gwolf.coffeetea.util.UiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DeliveryUiState(
    val selectedNovaPostDepartments: Boolean = false,
    val selectedNovaPost: Boolean = false,
    val typeByRef: String = "",
    val searchCitiesList: List<City> = listOf<City>(),
    val searchDepartmentsList: List<Department> = listOf<Department>(),
    val searchCity: String = "",
    val searchDepartment: String = "",
    val selectedCity: City? = null,
    val selectedDepartment: Department? = null,
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
}

@HiltViewModel
class DeliveryViewModel @Inject constructor(
    private val getCityBySearchUseCase: GetCityBySearchUseCase,
    private val getDepartmentsUseCase: GetDepartmentsUseCase
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
                    selectedNovaPost = false,
                    typeByRef = ""
                )
            }

            is DeliveryEvent.SelectNovaPostDepartments -> {
                _deliveryScreenState.value = _deliveryScreenState.value.copy(
                    selectedNovaPostDepartments = true,
                    selectedNovaPost = false,
                )
            }

            is DeliveryEvent.SelectNovaPost -> {
                _deliveryScreenState.value = _deliveryScreenState.value.copy(
                    selectedNovaPostDepartments = false,
                    selectedNovaPost = true
                )
            }
        }
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

            try {
                _deliveryScreenState.value = _deliveryScreenState.value.copy(isLoading = false)
                setupSearchAddressDebounce()
                setupSearchDepartmentDebounce()
            } catch (e: Exception) {
                Log.e(LOGGER_TAG, "Error loading delivery page data: ${e.message}")
            }
        }
    }
}