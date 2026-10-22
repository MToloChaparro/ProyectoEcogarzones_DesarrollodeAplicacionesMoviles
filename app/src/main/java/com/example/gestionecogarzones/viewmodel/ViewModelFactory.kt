package com.example.gestionecogarzones.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gestionecogarzones.data.repository.SolicitudRepository

class ViewModelFactory(
    private val repository: SolicitudRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SolicitudViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SolicitudViewModel(repository) as T
        }

        if (modelClass.isAssignableFrom(AdminViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AdminViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}