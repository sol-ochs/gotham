package com.aurox.gotham.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurox.gotham.domain.usecase.vehicle.GetVehiclesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val getVehiclesUseCase: GetVehiclesUseCase
) : ViewModel() {

    private val _hasVehicles = MutableStateFlow<Boolean?>(null)
    val hasVehicles: StateFlow<Boolean?> = _hasVehicles.asStateFlow()

    fun checkOnboardingStatus() {
        viewModelScope.launch {
            getVehiclesUseCase().collect { vehicles ->
                _hasVehicles.value = vehicles.isNotEmpty()
            }
        }
    }
}
