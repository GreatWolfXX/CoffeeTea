package com.gwolf.coffeetea.presentation.screen.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gwolf.coffeetea.data.repository.local.PreferencesKey
import com.gwolf.coffeetea.domain.usecase.preference.SaveBooleanPreferenceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val saveBooleanPreferenceUseCase: SaveBooleanPreferenceUseCase
) : ViewModel() {

    fun saveOnBoarding(completed: Boolean) {
        viewModelScope.launch {
            saveBooleanPreferenceUseCase.invoke(
                key = PreferencesKey.onBoardingKey,
                value = completed
            )
        }
    }

}