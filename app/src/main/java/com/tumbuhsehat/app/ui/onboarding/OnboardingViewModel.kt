package com.tumbuhsehat.app.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tumbuhsehat.app.data.repository.UserRepository
import kotlinx.coroutines.launch

class OnboardingViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun buatProfilHamil(nama: String, hphtMillis: Long, onSelesai: () -> Unit) {
        viewModelScope.launch {
            userRepository.createPregnancyProfile(nama, hphtMillis)
            onSelesai()
        }
    }

    fun buatProfilAnak(nama: String, jenisKelamin: String, tanggalLahirMillis: Long, onSelesai: () -> Unit) {
        viewModelScope.launch {
            userRepository.createChildProfile(nama, jenisKelamin, tanggalLahirMillis)
            onSelesai()
        }
    }
}
