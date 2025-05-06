package com.gwolf.coffeetea.presentation.screen.checkout

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gwolf.coffeetea.util.LOGGER_TAG
import com.gwolf.coffeetea.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CheckoutScreenState(
    val currentStepBar: Int = 0,
    val isLoading: Boolean = false,
    val error: UiText = UiText.DynamicString(""),
)

sealed class CheckoutIntent {
    data class SetStepBar(val currentStep: Int) : CheckoutIntent()
}

sealed class CheckoutEvent {
    data object Idle : CheckoutEvent()
    data class StepBarChanged(val newPage: Int) : CheckoutEvent()
}

@HiltViewModel
class CheckoutViewModel @Inject constructor() : ViewModel() {

    private var _state = MutableStateFlow(CheckoutScreenState())
    val state: StateFlow<CheckoutScreenState> = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = CheckoutScreenState()
    )

    private var _event: Channel<CheckoutEvent> = Channel()
    val event = _event.receiveAsFlow()

    fun onIntent(intent: CheckoutIntent) {
        when (intent) {
            is CheckoutIntent.SetStepBar -> {
                _state.update { it.copy(currentStepBar = intent.currentStep) }
                viewModelScope.launch {
                    _event.send(CheckoutEvent.StepBarChanged(intent.currentStep))
                }
            }
        }
    }

    init {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {

            try {

                _state.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                Log.e(LOGGER_TAG, "Error loading checkout screen data: ${e.message}")
            }
        }
    }
}