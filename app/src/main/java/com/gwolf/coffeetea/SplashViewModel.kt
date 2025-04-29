package com.gwolf.coffeetea

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gwolf.coffeetea.data.repository.local.PreferencesKey
import com.gwolf.coffeetea.domain.usecase.auth.CheckAuthUseCase
import com.gwolf.coffeetea.domain.usecase.preference.ReadBooleanPreferenceUseCase
import com.gwolf.coffeetea.navigation.Screen
import com.gwolf.coffeetea.util.DataResult
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

class SplashViewModel @Inject constructor(
    private val readBooleanPreference: ReadBooleanPreferenceUseCase,
    private val checkAuthUseCase: CheckAuthUseCase
) : ViewModel() {

    private val _startDestination: MutableState<Screen?> = mutableStateOf(null)
    val startDestination: State<Screen?> = _startDestination

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
                _startDestination.value = startDestination
            }
        }
    }

}