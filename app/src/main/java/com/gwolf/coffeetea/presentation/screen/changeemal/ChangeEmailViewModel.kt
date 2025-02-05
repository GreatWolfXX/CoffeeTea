package com.gwolf.coffeetea.presentation.screen.changeemal

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.gwolf.coffeetea.domain.usecase.validate.ValidateEmailUseCase
import com.gwolf.coffeetea.navigation.Screen
import com.gwolf.coffeetea.util.LOGGER_TAG
import com.gwolf.coffeetea.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChangeEmailUiState(
    val currentEmail: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val email: String = "",
    val emailError: UiText? = null,
)

sealed class ChangeEmailEvent {
    data class EmailChanged(val email: String) : ChangeEmailEvent()
    data object Save : ChangeEmailEvent()
}

@HiltViewModel
class ChangeEmailViewModel @Inject constructor(
    private val validateEmailUseCase: ValidateEmailUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _changeEmailState = mutableStateOf(ChangeEmailUiState())
    val changeEmailState: State<ChangeEmailUiState> = _changeEmailState

    fun onEvent(event: ChangeEmailEvent) {
        when (event) {
            is ChangeEmailEvent.EmailChanged -> {
                _changeEmailState.value = _changeEmailState.value.copy(email = event.email)
                validateEmail()
            }
            is ChangeEmailEvent.Save -> {

            }
        }
    }

    init {
        _changeEmailState.value = _changeEmailState.value.copy(isLoading = true)
        viewModelScope.launch {
            try {
                _changeEmailState.value = _changeEmailState.value.copy(
                    isLoading = false,
                    currentEmail = savedStateHandle.toRoute<Screen.ChangeEmail>().email
                )
            } catch (e: Exception) {
                Log.e(LOGGER_TAG, "Error loading change email screen data: ${e.message}")
            }
        }
    }

    private fun validateEmail(): Boolean {
        val emailResult = validateEmailUseCase.invoke(_changeEmailState.value.email)
        _changeEmailState.value = _changeEmailState.value.copy(emailError = emailResult.errorMessage)
        return emailResult.successful
    }

}