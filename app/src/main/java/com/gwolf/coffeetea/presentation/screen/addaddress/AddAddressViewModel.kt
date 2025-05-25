package com.gwolf.coffeetea.presentation.screen.addaddress

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
import com.gwolf.coffeetea.util.DataResult
import com.gwolf.coffeetea.util.LocalizedText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
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

data class AddAddressScreenState(
    val selection: AddAddressSelectionState = AddAddressSelectionState(),
    val search: AddAddressSearchState = AddAddressSearchState(),
    val isLoading: Boolean = false,
    val error: LocalizedText = LocalizedText.DynamicString(""),
)

data class AddAddressSelectionState(
    val selectedNovaPostDepartments: Boolean = false,
    val selectedNovaPostCabin: Boolean = false,
    val selectedCity: City? = null,
    val selectedDepartment: Department? = null,
    val isDefault: Boolean = false,
)

data class AddAddressSearchState(
    val typeByRef: String = "",
    val searchCity: String = "",
    val searchDepartment: String = "",
    val searchCitiesList: List<City> = emptyList(),
    val searchDepartmentsList: List<Department> = emptyList(),
)

sealed class AddAddressIntent {
    sealed class Input {
        data class SearchCity(val query: String) : AddAddressIntent()
        data class SearchDepartment(val query: String) : AddAddressIntent()
    }

    sealed class ButtonClick {
        data class SetTypeDepartment(val typeByRef: String) : AddAddressIntent()
        data class SelectCity(val city: City) : AddAddressIntent()
        data class SelectDepartment(val department: Department) : AddAddressIntent()
        data object ClearSelected : AddAddressIntent()
        data object SelectNovaPostDepartments : AddAddressIntent()
        data object SelectNovaPost : AddAddressIntent()
        data object Submit : AddAddressIntent()
    }
}

sealed class AddAddressEvent {
    data object Idle : AddAddressEvent()
    data object Navigate : AddAddressEvent()
}

@HiltViewModel
class AddAddressViewModel @Inject constructor(
    private val getCityBySearchUseCase: GetCityBySearchUseCase,
    private val getDepartmentsUseCase: GetDepartmentsUseCase,
    private val addAddressUseCase: AddAddressUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private var _state = MutableStateFlow(AddAddressScreenState())
    val state: StateFlow<AddAddressScreenState> = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = AddAddressScreenState()
    )

    private var _event: Channel<AddAddressEvent> = Channel()
    val event = _event.receiveAsFlow()

    fun onIntent(intent: AddAddressIntent) {
        when (intent) {
            is AddAddressIntent.Input.SearchCity -> {
                _state.update { it.copy(search = it.search.copy(searchCity = intent.query)) }
            }

            is AddAddressIntent.ButtonClick.SelectCity -> {
                _state.update { it.copy(selection = it.selection.copy(selectedCity = intent.city)) }
            }

            is AddAddressIntent.Input.SearchDepartment -> {
                _state.update { it.copy(search = it.search.copy(searchDepartment = intent.query)) }
            }

            is AddAddressIntent.ButtonClick.SetTypeDepartment -> {
                if (_state.value.search.typeByRef != intent.typeByRef) {
                    viewModelScope.launch {
                        getDepartments("", intent.typeByRef)
                    }
                }
                _state.update { it.copy(search = it.search.copy(typeByRef = intent.typeByRef)) }
            }

            is AddAddressIntent.ButtonClick.SelectDepartment -> {
                _state.update { it.copy(selection = it.selection.copy(selectedDepartment = intent.department)) }
            }

            is AddAddressIntent.ButtonClick.ClearSelected -> {
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

            is AddAddressIntent.ButtonClick.SelectNovaPostDepartments -> {
                _state.update {
                    it.copy(
                        selection = it.selection.copy(
                            selectedNovaPostDepartments = true,
                            selectedNovaPostCabin = false
                        )
                    )
                }
            }

            is AddAddressIntent.ButtonClick.SelectNovaPost -> {
                _state.update {
                    it.copy(
                        selection = it.selection.copy(
                            selectedNovaPostDepartments = false,
                            selectedNovaPostCabin = true
                        )
                    )
                }
            }

            is AddAddressIntent.ButtonClick.Submit -> {
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
        val isDefault = _state.value.selection.isDefault
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
            _state.update { it.copy(isLoading = true) }
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
                        _event.send(AddAddressEvent.Navigate)
                    }

                    is DataResult.Error -> {
                        _state.update {
                            it.copy(error = LocalizedText.DynamicString(response.exception.message.orEmpty()))
                        }
                    }
                }
            }
            _state.update {
                it.copy(
                    isLoading = false,
                    error = LocalizedText.DynamicString("")
                )
            }
        }
    }

    @OptIn(FlowPreview::class)
    private suspend fun setupSearchAddressDebounce() {
        snapshotFlow { _state.value.search.searchCity }
            .debounce(500)
            .distinctUntilChanged()
            .onEach {
                _state.update {
                    it.copy(search = it.search.copy(searchCitiesList = listOf()))
                }
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
                _state.update {
                    it.copy(search = it.search.copy(searchDepartmentsList = listOf()))
                }
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
                    _state.update {
                        it.copy(search = it.search.copy(searchCitiesList = response.data))
                    }
                }

                is DataResult.Error -> {
                    _state.update {
                        it.copy(error = LocalizedText.DynamicString(response.exception.message.orEmpty()))
                    }
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
                    _state.update {
                        it.copy(search = it.search.copy(searchDepartmentsList = response.data))
                    }
                }

                is DataResult.Error -> {
                    _state.update {
                        it.copy(error = LocalizedText.DynamicString(response.exception.message.orEmpty()))
                    }
                }
            }
        }
    }

    init {
        val isDefault = savedStateHandle.toRoute<Screen.AddAddress>().isDefault

        _state.update {
            it.copy(
                isLoading = true,
                selection = it.selection.copy(isDefault = isDefault)
            )
        }
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = false) }
                setupSearchAddressDebounce()
                setupSearchDepartmentDebounce()
            } catch (e: Exception) {
                Timber.d("Error loading add address screen data: ${e.message}")
            }
        }
    }
}