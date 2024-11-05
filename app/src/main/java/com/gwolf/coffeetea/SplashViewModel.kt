package com.gwolf.coffeetea

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gwolf.coffeetea.data.local.PreferencesKey
import com.gwolf.coffeetea.domain.usecase.preference.ReadBooleanPreferenceUseCase
import com.gwolf.coffeetea.navigation.Screen
import kotlinx.coroutines.launch
import javax.inject.Inject

class SplashViewModel @Inject constructor(
    private val readBooleanPreference: ReadBooleanPreferenceUseCase,
) : ViewModel() {

    private val _startDestination: MutableState<Screen?> = mutableStateOf(null)
    val startDestination: State<Screen?> = _startDestination

    init {
        viewModelScope.launch {
            readBooleanPreference.invoke(PreferencesKey.onBoardingKey).collect { result ->
                _startDestination.value = if (result) Screen.Auth else Screen.Welcome
            }
        }
    }

}