package com.example.ui.viewmodel

import com.example.data.model.Account
import com.example.data.model.Role

/**
 * Admin Panel Section targets subject to role-based access control.
 */
enum class AdminSection(val tabIndex: Int, val title: String) {
    APPROVALS(0, "Approvals"),
    CREATE_ID(1, "Create ID"),
    GAME_SKINS(2, "Game Skins"),
    AUDIT_LOGS(3, "Audit Logs"),
    SECURITY(4, "Security & Master Credentials"),
    APP_SETTINGS(5, "App Settings & Branding"),
    CODE_EDITOR(6, "Code Editor"),
    PLAYER_MANAGEMENT(7, "Player Management");

    companion object {
        fun fromTabIndex(index: Int): AdminSection {
            return entries.firstOrNull { it.tabIndex == index } ?: APPROVALS
        }
    }
}

/**
 * Granular Administrative Actions for validation.
 */
enum class AdminAction(val description: String) {
    ADD_NEW_MEMBER("Invite / Accept / Create Member ID"),
    MODIFY_GAME_SKINS("Modify Bundle, Gun, Character, Vehicle skins"),
    CHANGE_APP_NAME("Change App Name"),
    CHANGE_APP_LOGO("Change App Logo"),
    CHANGE_APP_COLOUR("Change App Colour"),
    EDIT_APP_CODE("Edit App Code"),
    MODIFY_APP_SETTINGS("Modify App Settings"),
    CHANGE_HEAD_ADMIN_PASSWORD("Change Head Admin Password"),
    DELETE_PLAYER("Delete Player Account"),
    BAN_PLAYER("Ban Player Account"),
    REMOVE_ALL_CLASSMATES("Remove All Classmates Registry"),
    MODIFY_FIREBASE("Modify Firebase Configuration")
}

/**
 * Result representing whether an administrative action or section access is permitted.
 */
sealed class PermissionResult {
    object Granted : PermissionResult()
    data class Denied(val message: String) : PermissionResult()

    val isGranted: Boolean get() = this is Granted
    val isDenied: Boolean get() = this is Denied
}

/**
 * UserRoleManager: Centralized utility that validates user permissions before granting
 * access to Admin Panel sections and operations.
 *
 * Enforces strict differentiation between Head Admin and Temporary Admin:
 * 1. ONLY HEAD ADMIN can modify the app (Name, Logo, Colour, Code, Settings, Passwords, Ban, Delete).
 * 2. TEMPORARY ADMIN can ONLY do 2 works:
 *    A) Add New Members (Approvals, Create ID, Invites)
 *    B) Modify Game Skins (Bundles, Guns, Characters, Backpacks, Cars, Skateboards, Emotes)
 * 3. HEAD ADMIN is the main owner and CANNOT be removed, deleted, or modified by anyone.
 */
object UserRoleManager {

    const val HEAD_ADMIN_ID = "ADMIN-001"

    const val TEMP_ADMIN_DENIAL_MESSAGE =
        "You Cannot Modify App. Only Head Admin Can Modify App. You Can Only Add New Members And Modify Game Skins."

    const val HEAD_ADMIN_PROTECTED_MESSAGE =
        "HEAD ADMIN CANNOT BE REMOVED! Head Admin is the main owner."

    const val MEMBER_NO_ACCESS_MESSAGE =
        "Access Denied. Only authorized Administrators can access this control center."

    /**
     * Checks if the account is the permanent Head Admin (main owner).
     */
    fun isHeadAdmin(user: Account?): Boolean {
        if (user == null) return false
        return user.id == HEAD_ADMIN_ID || user.effectiveRole == Role.ADMIN
    }

    /**
     * Checks if the account is an active Temporary Admin (1-Year election winner).
     */
    fun isTemporaryAdmin(user: Account?): Boolean {
        if (user == null) return false
        return user.effectiveRole == Role.TEMPORARY_ADMIN
    }

    /**
     * Checks whether the user has general app modification rights.
     * ONLY Head Admin can modify the app.
     */
    fun canModifyApp(user: Account?): Boolean {
        return isHeadAdmin(user)
    }

    /**
     * Checks whether the user can modify game skins.
     * Permitted for both Head Admin and Temporary Admin.
     */
    fun canModifyGameSkins(user: Account?): Boolean {
        return isHeadAdmin(user) || isTemporaryAdmin(user)
    }

    /**
     * Checks whether the user can add new members (Approvals & Direct ID Creation).
     * Permitted for both Head Admin and Temporary Admin.
     */
    fun canAddNewMembers(user: Account?): Boolean {
        return isHeadAdmin(user) || isTemporaryAdmin(user)
    }

    /**
     * Validates whether a user can access a specific Admin Panel Section.
     */
    fun validateSectionAccess(user: Account?, section: AdminSection): PermissionResult {
        if (user == null) {
            return PermissionResult.Denied(MEMBER_NO_ACCESS_MESSAGE)
        }

        // Head Admin has complete, unrestricted access to all sections
        if (isHeadAdmin(user)) {
            return PermissionResult.Granted
        }

        // Temporary Admin is strictly limited to 2 works:
        // A) Add New Members (APPROVALS, CREATE_ID)
        // B) Modify Game Skins (GAME_SKINS)
        // Audit Logs are viewable for transparency
        if (isTemporaryAdmin(user)) {
            return when (section) {
                AdminSection.APPROVALS,
                AdminSection.CREATE_ID,
                AdminSection.GAME_SKINS,
                AdminSection.AUDIT_LOGS -> PermissionResult.Granted

                AdminSection.SECURITY,
                AdminSection.APP_SETTINGS,
                AdminSection.CODE_EDITOR,
                AdminSection.PLAYER_MANAGEMENT -> {
                    PermissionResult.Denied(TEMP_ADMIN_DENIAL_MESSAGE)
                }
            }
        }

        return PermissionResult.Denied(MEMBER_NO_ACCESS_MESSAGE)
    }

    /**
     * Validates whether a user can execute a specific Administrative Action.
     */
    fun validateAction(user: Account?, action: AdminAction): PermissionResult {
        if (user == null) {
            return PermissionResult.Denied(MEMBER_NO_ACCESS_MESSAGE)
        }

        if (isHeadAdmin(user)) {
            return PermissionResult.Granted
        }

        if (isTemporaryAdmin(user)) {
            return when (action) {
                AdminAction.ADD_NEW_MEMBER,
                AdminAction.MODIFY_GAME_SKINS -> PermissionResult.Granted

                AdminAction.CHANGE_APP_NAME,
                AdminAction.CHANGE_APP_LOGO,
                AdminAction.CHANGE_APP_COLOUR,
                AdminAction.EDIT_APP_CODE,
                AdminAction.MODIFY_APP_SETTINGS,
                AdminAction.CHANGE_HEAD_ADMIN_PASSWORD,
                AdminAction.DELETE_PLAYER,
                AdminAction.BAN_PLAYER,
                AdminAction.REMOVE_ALL_CLASSMATES,
                AdminAction.MODIFY_FIREBASE -> {
                    PermissionResult.Denied(TEMP_ADMIN_DENIAL_MESSAGE)
                }
            }
        }

        return PermissionResult.Denied(MEMBER_NO_ACCESS_MESSAGE)
    }

    /**
     * Validates if actor can delete or ban a target account.
     * - Head Admin cannot be removed or banned by anyone.
     * - Temporary Admin cannot delete or ban any player.
     * - Head Admin can delete unsecured players.
     */
    fun canDeleteOrBanAccount(actor: Account?, target: Account): PermissionResult {
        // Rule: HEAD ADMIN CANNOT BE REMOVED!
        if (target.id == HEAD_ADMIN_ID || target.role == Role.ADMIN || target.isHeadAdmin) {
            return PermissionResult.Denied(HEAD_ADMIN_PROTECTED_MESSAGE)
        }

        // Non-Head Admin cannot delete anyone
        if (!isHeadAdmin(actor)) {
            return PermissionResult.Denied(TEMP_ADMIN_DENIAL_MESSAGE)
        }

        // Protected secured accounts check
        if (target.isSecured) {
            return PermissionResult.Denied("Account ${target.name} (${target.id}) is Secured and protected from deletion.")
        }

        return PermissionResult.Granted
    }
}
