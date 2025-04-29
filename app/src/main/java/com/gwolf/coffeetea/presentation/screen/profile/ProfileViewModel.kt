package com.gwolf.coffeetea.presentation.screen.profile

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gwolf.coffeetea.domain.entities.Profile
import com.gwolf.coffeetea.domain.usecase.database.add.AddImageProfileUseCase
import com.gwolf.coffeetea.domain.usecase.database.get.GetProfileUseCase
import com.gwolf.coffeetea.domain.usecase.database.update.UpdateProfileImageUseCase
import com.gwolf.coffeetea.util.LOGGER_TAG
import com.gwolf.coffeetea.util.DataResult
import com.gwolf.coffeetea.util.bitmapToByteArray
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
    data class LoadImage(val bitmap: Bitmap?) : ProfileEvent()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val auth: Auth,
    private val getProfileUseCase: GetProfileUseCase,
    private val addImageProfileUseCase: AddImageProfileUseCase,
    private val updateProfileImageUseCase: UpdateProfileImageUseCase
) : ViewModel() {

    private val _profileScreenState = mutableStateOf(ProfileUiState())
    val profileScreenState: State<ProfileUiState> = _profileScreenState

    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.Exit -> {
                viewModelScope.launch {
                    auth.signOut()
                }
            }

            is ProfileEvent.LoadImage -> {
                viewModelScope.launch {
                    _profileScreenState.value =
                        _profileScreenState.value.copy(
                            isLoading = true
                        )
                    loadProfileImage(event.bitmap)
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
                    _profileScreenState.value =
                        _profileScreenState.value.copy(
                            error = response.exception.message.toString()
                        )
                }
            }
        }
    }

    private suspend fun updateProfileImage(imagePath: String) {
        updateProfileImageUseCase.invoke(imagePath).collect { response ->
            when (response) {
                is DataResult.Success -> {
                    val profile = _profileScreenState.value.profile?.copy(
                        imageUrl = response.data
                    )
                    _profileScreenState.value =
                        _profileScreenState.value.copy(
                            profile = profile,
                            isLoading = false
                        )
                }

                is DataResult.Error -> {
                    _profileScreenState.value =
                        _profileScreenState.value.copy(
                            error = response.exception.message.toString(),
                            isLoading = false
                        )
                }
            }
        }
    }


    private suspend fun getProfile() {
        getProfileUseCase.invoke().collect { response ->
            when (response) {
                is DataResult.Success -> {
                    _profileScreenState.value =
                        _profileScreenState.value.copy(
                            profile = response.data,
                        )
                }

                is DataResult.Error -> {
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
                awaitAll(profile)
                _profileScreenState.value = _profileScreenState.value.copy(isLoading = false)
            } catch (e: Exception) {
                Log.e(LOGGER_TAG, "Error loading profile screen data: ${e.message}")
            }
        }
    }

}