package com.gwolf.coffeetea.presentation.screen.profile

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gwolf.coffeetea.domain.model.Profile
import com.gwolf.coffeetea.domain.usecase.database.get.GetProfileUseCase
import com.gwolf.coffeetea.util.UiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.auth.Auth
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val profile: Profile? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)

sealed class ProfileEvent {
    data object Exit : ProfileEvent()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val auth: Auth,
    private val getProfileUseCase: GetProfileUseCase
) : ViewModel() {

    private val _profileScreenState = mutableStateOf(ProfileUiState())
    val profileScreenState: State<ProfileUiState> = _profileScreenState

    fun onEvent(event: ProfileEvent) {
        when(event) {
            is ProfileEvent.Exit -> {
                viewModelScope.launch {
                    auth.signOut()
                }
            }
        }
    }

    private suspend fun getProfile() {
        getProfileUseCase.invoke().collect { response ->
            when (response) {
                is UiResult.Success -> {
                    _profileScreenState.value =
                        _profileScreenState.value.copy(
                            profile = response.data,
                        )
                }

                is UiResult.Error -> {
                    _profileScreenState.value =
                        _profileScreenState.value.copy(
                            error = response.exception.message.toString(),
                            isLoading = false
                        )
                }
            }
        }
    }


    init {
        _profileScreenState.value = _profileScreenState.value.copy(isLoading = true)
        viewModelScope.launch {
            val profile = async { getProfile() }

            try {
                awaitAll(profile, )
                _profileScreenState.value = _profileScreenState.value.copy(isLoading = false)
            } catch (e: Exception) {
                Log.e("Coffee&TeaLogger", "Error loading profile screen data: ${e.message}")
            }
        }
    }

}