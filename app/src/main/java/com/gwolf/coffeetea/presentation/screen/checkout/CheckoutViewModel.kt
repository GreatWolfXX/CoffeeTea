package com.gwolf.coffeetea.presentation.screen.checkout

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gwolf.coffeetea.domain.model.City
import com.gwolf.coffeetea.domain.model.Department
import com.gwolf.coffeetea.domain.model.Profile
import com.gwolf.coffeetea.domain.usecase.database.get.GetProfileUseCase
import com.gwolf.coffeetea.domain.usecase.novapost.GetCityBySearchUseCase
import com.gwolf.coffeetea.domain.usecase.novapost.GetDepartmentsUseCase
import com.gwolf.coffeetea.domain.usecase.validate.ValidatePhoneUseCase
import com.gwolf.coffeetea.domain.usecase.validate.ValidateTextUseCase
import com.gwolf.coffeetea.util.LOGGER_TAG
import com.gwolf.coffeetea.util.UKRAINE_PHONE_CODE
import com.gwolf.coffeetea.util.UiResult
import com.gwolf.coffeetea.util.UiText
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

data class CheckoutUiState(
    val currentStepBar: Int = 0,
    val selectedNovaPostDepartments: Boolean = false,
    val selectedNovaPost: Boolean = false,
    val typeByRef: String = "",
    val searchCitiesList: List<City> = listOf<City>(),
    val searchDepartmentsList: List<Department> = listOf<Department>(),
    val searchCity: String = "",
    val searchDepartment: String = "",
    val selectedCity: City? = null,
    val selectedDepartment: Department? = null,

    val profile: Profile? = null,
    val phone: String = "",
    val phoneError: UiText? = null,
    val firstName: String = "",
    val firstNameError: UiText? = null,
    val lastName: String = "",
    val lastNameError: UiText? = null,

    val isLoading: Boolean = false,
    val error: String? = null,
)

sealed class CheckoutEvent {
    data class SetStepBar(val currentSte: Int) : CheckoutEvent()

    data class SetTypeDepartment(val typeByRef: String) : CheckoutEvent()
    data class SearchCity(val query: String) : CheckoutEvent()
    data class SelectCity(val city: City) : CheckoutEvent()
    data class SearchDepartment(val query: String) : CheckoutEvent()
    data class SelectDepartment(val department: Department) : CheckoutEvent()
    data object ClearSelected : CheckoutEvent()
    data object SelectNovaPostDepartments : CheckoutEvent()
    data object SelectNovaPost : CheckoutEvent()

    data class PhoneChanged(val phone: String) : CheckoutEvent()
    data class FirstNameChanged(val firstName: String) : CheckoutEvent()
    data class LastNameChanged(val lastName: String) : CheckoutEvent()
}

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val getCityBySearchUseCase: GetCityBySearchUseCase,
    private val getDepartmentsUseCase: GetDepartmentsUseCase,
    private val validatePhoneUseCase: ValidatePhoneUseCase,
    private val validateTextUseCase: ValidateTextUseCase,
    private val getProfileUseCase: GetProfileUseCase,
) : ViewModel() {

    private val _checkoutScreenState = mutableStateOf(CheckoutUiState())
    val checkoutScreenState: State<CheckoutUiState> = _checkoutScreenState

    fun onEvent(event: CheckoutEvent) {
        when (event) {
            is CheckoutEvent.SetStepBar -> {
                _checkoutScreenState.value = _checkoutScreenState.value.copy(
                    currentStepBar = event.currentSte
                )
            }

            is CheckoutEvent.SetTypeDepartment -> {
                if(_checkoutScreenState.value.typeByRef != event.typeByRef) {
                    viewModelScope.launch {
                        getDepartments("", event.typeByRef)
                    }
                }
                _checkoutScreenState.value = _checkoutScreenState.value.copy(
                    typeByRef = event.typeByRef
                )
            }

            is CheckoutEvent.SearchCity -> {
                _checkoutScreenState.value = _checkoutScreenState.value.copy(
                    searchCity = event.query
                )
            }

            is CheckoutEvent.SelectCity -> {
                _checkoutScreenState.value = _checkoutScreenState.value.copy(
                    selectedCity = event.city
                )
            }

            is CheckoutEvent.SearchDepartment -> {
                _checkoutScreenState.value = _checkoutScreenState.value.copy(
                    searchDepartment = event.query
                )
            }

            is CheckoutEvent.SelectDepartment -> {
                _checkoutScreenState.value = _checkoutScreenState.value.copy(
                    selectedDepartment = event.department
                )
            }

            is CheckoutEvent.ClearSelected -> {
                _checkoutScreenState.value = _checkoutScreenState.value.copy(
                    selectedNovaPostDepartments = false,
                    selectedNovaPost = false,
                    typeByRef = ""
                )
            }

            is CheckoutEvent.SelectNovaPostDepartments -> {
                _checkoutScreenState.value = _checkoutScreenState.value.copy(
                    selectedNovaPostDepartments = true,
                    selectedNovaPost = false,
                )
            }

            is CheckoutEvent.SelectNovaPost -> {
                _checkoutScreenState.value = _checkoutScreenState.value.copy(
                    selectedNovaPostDepartments = false,
                    selectedNovaPost = true
                )
            }

            is CheckoutEvent.PhoneChanged -> {
                _checkoutScreenState.value = _checkoutScreenState.value.copy(phone = event.phone)
                validatePhone()
            }

            is CheckoutEvent.FirstNameChanged -> {
                _checkoutScreenState.value =
                    _checkoutScreenState.value.copy(firstName = event.firstName)
                validateFirstName()
            }

            is CheckoutEvent.LastNameChanged -> {
                _checkoutScreenState.value =
                    _checkoutScreenState.value.copy(lastName = event.lastName)
                validateLastName()
            }
        }
    }

    private suspend fun getProfile() {
        getProfileUseCase.invoke().collect { response ->
            when (response) {
                is UiResult.Success -> {
                    _checkoutScreenState.value =
                        _checkoutScreenState.value.copy(
                            profile = response.data,
                            phone = response.data.phone.removePrefix ("+38"),
                            firstName = response.data.firstName,
                            lastName = response.data.lastName
                        )
                }

                is UiResult.Error -> {
                    _checkoutScreenState.value =
                        _checkoutScreenState.value.copy(
                            error = response.exception.message.toString(),
                            isLoading = false
                        )
                }
            }
        }
    }

    @OptIn(FlowPreview::class)
    private suspend fun setupSearchAddressDebounce() {
        snapshotFlow { _checkoutScreenState.value.searchCity }
            .debounce(500)
            .distinctUntilChanged()
            .onEach {
                _checkoutScreenState.value =
                    _checkoutScreenState.value.copy(
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
        snapshotFlow { _checkoutScreenState.value.searchDepartment }
            .debounce(500)
            .distinctUntilChanged()
            .onEach {
                _checkoutScreenState.value =
                    _checkoutScreenState.value.copy(
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
                    _checkoutScreenState.value =
                        _checkoutScreenState.value.copy(
                            searchCitiesList = response.data
                        )
                }

                is UiResult.Error -> {
                    _checkoutScreenState.value =
                        _checkoutScreenState.value.copy(
                            error = response.exception.message.toString()
                        )
                }
            }
        }
    }

    private suspend fun getDepartments(query: String, type: String = "") {
        val typeByRef = type.ifBlank { _checkoutScreenState.value.typeByRef }
        val cityRef = _checkoutScreenState.value.selectedCity?.ref
        getDepartmentsUseCase.invoke(typeByRef, cityRef.orEmpty(), query).collect { response ->
            when (response) {
                is UiResult.Success -> {
                    _checkoutScreenState.value =
                        _checkoutScreenState.value.copy(
                            searchDepartmentsList = response.data
                        )
                }

                is UiResult.Error -> {
                    _checkoutScreenState.value =
                        _checkoutScreenState.value.copy(
                            error = response.exception.message.toString()
                        )
                }
            }
        }
    }

    init {
        _checkoutScreenState.value = _checkoutScreenState.value.copy(isLoading = true)
        viewModelScope.launch {
            val profile = async { getProfile() }

            try {
                awaitAll(profile)

                _checkoutScreenState.value = _checkoutScreenState.value.copy(isLoading = false)
                setupSearchAddressDebounce()
                setupSearchDepartmentDebounce()
            } catch (e: Exception) {
                Log.e(LOGGER_TAG, "Error loading checkout screen data: ${e.message}")
            }
        }
    }

    private fun validatePhone(): Boolean {
        val newPhone = "$UKRAINE_PHONE_CODE${_checkoutScreenState.value.phone}"
        val phoneResult = validatePhoneUseCase.invoke(newPhone)
        _checkoutScreenState.value =
            _checkoutScreenState.value.copy(phoneError = phoneResult.errorMessage)
        return phoneResult.successful
    }

    private fun validateFirstName(): Boolean {
        val textResult = validateTextUseCase.invoke(_checkoutScreenState.value.firstName)
        _checkoutScreenState.value =
            _checkoutScreenState.value.copy(firstNameError = textResult.errorMessage)
        return textResult.successful
    }

    private fun validateLastName(): Boolean {
        val textResult = validateTextUseCase.invoke(_checkoutScreenState.value.lastName)
        _checkoutScreenState.value =
            _checkoutScreenState.value.copy(lastNameError = textResult.errorMessage)
        return textResult.successful
    }

}