package com.tumbuhsehat.app.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/** Generic factory: `viewModel(factory = viewModelFactory { MyViewModel(container.userRepository) })` */
class GenericViewModelFactory(private val creator: () -> ViewModel) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = creator() as T
}

fun viewModelFactory(creator: () -> ViewModel): GenericViewModelFactory = GenericViewModelFactory(creator)
