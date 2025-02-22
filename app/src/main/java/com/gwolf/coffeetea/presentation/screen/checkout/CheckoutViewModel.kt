package com.gwolf.coffeetea.presentation.screen.checkout

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gwolf.coffeetea.util.LOGGER_TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CheckoutUiState(
    val currentStepBar: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
)

sealed class CheckoutEvent {
    data class SetStepBar(val currentStep: Int) : CheckoutEvent()
}

@HiltViewModel
class CheckoutViewModel @Inject constructor() : ViewModel() {

    private val _checkoutScreenState = mutableStateOf(CheckoutUiState())
    val checkoutScreenState: State<CheckoutUiState> = _checkoutScreenState

    fun onEvent(event: CheckoutEvent) {
        when (event) {
            is CheckoutEvent.SetStepBar -> {
                _checkoutScreenState.value = _checkoutScreenState.value.copy(
                    currentStepBar = event.currentStep
                )
            }
        }
    }

    init {
        _checkoutScreenState.value = _checkoutScreenState.value.copy(isLoading = true)
        viewModelScope.launch {

            try {

                _checkoutScreenState.value = _checkoutScreenState.value.copy(isLoading = false)
            } catch (e: Exception) {
                Log.e(LOGGER_TAG, "Error loading checkout screen data: ${e.message}")
            }
        }
    }

}