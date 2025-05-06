package com.gwolf.coffeetea

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gwolf.coffeetea.data.repository.local.PreferencesKey
import com.gwolf.coffeetea.domain.usecase.auth.CheckAuthUseCase
import com.gwolf.coffeetea.domain.usecase.preference.ReadBooleanPreferenceUseCase
import com.gwolf.coffeetea.navigation.Screen
import com.gwolf.coffeetea.util.DataResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class SplashViewModel @Inject constructor(
    private val readBooleanPreference: ReadBooleanPreferenceUseCase,
    private val checkAuthUseCase: CheckAuthUseCase
) : ViewModel() {

    private var _state = MutableStateFlow<Screen?>(null)
    val state: StateFlow<Screen?> = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = null
    )

    init {
        viewModelScope.launch {
            val readOnBoardingFlow = readBooleanPreference.invoke(PreferencesKey.onBoardingKey)
            val checkAuthFlow = checkAuthUseCase.invoke()
            readOnBoardingFlow.combine(checkAuthFlow) { readOnBoarding, checkAuth ->
                when (checkAuth) {
                    is DataResult.Success -> {
                        if (readOnBoarding) Screen.Home else Screen.Welcome
                    }

                    is DataResult.Error -> {
                        if (readOnBoarding) Screen.Auth else Screen.Welcome
                    }
                }
            }.collect { startDestination ->
                _state.update { startDestination }
            }
        }
    }
}