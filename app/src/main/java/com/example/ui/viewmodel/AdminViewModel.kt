package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Account
import com.example.data.repository.ClassmateRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * AdminViewModel: Manages Admin Panel UI state and validates administrative permissions
 * via UserRoleManager before granting access to sections or executing operations.
 */
class AdminViewModel(
    private val roleManager: UserRoleManager = UserRoleManager
) : ViewModel() {

    private val _activeSection = MutableStateFlow(AdminSection.APPROVALS)
    val activeSection: StateFlow<AdminSection> = _activeSection.asStateFlow()

    private val _permissionDeniedDialog = MutableStateFlow<String?>(null)
    val permissionDeniedDialog: StateFlow<String?> = _permissionDeniedDialog.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    /**
     * Attempts to navigate to an Admin Panel Section after permission validation.
     * Returns true if access was granted; false if denied.
     */
    fun selectSection(section: AdminSection, currentUser: Account?): Boolean {
        val result = roleManager.validateSectionAccess(currentUser, section)
        return when (result) {
            is PermissionResult.Granted -> {
                _activeSection.value = section
                _permissionDeniedDialog.value = null
                _errorMessage.value = null
                true
            }
            is PermissionResult.Denied -> {
                _permissionDeniedDialog.value = result.message
                false
            }
        }
    }

    /**
     * Attempts to navigate by tab index (0: Approvals, 1: Create ID, 2: Game Skins, 3: Logs, 4: Security)
     */
    fun selectTabByIndex(index: Int, currentUser: Account?): Boolean {
        val section = AdminSection.fromTabIndex(index)
        return selectSection(section, currentUser)
    }

    /**
     * Validates whether an administrative action is allowed.
     */
    fun validateAndExecuteAction(
        action: AdminAction,
        currentUser: Account?,
        onAllowed: () -> Unit
    ): Boolean {
        val result = roleManager.validateAction(currentUser, action)
        return when (result) {
            is PermissionResult.Granted -> {
                _permissionDeniedDialog.value = null
                onAllowed()
                true
            }
            is PermissionResult.Denied -> {
                _permissionDeniedDialog.value = result.message
                false
            }
        }
    }

    /**
     * Validates deletion of a target player account.
     */
    fun validateAndExecuteDelete(
        actor: Account?,
        target: Account,
        onConfirmed: () -> Unit
    ): Boolean {
        val result = roleManager.canDeleteOrBanAccount(actor, target)
        return when (result) {
            is PermissionResult.Granted -> {
                _permissionDeniedDialog.value = null
                onConfirmed()
                true
            }
            is PermissionResult.Denied -> {
                _permissionDeniedDialog.value = result.message
                false
            }
        }
    }

    fun dismissPermissionDialog() {
        _permissionDeniedDialog.value = null
    }

    fun setStatusMessage(msg: String?) {
        _statusMessage.value = msg
    }

    fun setErrorMessage(err: String?) {
        _errorMessage.value = err
    }
}
