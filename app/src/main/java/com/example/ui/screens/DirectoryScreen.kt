package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Account
import com.example.data.model.Role
import com.example.data.repository.ClassmateRepository
import com.example.ui.components.AvatarCircle
import com.example.ui.components.RoleBadge
import com.example.ui.components.SecurityBadge
import com.example.ui.theme.AlumniBlue
import com.example.ui.theme.AlumniEmerald
import com.example.ui.theme.AlumniGold
import com.example.ui.theme.AlumniGoldAccent
import com.example.ui.theme.AlumniNavyDark
import com.example.ui.theme.AlumniRed

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DirectoryScreen(
    currentUser: Account,
    repository: ClassmateRepository,
    accounts: List<Account>,
    onStartPrivateChat: (partnerId: String) -> Unit,
    onOpenBattleGame: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var accountToDelete by remember { mutableStateOf<Account?>(null) }
    var accountToEditId by remember { mutableStateOf<Account?>(null) }
    var deleteErrorMessage by remember { mutableStateOf<String?>(null) }
    var actionFeedback by remember { mutableStateOf<String?>(null) }
    var showRemoveAllDialog by remember { mutableStateOf(false) }

    val isAdmin = currentUser.isAdminOrOneDayAdmin

    val filtered = accounts.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
                it.id.contains(searchQuery, ignoreCase = true) ||
                it.batchInfo.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        // Search header
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search Classmates by Name, ID, or Batch") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_directory_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${filtered.size} Classmates Registered",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (isAdmin) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val hasRemovableClassmates = accounts.any { it.role != Role.ADMIN }
                    if (hasRemovableClassmates) {
                        Button(
                            onClick = { showRemoveAllDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = AlumniRed.copy(alpha = 0.9f)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .height(34.dp)
                                .testTag("btn_remove_all_classmates")
                        ) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Remove All", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    Surface(
                        color = Color(0xFFFEF3C7),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Admin 🛡️",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF92400E),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        if (actionFeedback != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = AlumniBlue.copy(alpha = 0.15f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = actionFeedback ?: "",
                        fontSize = 12.sp,
                        color = AlumniBlue,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { actionFeedback = null },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Text("✕", fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (filtered.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth().padding(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.People,
                                    contentDescription = null,
                                    tint = AlumniBlue,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = if (searchQuery.isNotBlank()) "No classmates matching \"$searchQuery\"" else "No Classmates Registered Yet",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (searchQuery.isNotBlank()) {
                                        "Try searching with a different name or student ID."
                                    } else {
                                        "All dummy classmates have been removed. Admins can add new classmates from the Admin Panel, or students can request registration from the Login screen."
                                    },
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
            items(filtered, key = { it.id }) { acc ->
                val isSelf = acc.id == currentUser.id

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_classmate_${acc.id}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AvatarCircle(name = acc.name, size = 48)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = acc.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    if (isSelf) {
                                        Text(
                                            text = "(You)",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = AlumniBlue
                                        )
                                    }
                                }
                                Text(
                                    text = "ID: ${acc.id} • ${acc.batchInfo}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Phone,
                                        contentDescription = null,
                                        modifier = Modifier.size(12.dp),
                                        tint = if (acc.isPhoneConfirmed) AlumniEmerald else Color.Gray
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isAdmin || isSelf) acc.phone else "••••••${acc.phone.takeLast(4)}",
                                        fontSize = 11.sp,
                                        color = if (acc.isPhoneConfirmed) AlumniEmerald else Color.Gray
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    if (acc.isPhoneConfirmed) {
                                        Text(
                                            text = "• Confirmed ✓",
                                            fontSize = 10.sp,
                                            color = AlumniEmerald,
                                            fontWeight = FontWeight.Bold
                                        )
                                    } else {
                                        Text(
                                            text = "• Phone Pending",
                                            fontSize = 10.sp,
                                            color = AlumniRed,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                RoleBadge(role = acc.effectiveRole)
                                Spacer(modifier = Modifier.height(4.dp))
                                SecurityBadge(isSecured = acc.isSecured)
                            }
                        }

                        // Code disclosure for admin
                        if (isAdmin) {
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Admin Record: Secret Code = '${acc.secretCode}'",
                                    fontSize = 11.sp,
                                    color = Color(0xFF6B7280),
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Created: ${acc.approvalStatus.name}",
                                    fontSize = 10.sp,
                                    color = AlumniBlue
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Action Buttons Row
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Free Fire Battle Game Button for student account
                            Button(
                                onClick = onOpenBattleGame,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA580C)),
                                modifier = Modifier.testTag("btn_game_${acc.id}")
                            ) {
                                Icon(Icons.Default.SportsEsports, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isSelf) "🎮 My Battle Game" else "🎮 Squad Up", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }

                            if (!isSelf) {
                                OutlinedButton(
                                    onClick = { onStartPrivateChat(acc.id) },
                                    modifier = Modifier.testTag("btn_chat_with_${acc.id}")
                                ) {
                                    Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Private Chat", fontSize = 12.sp)
                                }
                            }

                            // Admin Actions
                            if (isAdmin && !isSelf) {
                                // Impersonate / Access Account
                                Button(
                                    onClick = {
                                        repository.startImpersonating(acc.id)
                                        actionFeedback = "Entered inspect/access mode for ${acc.name} (${acc.id})."
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = AlumniNavyDark),
                                    modifier = Modifier.testTag("btn_access_acc_${acc.id}")
                                ) {
                                    Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Access Account", fontSize = 11.sp)
                                }

                                // Change ID (Admin can change anyone's ID)
                                OutlinedButton(
                                    onClick = { accountToEditId = acc },
                                    modifier = Modifier.testTag("btn_change_id_${acc.id}")
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Change ID", fontSize = 11.sp)
                                }

                                // Toggle Secure / Unsecure
                                Button(
                                    onClick = {
                                        val newSecured = !acc.isSecured
                                        repository.toggleSecureAccount(acc.id, newSecured)
                                        actionFeedback = if (newSecured)
                                            "Account ${acc.name} is now SECURED 🔒 (Cannot be deleted)"
                                        else
                                            "Account ${acc.name} is now UNSECURED 🔓 (Can be deleted by Admin)"
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (acc.isSecured) Color(0xFFF59E0B) else Color(0xFF10B981)
                                    ),
                                    modifier = Modifier.testTag("btn_toggle_secure_${acc.id}")
                                ) {
                                    Icon(
                                        imageVector = if (acc.isSecured) Icons.Default.LockOpen else Icons.Default.Lock,
                                        contentDescription = null,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (acc.isSecured) "Unsecure Account" else "Secure Account",
                                        fontSize = 11.sp
                                    )
                                }

                                // Make Admin / Demote
                                OutlinedButton(
                                    onClick = {
                                        val makeAdm = acc.role != Role.ADMIN
                                        repository.makeAdmin(acc.id, makeAdm)
                                        actionFeedback = if (makeAdm) "Made ${acc.name} an ADMIN 🛡️." else "Demoted ${acc.name} to Classmate."
                                    },
                                    modifier = Modifier.testTag("btn_make_admin_${acc.id}")
                                ) {
                                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (acc.role == Role.ADMIN) "Demote Member" else "Make Admin",
                                        fontSize = 11.sp
                                    )
                                }

                                // Delete Account
                                Button(
                                    onClick = {
                                        accountToDelete = acc
                                        deleteErrorMessage = null
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = AlumniRed),
                                    modifier = Modifier.testTag("btn_delete_acc_${acc.id}")
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Delete", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Deletion Confirmation & Security Check Dialog
    if (accountToDelete != null) {
        val target = accountToDelete!!
        AlertDialog(
            onDismissRequest = { accountToDelete = null },
            icon = {
                Icon(
                    imageVector = if (target.isSecured) Icons.Default.Warning else Icons.Default.Delete,
                    contentDescription = null,
                    tint = if (target.isSecured) AlumniGoldAccent else AlumniRed,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = if (target.isSecured) "Security Protection Active" else "Confirm Account Deletion",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    if (target.isSecured) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "CANNOT DELETE SECURED ACCOUNT!",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF92400E),
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "This account (${target.name}) was created as a SECURED ID so no one can delete it. Per app rules, an Admin must first tap 'Unsecure Account' to enable deletion.",
                                    fontSize = 12.sp,
                                    color = Color(0xFF78350F)
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "Are you sure you want to delete ${target.name} (${target.id})? This action will permanently remove this classmate from the portal.",
                            fontSize = 13.sp
                        )
                    }

                    if (deleteErrorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = deleteErrorMessage ?: "",
                            color = AlumniRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            confirmButton = {
                if (target.isSecured) {
                    Button(
                        onClick = {
                            // Quick option for admin to unsecure
                            repository.toggleSecureAccount(target.id, false)
                            actionFeedback = "Account ${target.name} has been un-secured. You may now delete it if desired."
                            accountToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AlumniGold)
                    ) {
                        Text("Unsecure Now")
                    }
                } else {
                    Button(
                        onClick = {
                            val res = repository.deleteAccount(target.id)
                            res.onSuccess {
                                actionFeedback = "Account ${target.name} was successfully deleted."
                                accountToDelete = null
                            }.onFailure { err ->
                                deleteErrorMessage = err.message
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AlumniRed),
                        modifier = Modifier.testTag("confirm_delete_button")
                    ) {
                        Text("Confirm Delete")
                    }
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { accountToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Change Student ID Dialog (Admin can change anyone's ID)
    if (accountToEditId != null) {
        val target = accountToEditId!!
        var newIdInput by remember { mutableStateOf(target.id) }
        var editError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { accountToEditId = null },
            icon = {
                Icon(Icons.Default.Edit, contentDescription = null, tint = AlumniBlue, modifier = Modifier.size(28.dp))
            },
            title = {
                Text("Change Classmate ID", fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(
                        text = "Modify Student ID for ${target.name} (Current ID: ${target.id}). All portal records, chat history, and votes will automatically update to the new ID.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = newIdInput,
                        onValueChange = {
                            newIdInput = it
                            editError = null
                        },
                        label = { Text("Enter new Student ID") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_change_student_id")
                    )
                    if (editError != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(editError ?: "", color = AlumniRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val res = repository.updateStudentId(target.id, newIdInput)
                        res.onSuccess { updated ->
                            actionFeedback = "Successfully updated ID to ${updated.id} for ${updated.name}."
                            accountToEditId = null
                        }.onFailure { err ->
                            editError = err.message
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AlumniNavyDark),
                    modifier = Modifier.testTag("btn_save_new_id")
                ) {
                    Text("Save ID")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { accountToEditId = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Remove All Classmates Confirmation Dialog
    if (showRemoveAllDialog) {
        AlertDialog(
            onDismissRequest = { showRemoveAllDialog = false },
            icon = {
                Icon(Icons.Default.Warning, contentDescription = null, tint = AlumniRed, modifier = Modifier.size(32.dp))
            },
            title = {
                Text("Remove All Classmates?", fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    text = "Are you sure you want to remove all classmate accounts from the directory? All student accounts, private chats, and candidate registries will be cleared. Only the Head Admin account will remain.",
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val count = repository.removeAllClassmates()
                        actionFeedback = "Successfully removed all $count classmate account(s)."
                        showRemoveAllDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AlumniRed),
                    modifier = Modifier.testTag("confirm_remove_all_button")
                ) {
                    Text("Confirm Remove All", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showRemoveAllDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

