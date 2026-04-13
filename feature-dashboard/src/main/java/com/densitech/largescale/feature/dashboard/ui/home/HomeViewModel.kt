package com.densitech.largescale.feature.dashboard.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.densitech.largescale.contracts.AuthService
import com.densitech.largescale.contracts.Role
import com.densitech.largescale.contracts.SlotRegistry
import com.densitech.largescale.contracts.SlotIds
import com.densitech.largescale.contracts.UISlot
import com.densitech.largescale.contracts.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class HomeUiState(
    val user: User? = null,
    val currentRole: Role = Role.GUEST,
    val homeWidgets: List<UISlot> = emptyList()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authService: AuthService,
    private val slotRegistry: SlotRegistry
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = authService.currentUser.map { user ->
        val role = user?.role ?: Role.GUEST
        HomeUiState(
            user = user,
            currentRole = role,
            homeWidgets = slotRegistry.getSlotsForHost(SlotIds.HOME_WIDGETS, role)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState()
    )
}
