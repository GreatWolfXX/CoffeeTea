package com.gwolf.coffeetea.presentation.screen.profile

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gwolf.coffeetea.domain.entities.Profile
import com.gwolf.coffeetea.domain.usecase.database.add.AddImageProfileUseCase
import com.gwolf.coffeetea.domain.usecase.database.get.GetProfileUseCase
import com.gwolf.coffeetea.domain.usecase.database.update.UpdateProfileImageUseCase
import com.gwolf.coffeetea.util.DataResult
import com.gwolf.coffeetea.util.LOGGER_TAG
import com.gwolf.coffeetea.util.UiText
import com.gwolf.coffeetea.util.bitmapToByteArray
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.auth.Auth
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileScreenState(
    val profile: Profile? = null,
    val isLoading: Boolean = false,
    val error: UiText = UiText.DynamicString(""),
)

sealed class ProfileIntent {
    data class LoadImage(val bitmap: Bitmap?) : ProfileIntent()
    data object Exit : ProfileIntent()
}

sealed class ProfileEvent {
    data object Idle : ProfileEvent()
    data object NavigateToAuth : ProfileEvent()
}


@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val auth: Auth,
    private val getProfileUseCase: GetProfileUseCase,
    private val addImageProfileUseCase: AddImageProfileUseCase,
    private val updateProfileImageUseCase: UpdateProfileImageUseCase
) : ViewModel() {

    private var _state = MutableStateFlow(ProfileScreenState())
    val state: StateFlow<ProfileScreenState> = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = ProfileScreenState()
    )

    private var _event: Channel<ProfileEvent> = Channel()
    val event = _event.receiveAsFlow()

    fun onIntent(intent: ProfileIntent) {
        when (intent) {
            is ProfileIntent.LoadImage -> {
                viewModelScope.launch {
                    _state.update { it.copy(isLoading = true) }
                    loadProfileImage(intent.bitmap)
                }
            }

            is ProfileIntent.Exit -> {
                viewModelScope.launch {
                    auth.signOut()
                    _event.send(ProfileEvent.NavigateToAuth)
                }
            }
        }
    }

    private suspend fun loadProfileImage(bitmap: Bitmap?) {
        val byteArray = bitmapToByteArray(bitmap!!)
        addImageProfileUseCase.invoke(byteArray).collect { response ->
            when (response) {
                is DataResult.Success -> {
                    updateProfileImage(response.data)
                }

                is DataResult.Error -> {
                    _state.update {
                        it.copy(
                            error = UiText.DynamicString(response.exception.message.orEmpty())
                        )
                    }
                }
            }
        }
    }

    private suspend fun updateProfileImage(imagePath: String) {
        updateProfileImageUseCase.invoke(imagePath).collect { response ->
            when (response) {
                is DataResult.Success -> {
                    val profile = _state.value.profile?.copy(
                        imageUrl = response.data
                    )
                    _state.update {
                        it.copy(
                            profile = profile,
                            isLoading = false
                        )
                    }
                }

                is DataResult.Error -> {
                    _state.update {
                        it.copy(
                            error = UiText.DynamicString(response.exception.message.orEmpty()),
                            isLoading = false
                        )
                    }
                }
            }
        }
    }


    private suspend fun getProfile() {
        getProfileUseCase.invoke().collect { response ->
            when (response) {
                is DataResult.Success -> {
                    _state.update { it.copy(profile = response.data) }
                }

                is DataResult.Error -> {
                    _state.update { it.copy(error = UiText.DynamicString(response.exception.message.orEmpty())) }
                }
            }
        }
    }

    init {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val profile = async { getProfile() }

            try {
                awaitAll(profile)
                _state.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                Log.e(LOGGER_TAG, "Error loading profile screen data: ${e.message}")
            }
        }
    }
}