package com.gwolf.coffeetea.presentation.screen.checkout.pages

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gwolf.coffeetea.domain.entities.Address
import com.gwolf.coffeetea.domain.entities.City
import com.gwolf.coffeetea.domain.entities.Department
import com.gwolf.coffeetea.domain.usecase.database.add.AddAddressUseCase
import com.gwolf.coffeetea.domain.usecase.database.get.GetAddressesUseCase
import com.gwolf.coffeetea.domain.usecase.novapost.GetCityBySearchUseCase
import com.gwolf.coffeetea.domain.usecase.novapost.GetDepartmentsUseCase
import com.gwolf.coffeetea.presentation.component.SavedDeliveryAddressType
import com.gwolf.coffeetea.util.DataResult
import com.gwolf.coffeetea.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class DeliveryScreenState(
    val listAddresses: List<Address> = listOf(),
    val selection: DeliverySelectionState = DeliverySelectionState(),
    val search: DeliverySearchState = DeliverySearchState(),
    val isLoading: Boolean = false,
    val error: UiText = UiText.DynamicString(""),
)

data class DeliverySelectionState(
    val selectedNovaPostDepartments: Boolean = false,
    val selectedNovaPostCabin: Boolean = false,
    val selectedCity: City? = null,
    val selectedDepartment: Department? = null
)

data class DeliverySearchState(
    val typeByRef: String = "",
    val searchCitiesList: List<City> = listOf(),
    val searchDepartmentsList: List<Department> = listOf(),
    val searchCity: String = "",
    val searchDepartment: String = ""
)

sealed class DeliveryIntent {
    sealed class Input {
        data class SearchCity(val query: String) : DeliveryIntent()
        data class SearchDepartment(val query: String) : DeliveryIntent()
    }

    sealed class ButtonClick {
        data class SetTypeDepartment(val typeByRef: String) : DeliveryIntent()
        data class SelectCity(val city: City) : DeliveryIntent()
        data class SelectDepartment(val department: Department) : DeliveryIntent()
        data object ClearSelected : DeliveryIntent()
        data object SelectNovaPostDepartments : DeliveryIntent()
        data object SelectNovaPost : DeliveryIntent()
        data class SetSelectAddress(val address: Address) : DeliveryIntent()
        data object Submit : DeliveryIntent()
    }
}

sealed class DeliveryEvent {
    data object Idle : DeliveryEvent()
    data object Navigate : DeliveryEvent()
}

@HiltViewModel
class DeliveryViewModel @Inject constructor(
    private val getCityBySearchUseCase: GetCityBySearchUseCase,
    private val getDepartmentsUseCase: GetDepartmentsUseCase,
    private val getAddressUseCase: GetAddressesUseCase,
    private val addAddressUseCase: AddAddressUseCase
) : ViewModel() {

    private var _state = MutableStateFlow(DeliveryScreenState())
    val state: StateFlow<DeliveryScreenState> = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = DeliveryScreenState()
    )

    private var _event: Channel<DeliveryEvent> = Channel()
    val event = _event.receiveAsFlow()

    fun onIntent(intent: DeliveryIntent) {
        when (intent) {
            is DeliveryIntent.Input.SearchCity -> {
                _state.update { it.copy(search = it.search.copy(searchCity = intent.query)) }
            }

            is DeliveryIntent.Input.SearchDepartment -> {
                _state.update { it.copy(search = it.search.copy(searchDepartment = intent.query)) }
            }

            is DeliveryIntent.ButtonClick.SetTypeDepartment -> {
                if (_state.value.search.typeByRef != intent.typeByRef) {
                    viewModelScope.launch {
                        getDepartments("", intent.typeByRef)
                    }
                }
                _state.update { it.copy(search = it.search.copy(typeByRef = intent.typeByRef)) }
            }

            is DeliveryIntent.ButtonClick.SelectCity -> {
                _state.update { it.copy(selection = it.selection.copy(selectedCity = intent.city)) }
            }

            is DeliveryIntent.ButtonClick.SelectDepartment -> {
                _state.update { it.copy(selection = it.selection.copy(selectedDepartment = intent.department)) }
            }

            is DeliveryIntent.ButtonClick.ClearSelected -> {
                _state.update {
                    it.copy(
                        selection = it.selection.copy(
                            selectedNovaPostDepartments = false,
                            selectedNovaPostCabin = false
                        ),
                        search = it.search.copy(typeByRef = "")
                    )
                }
            }

            is DeliveryIntent.ButtonClick.SelectNovaPostDepartments -> {
                _state.update {
                    it.copy(
                        selection = it.selection.copy(
                            selectedNovaPostDepartments = true,
                            selectedNovaPostCabin = false
                        )
                    )
                }
            }

            is DeliveryIntent.ButtonClick.SelectNovaPost -> {
                _state.update {
                    it.copy(
                        selection = it.selection.copy(
                            selectedNovaPostDepartments = false,
                            selectedNovaPostCabin = true
                        )
                    )
                }
            }

            is DeliveryIntent.ButtonClick.SetSelectAddress -> {
                setSelectedAddress(intent.address)
            }

            is DeliveryIntent.ButtonClick.Submit -> {
                val selectedCity = _state.value.selection.selectedCity != null
                val selectedDepartment = _state.value.selection.selectedDepartment != null
                if (selectedCity && selectedDepartment) {
                    addAddress()
                }
            }
        }
    }

    private fun addAddress() {
        val selectedCity = _state.value.selection.selectedCity
        val selectedDepartment = _state.value.selection.selectedDepartment
        val isDefault = _state.value.listAddresses.isEmpty()
        val type = when {
            _state.value.selection.selectedNovaPostDepartments -> {
                SavedDeliveryAddressType.NovaPostDepartment.value
            }

            _state.value.selection.selectedNovaPostCabin -> {
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
                        _event.send(DeliveryEvent.Navigate)
                    }

                    is DataResult.Error -> {
                        _state.update {
                            it.copy(
                                error = UiText.DynamicString(response.exception.message.orEmpty()),
                                isLoading = false
                            )
                        }
                    }
                }
            }
        }
    }

    private suspend fun getAddresses() {
        getAddressUseCase.invoke().collect { response ->
            when (response) {
                is DataResult.Success -> {
                    val list = response.data.sortedBy { address ->
                        !address.isDefault
                    }
                    _state.update { it.copy(listAddresses = list) }
                    if (list.isNotEmpty()) {
                        val default = _state.value.listAddresses.find { address ->
                            address.isDefault
                        }
                        if (default != null) {
                            setSelectedAddress(default)
                        }
                    }
                }

                is DataResult.Error -> {
                    _state.update {
                        it.copy(
                            error = UiText.DynamicString(response.exception.message.orEmpty()),
                            isLoading = false
                        )
                    }
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

        if (address.deliveryType == SavedDeliveryAddressType.NovaPostDepartment.value) {
            _state.update {
                it.copy(
                    selection = it.selection.copy(
                        selectedNovaPostDepartments = true,
                        selectedNovaPostCabin = false
                    )
                )
            }
        } else {
            _state.update {
                it.copy(
                    selection = it.selection.copy(
                        selectedNovaPostCabin = true,
                        selectedNovaPostDepartments = false
                    )
                )
            }
        }
        _state.update {
            it.copy(
                selection = it.selection.copy(
                    selectedCity = selectedCity,
                    selectedDepartment = selectedDepartment,
                )
            )
        }
    }

    @OptIn(FlowPreview::class)
    private suspend fun setupSearchAddressDebounce() {
        snapshotFlow { _state.value.search.searchCity }
            .debounce(500)
            .distinctUntilChanged()
            .onEach {
                _state.update { it.copy(search = it.search.copy(searchCitiesList = listOf())) }
            }
            .filter { it.isNotBlank() }
            .collect { query ->
                getCities(query)
            }
    }

    @OptIn(FlowPreview::class)
    private suspend fun setupSearchDepartmentDebounce() {
        snapshotFlow { _state.value.search.searchDepartment }
            .debounce(500)
            .distinctUntilChanged()
            .onEach {
                _state.update { it.copy(search = it.search.copy(searchDepartmentsList = listOf())) }
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
                    _state.update { it.copy(search = it.search.copy(searchCitiesList = response.data)) }
                }

                is DataResult.Error -> {
                    _state.update { it.copy(error = UiText.DynamicString(response.exception.message.orEmpty())) }
                }
            }
        }
    }

    private suspend fun getDepartments(query: String, type: String = "") {
        val typeByRef = type.ifBlank { _state.value.search.typeByRef }
        val cityRef = _state.value.selection.selectedCity?.ref
        getDepartmentsUseCase.invoke(typeByRef, cityRef.orEmpty(), query).collect { response ->
            when (response) {
                is DataResult.Success -> {
                    _state.update { it.copy(search = it.search.copy(searchDepartmentsList = response.data)) }
                }

                is DataResult.Error -> {
                    _state.update { it.copy(error = UiText.DynamicString(response.exception.message.orEmpty())) }
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
                setupSearchAddressDebounce()
                setupSearchDepartmentDebounce()
            } catch (e: Exception) {
                Timber.d("Error loading delivery page data: ${e.message}")
            }
        }
    }
}