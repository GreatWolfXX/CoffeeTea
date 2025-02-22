package com.gwolf.coffeetea.presentation.screen.checkout.pages

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gwolf.coffeetea.domain.model.Profile
import com.gwolf.coffeetea.domain.usecase.database.get.GetProfileUseCase
import com.gwolf.coffeetea.domain.usecase.validate.ValidatePhoneUseCase
import com.gwolf.coffeetea.domain.usecase.validate.ValidateTextUseCase
import com.gwolf.coffeetea.util.LOGGER_TAG
import com.gwolf.coffeetea.util.UKRAINE_PHONE_CODE
import com.gwolf.coffeetea.util.UiResult
import com.gwolf.coffeetea.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PersonalInfoUiState(
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

sealed class PersonalInfoEvent {
    data class PhoneChanged(val phone: String) : PersonalInfoEvent()
    data class FirstNameChanged(val firstName: String) : PersonalInfoEvent()
    data class LastNameChanged(val lastName: String) : PersonalInfoEvent()
}

@HiltViewModel
class PersonalInfoViewModel @Inject constructor(
    private val validatePhoneUseCase: ValidatePhoneUseCase,
    private val validateTextUseCase: ValidateTextUseCase,
    private val getProfileUseCase: GetProfileUseCase,
) : ViewModel()  {

    private val _personalInfoScreenState = mutableStateOf(PersonalInfoUiState())
    val personalInfoScreenState: State<PersonalInfoUiState> = _personalInfoScreenState

    fun onEvent(event: PersonalInfoEvent) {
        when (event) {
            is PersonalInfoEvent.PhoneChanged -> {
                _personalInfoScreenState.value = _personalInfoScreenState.value.copy(phone = event.phone)
                validatePhone()
            }

            is PersonalInfoEvent.FirstNameChanged -> {
                _personalInfoScreenState.value =
                    _personalInfoScreenState.value.copy(firstName = event.firstName)
                validateFirstName()
            }

            is PersonalInfoEvent.LastNameChanged -> {
                _personalInfoScreenState.value =
                    _personalInfoScreenState.value.copy(lastName = event.lastName)
                validateLastName()
            }
        }
    }
    
    private suspend fun getProfile() {
        getProfileUseCase.invoke().collect { response ->
            when (response) {
                is UiResult.Success -> {
                    _personalInfoScreenState.value =
                        _personalInfoScreenState.value.copy(
                            profile = response.data,
                            phone = response.data.phone.removePrefix("+38"),
                            firstName = response.data.firstName,
                            lastName = response.data.lastName
                        )
                }

                is UiResult.Error -> {
                    _personalInfoScreenState.value =
                        _personalInfoScreenState.value.copy(
                            error = response.exception.message.toString(),
                            isLoading = false
                        )
                }
            }
        }
    }
    
    init {
        _personalInfoScreenState.value = _personalInfoScreenState.value.copy(isLoading = true)
        viewModelScope.launch {
            val profile = async { getProfile() }

            try {
                awaitAll(profile)

                _personalInfoScreenState.value = _personalInfoScreenState.value.copy(isLoading = false)
            } catch (e: Exception) {
                Log.e(LOGGER_TAG, "Error loading personal info page data: ${e.message}")
            }
        }
    }

    private fun validatePhone(): Boolean {
        val newPhone = "$UKRAINE_PHONE_CODE${_personalInfoScreenState.value.phone}"
        val phoneResult = validatePhoneUseCase.invoke(newPhone)
        _personalInfoScreenState.value =
            _personalInfoScreenState.value.copy(phoneError = phoneResult.errorMessage)
        return phoneResult.successful
    }

    private fun validateFirstName(): Boolean {
        val textResult = validateTextUseCase.invoke(_personalInfoScreenState.value.firstName)
        _personalInfoScreenState.value =
            _personalInfoScreenState.value.copy(firstNameError = textResult.errorMessage)
        return textResult.successful
    }

    private fun validateLastName(): Boolean {
        val textResult = validateTextUseCase.invoke(_personalInfoScreenState.value.lastName)
        _personalInfoScreenState.value =
            _personalInfoScreenState.value.copy(lastNameError = textResult.errorMessage)
        return textResult.successful
    }
    
}