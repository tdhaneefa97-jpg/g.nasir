package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.Account
import com.example.data.model.ActivityLog
import com.example.data.model.ApprovalStatus
import com.example.data.model.AdminReport
import com.example.data.model.CandidateProfile
import com.example.data.model.ChatMessage
import com.example.data.model.Election
import com.example.data.model.ExamQuestion
import com.example.data.model.Quiz
import com.example.data.model.Role
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class ClassmateRepository private constructor(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences("classmate_hub_prefs", Context.MODE_PRIVATE)

    // Master Admin Credentials
    var adminPasskey: String = "nasir1872"
        private set
    var adminSecurityCode: String = "1872"
        private set
    var adminPhone: String = "9999900000"
        private set

    private val _accounts = MutableStateFlow<List<Account>>(emptyList())
    val accounts: StateFlow<List<Account>> = _accounts.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _activeQuiz = MutableStateFlow<Quiz?>(null)
    val activeQuiz: StateFlow<Quiz?> = _activeQuiz.asStateFlow()

    private val _activeElection = MutableStateFlow<Election?>(null)
    val activeElection: StateFlow<Election?> = _activeElection.asStateFlow()

    private val _adminReports = MutableStateFlow<List<AdminReport>>(emptyList())
    val adminReports: StateFlow<List<AdminReport>> = _adminReports.asStateFlow()

    private val _activityLogs = MutableStateFlow<List<ActivityLog>>(emptyList())
    val activityLogs: StateFlow<List<ActivityLog>> = _activityLogs.asStateFlow()

    private val _currentUser = MutableStateFlow<Account?>(null)
    val currentUser: StateFlow<Account?> = _currentUser.asStateFlow()

    // For Admin inspecting an account
    private val _impersonatedUser = MutableStateFlow<Account?>(null)
    val impersonatedUser: StateFlow<Account?> = _impersonatedUser.asStateFlow()

    private val _isNearVotingDays = MutableStateFlow<Boolean>(false)
    val isNearVotingDays: StateFlow<Boolean> = _isNearVotingDays.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        val accountsVersion = prefs.getInt("accounts_version_v4", 0)
        if (accountsVersion < 4) {
            // User requested "Remove all classmates": clear all mock classmates and retain only Head Admin
            initDefaultAccounts()
            prefs.edit().putInt("accounts_version_v4", 4).apply()
        } else {
            val accountsJson = prefs.getString("accounts_json", null)
            if (accountsJson != null) {
                try {
                    val list = mutableListOf<Account>()
                    val arr = JSONArray(accountsJson)
                    for (i in 0 until arr.length()) {
                        val obj = arr.getJSONObject(i)
                        list.add(
                            Account(
                                id = obj.getString("id"),
                                name = obj.getString("name"),
                                secretCode = obj.getString("secretCode"),
                                phone = obj.getString("phone"),
                                batchInfo = obj.getString("batchInfo"),
                                role = Role.valueOf(obj.optString("role", Role.MEMBER.name)),
                                isPhoneConfirmed = obj.optBoolean("isPhoneConfirmed", false),
                                isSecured = obj.optBoolean("isSecured", false),
                                approvalStatus = ApprovalStatus.valueOf(obj.optString("approvalStatus", ApprovalStatus.APPROVED.name)),
                                createdAt = obj.optLong("createdAt", System.currentTimeMillis()),
                                lastLoginAt = if (obj.has("lastLoginAt") && !obj.isNull("lastLoginAt")) obj.getLong("lastLoginAt") else null,
                                oneDayAdminExpiresAt = if (obj.has("oneDayAdminExpiresAt") && !obj.isNull("oneDayAdminExpiresAt")) obj.getLong("oneDayAdminExpiresAt") else null
                            )
                        )
                    }
                    _accounts.value = list
                } catch (e: Exception) {
                    initDefaultAccounts()
                }
            } else {
                initDefaultAccounts()
            }
        }

        // Load Admin Credentials
        adminPasskey = prefs.getString("admin_passkey", "nasir1872") ?: "nasir1872"
        if (adminPasskey == "ADMINPASS2026") {
            adminPasskey = "nasir1872"
            prefs.edit().putString("admin_passkey", "nasir1872").apply()
        }
        adminSecurityCode = prefs.getString("admin_security_code", "1872") ?: "1872"
        if (adminSecurityCode == "778899") {
            adminSecurityCode = "1872"
            prefs.edit().putString("admin_security_code", "1872").apply()
        }
        adminPhone = prefs.getString("admin_phone", "9999900000") ?: "9999900000"

        // Load Near Voting Days
        _isNearVotingDays.value = prefs.getBoolean("near_voting_days", false)

        // Load Quiz
        initDefaultQuiz()

        // Load Election
        initDefaultElection()

        // Load Chats
        initDefaultChats()

        // Load initial activity logs
        initDefaultLogs()
    }

    private fun initDefaultAccounts() {
        // No dummy classmates - only Head Admin account is preserved
        val initialList = listOf(
            Account(
                id = "ADMIN-001",
                name = "Head Admin",
                secretCode = "admin123",
                phone = "9999900000",
                batchInfo = "VISWAM CBSE 7A Admin",
                role = Role.ADMIN,
                isPhoneConfirmed = true,
                isSecured = true, // Cannot be deleted
                approvalStatus = ApprovalStatus.APPROVED
            )
        )
        _accounts.value = initialList
        saveAccounts()
    }

    private fun saveAccounts() {
        try {
            val arr = JSONArray()
            for (acc in _accounts.value) {
                val obj = JSONObject()
                obj.put("id", acc.id)
                obj.put("name", acc.name)
                obj.put("secretCode", acc.secretCode)
                obj.put("phone", acc.phone)
                obj.put("batchInfo", acc.batchInfo)
                obj.put("role", acc.role.name)
                obj.put("isPhoneConfirmed", acc.isPhoneConfirmed)
                obj.put("isSecured", acc.isSecured)
                obj.put("approvalStatus", acc.approvalStatus.name)
                obj.put("createdAt", acc.createdAt)
                if (acc.lastLoginAt != null) obj.put("lastLoginAt", acc.lastLoginAt)
                if (acc.oneDayAdminExpiresAt != null) obj.put("oneDayAdminExpiresAt", acc.oneDayAdminExpiresAt)
                arr.put(obj)
            }
            prefs.edit().putString("accounts_json", arr.toString()).apply()
        } catch (_: Exception) {}
    }

    private fun initDefaultQuiz() {
        _activeQuiz.value = Quiz(
            id = "quiz-1",
            title = "VISWAM 7A History & Memories Quiz",
            question = "In which year did VISWAM School inaugurate the CBSE secondary campus wing?",
            options = listOf("2016", "2019", "2021", "2023"),
            correctOptionIndex = 1,
            caption = "Win to become Honorary 1-Day Admin with full moderation access!",
            isActive = true,
            submissions = emptyMap()
        )
    }

    private fun initDefaultElection() {
        val defaultCandidates = listOf(
            CandidateProfile(
                studentId = "STU-001",
                studentName = "Aarav Sharma",
                examScore = 98,
                canTakeCareScore = 96,
                aiEvaluation = "AI Certified: Excellent grasp of App Rules, Add Members & Game Skins. High care score.",
                voteCount = 0
            ),
            CandidateProfile(
                studentId = "STU-002",
                studentName = "Diya Patel",
                examScore = 95,
                canTakeCareScore = 93,
                aiEvaluation = "AI Certified: Strong knowledge of Admin duties, Game Skin modding & Anti-abuse handling.",
                voteCount = 0
            ),
            CandidateProfile(
                studentId = "STU-003",
                studentName = "Rohan Verma",
                examScore = 92,
                canTakeCareScore = 90,
                aiEvaluation = "AI Certified: Very good understanding of guild invites and member management.",
                voteCount = 0
            )
        )
        _activeElection.value = Election(
            id = "election-2027",
            title = "Annual VISWAM 7A Admin Election 2027",
            candidateIds = defaultCandidates.map { it.studentId },
            candidateProfiles = defaultCandidates,
            isActive = true,
            isNearVotingDays = _isNearVotingDays.value,
            votingEndsAt = System.currentTimeMillis() + 365L * 86_400_000L,
            votes = emptyMap(),
            year = 2027,
            electionDateText = "November 14, 2027"
        )
    }

    private fun initDefaultChats() {
        val now = System.currentTimeMillis()
        val defaultMessages = listOf(
            ChatMessage(
                id = "m1",
                conversationId = "public",
                senderId = "ADMIN-001",
                senderName = "Head Admin",
                receiverId = null,
                text = "Welcome to VISWAM School CBSE 7A portal! All registered classmates can connect here.",
                timestamp = now
            )
        )
        _chatMessages.value = defaultMessages
    }

    private fun initDefaultLogs() {
        val now = System.currentTimeMillis()
        _activityLogs.value = listOf(
            ActivityLog(UUID.randomUUID().toString(), now - 7200000L, "System Initialized", "Default Classmate Hub profiles and security rules loaded.", "SECURITY"),
            ActivityLog(UUID.randomUUID().toString(), now - 3600000L, "Admin Dual-Key Security", "Admin Passkey and Security Code activated.", "SECURITY"),
            ActivityLog(UUID.randomUUID().toString(), now - 1800000L, "Quiz Published", "Class of 2024 Quiz opened for 1-Day Admin contention.", "QUIZ")
        )
    }

    private fun addLog(title: String, details: String, category: String) {
        val newLog = ActivityLog(UUID.randomUUID().toString(), System.currentTimeMillis(), title, details, category)
        _activityLogs.value = listOf(newLog) + _activityLogs.value
    }

    // Helper for private chat IDs
    fun getPrivateConversationId(userA: String, userB: String): String {
        return if (userA < userB) "private_${userA}_${userB}" else "private_${userB}_${userA}"
    }

    // --- Authentication ---

    fun loginWithIdAndCode(studentId: String, code: String): Result<Account> {
        val trimmedId = studentId.trim()
        val trimmedCode = code.trim()
        val account = _accounts.value.find { it.id.equals(trimmedId, ignoreCase = true) }
            ?: return Result.failure(Exception("ID '$trimmedId' not found in alumni registry."))

        if (account.secretCode != trimmedCode) {
            return Result.failure(Exception("Incorrect Secret Code for ID $trimmedId."))
        }

        if (account.approvalStatus == ApprovalStatus.PENDING_APPROVAL) {
            return Result.failure(Exception("Your account registration is pending Admin Approval. Please wait for the admin to grant access."))
        }

        if (account.approvalStatus == ApprovalStatus.REJECTED) {
            return Result.failure(Exception("Your account registration was not approved by the admin."))
        }

        // Update login timestamp
        val updated = account.copy(lastLoginAt = System.currentTimeMillis())
        _accounts.value = _accounts.value.map { if (it.id == updated.id) updated else it }
        saveAccounts()

        _currentUser.value = updated
        _impersonatedUser.value = null
        addLog("Classmate Login", "${updated.name} (${updated.id}) logged into the app.", "LOGIN")

        return Result.success(updated)
    }

    fun confirmPhoneNumber(studentId: String, phoneInput: String): Result<Account> {
        val trimmedPhone = phoneInput.trim().replace(" ", "").replace("-", "")
        val account = _accounts.value.find { it.id.equals(studentId, ignoreCase = true) }
            ?: return Result.failure(Exception("Account not found."))

        val storedCleanPhone = account.phone.trim().replace(" ", "").replace("-", "")
        if (trimmedPhone != storedCleanPhone && !storedCleanPhone.endsWith(trimmedPhone) && !trimmedPhone.endsWith(storedCleanPhone)) {
            return Result.failure(Exception("The entered phone number does not match the official record ($storedCleanPhone) on file."))
        }

        val updated = account.copy(isPhoneConfirmed = true)
        _accounts.value = _accounts.value.map { if (it.id == updated.id) updated else it }
        saveAccounts()

        if (_currentUser.value?.id == updated.id) {
            _currentUser.value = updated
        }
        addLog("Phone Confirmed", "${updated.name} (${updated.id}) confirmed phone $trimmedPhone.", "PHONE")
        return Result.success(updated)
    }

    fun adminLogin(passkey: String, code: String): Result<Account> {
        val trimmedPasskey = passkey.trim()
        val trimmedCode = code.trim()

        if (trimmedPasskey != adminPasskey) {
            return Result.failure(Exception("Invalid Admin Passkey. Please verify your master passkey."))
        }

        if (trimmedCode != adminSecurityCode) {
            return Result.failure(Exception("Invalid Admin Security Code."))
        }

        // Fetch or create head admin account
        var adminAcc = _accounts.value.find { it.id == "ADMIN-001" }
        if (adminAcc == null) {
            adminAcc = Account(
                id = "ADMIN-001",
                name = "Head Admin (Batch Rep)",
                secretCode = "admin123",
                phone = "9999900000",
                batchInfo = "Alumni Council Admin",
                role = Role.ADMIN,
                isPhoneConfirmed = true,
                isSecured = true,
                approvalStatus = ApprovalStatus.APPROVED
            )
            _accounts.value = _accounts.value + adminAcc
            saveAccounts()
        }

        val updated = adminAcc.copy(lastLoginAt = System.currentTimeMillis())
        _accounts.value = _accounts.value.map { if (it.id == updated.id) updated else it }
        saveAccounts()

        _currentUser.value = updated
        _impersonatedUser.value = null
        addLog("Admin Portal Access", "Master Dual-Key Login successful for ${updated.name}.", "LOGIN")
        return Result.success(updated)
    }

    // --- Account Management & Security ---

    fun requestRegistration(id: String, name: String, code: String, phone: String, batch: String): Result<Account> {
        if (_isNearVotingDays.value) {
            return Result.failure(Exception("Notice: Registration is currently frozen! App Policy stipulates that no new IDs can be created during near-voting days."))
        }

        val trimmedId = id.trim().uppercase()
        if (trimmedId.isEmpty() || name.isBlank() || code.isBlank() || phone.isBlank()) {
            return Result.failure(Exception("All fields (ID, Name, Secret Code, Phone) are required."))
        }

        if (_accounts.value.any { it.id.equals(trimmedId, ignoreCase = true) }) {
            return Result.failure(Exception("Student ID '$trimmedId' already exists."))
        }

        val newAccount = Account(
            id = trimmedId,
            name = name.trim(),
            secretCode = code.trim(),
            phone = phone.trim(),
            batchInfo = if (batch.isBlank()) "Class of 2024" else batch.trim(),
            role = Role.MEMBER,
            isPhoneConfirmed = false,
            isSecured = false,
            approvalStatus = ApprovalStatus.PENDING_APPROVAL // Requires Admin Approval
        )

        _accounts.value = _accounts.value + newAccount
        saveAccounts()
        addLog("Registration Requested", "${newAccount.name} requested ID ${newAccount.id}. Pending Admin Approval.", "ACCOUNT")
        return Result.success(newAccount)
    }

    fun approveAccount(studentId: String) {
        _accounts.value = _accounts.value.map {
            if (it.id == studentId) it.copy(approvalStatus = ApprovalStatus.APPROVED) else it
        }
        saveAccounts()
        addLog("Account Approved", "Admin approved registration for $studentId.", "ACCOUNT")
    }

    fun rejectAccount(studentId: String) {
        _accounts.value = _accounts.value.map {
            if (it.id == studentId) it.copy(approvalStatus = ApprovalStatus.REJECTED) else it
        }
        saveAccounts()
        addLog("Account Rejected", "Admin rejected registration for $studentId.", "ACCOUNT")
    }

    fun addAccountDirectly(
        id: String,
        name: String,
        code: String,
        phone: String,
        batch: String,
        role: Role,
        isSecured: Boolean
    ): Result<Account> {
        val trimmedId = id.trim().uppercase()
        if (trimmedId.isEmpty() || name.isBlank() || code.isBlank() || phone.isBlank()) {
            return Result.failure(Exception("Please fill out all account fields."))
        }

        if (_accounts.value.any { it.id.equals(trimmedId, ignoreCase = true) }) {
            return Result.failure(Exception("Student ID '$trimmedId' already exists."))
        }

        val newAcc = Account(
            id = trimmedId,
            name = name.trim(),
            secretCode = code.trim(),
            phone = phone.trim(),
            batchInfo = if (batch.isBlank()) "Class of 2024" else batch.trim(),
            role = role,
            isPhoneConfirmed = true,
            isSecured = isSecured,
            approvalStatus = ApprovalStatus.APPROVED
        )
        _accounts.value = _accounts.value + newAcc
        saveAccounts()
        addLog("Account Added Directly", "Admin created account for ${newAcc.name} (${newAcc.id}) [Secured: $isSecured].", "ACCOUNT")
        return Result.success(newAcc)
    }

    fun deleteAccount(studentId: String): Result<Unit> {
        val target = _accounts.value.find { it.id == studentId }
            ?: return Result.failure(Exception("Account not found."))

        if (target.isSecured) {
            return Result.failure(Exception("SECURITY PROTECTION: This account (${target.name}) is Secured! App policy protects secured accounts from deletion. An Admin must first Unsecure this account."))
        }

        _accounts.value = _accounts.value.filterNot { it.id == studentId }
        saveAccounts()
        addLog("Account Deleted", "Admin removed unsecured account for ${target.name} (${target.id}).", "ACCOUNT")
        return Result.success(Unit)
    }

    fun removeAllClassmates(): Int {
        val nonAdminAccounts = _accounts.value.filter { it.role != Role.ADMIN }
        val count = nonAdminAccounts.size
        _accounts.value = _accounts.value.filter { it.role == Role.ADMIN }
        saveAccounts()

        // Clean up messages from deleted accounts
        val validIds = _accounts.value.map { it.id }.toSet()
        _chatMessages.value = _chatMessages.value.filter { msg ->
            msg.conversationId == "public" && (msg.senderId in validIds || msg.senderId == "ADMIN-001")
        }

        // Clean up election and quiz
        val election = _activeElection.value
        if (election != null) {
            _activeElection.value = election.copy(candidateIds = emptyList(), votes = emptyMap())
        }
        val quiz = _activeQuiz.value
        if (quiz != null) {
            _activeQuiz.value = quiz.copy(submissions = emptyMap(), winnerId = null)
        }

        addLog("All Classmates Removed", "Admin cleared all classmate accounts ($count accounts removed).", "ACCOUNT")
        return count
    }

    fun toggleSecureAccount(studentId: String, makeSecured: Boolean) {
        _accounts.value = _accounts.value.map {
            if (it.id == studentId) it.copy(isSecured = makeSecured) else it
        }
        saveAccounts()
        val statusText = if (makeSecured) "SECURED (Deletion Protected)" else "UNSECURED (Can be deleted)"
        addLog("Security Status Changed", "Account $studentId marked as $statusText.", "SECURITY")
    }

    fun makeAdmin(studentId: String, makeAdmin: Boolean) {
        _accounts.value = _accounts.value.map {
            if (it.id == studentId) {
                it.copy(role = if (makeAdmin) Role.ADMIN else Role.MEMBER)
            } else it
        }
        saveAccounts()
        val roleText = if (makeAdmin) "Promoted to ADMIN" else "Demoted to MEMBER"
        addLog("Admin Privilege Changed", "Classmate $studentId $roleText by Admin.", "ACCOUNT")
    }

    fun updateStudentId(oldId: String, newIdRaw: String): Result<Account> {
        val newId = newIdRaw.trim().uppercase()
        if (newId.isEmpty()) {
            return Result.failure(Exception("Student ID cannot be empty."))
        }
        val target = _accounts.value.find { it.id.equals(oldId, ignoreCase = true) }
            ?: return Result.failure(Exception("Account with ID '$oldId' not found."))

        if (oldId.equals(newId, ignoreCase = true)) {
            return Result.success(target)
        }

        if (_accounts.value.any { it.id.equals(newId, ignoreCase = true) }) {
            return Result.failure(Exception("Cannot change ID: Student ID '$newId' is already in use by another classmate."))
        }

        // Update the account
        val updated = target.copy(id = newId)
        _accounts.value = _accounts.value.map { if (it.id.equals(oldId, ignoreCase = true)) updated else it }
        saveAccounts()

        // Update active user state if affected
        if (_currentUser.value?.id.equals(oldId, ignoreCase = true)) {
            _currentUser.value = updated
        }
        if (_impersonatedUser.value?.id.equals(oldId, ignoreCase = true)) {
            _impersonatedUser.value = updated
        }

        // Update Chat Messages references
        _chatMessages.value = _chatMessages.value.map { msg ->
            val newSenderId = if (msg.senderId.equals(oldId, ignoreCase = true)) newId else msg.senderId
            val newReceiverId = if (msg.receiverId != null && msg.receiverId.equals(oldId, ignoreCase = true)) newId else msg.receiverId
            val newConvoId = if (msg.conversationId.startsWith("private_")) {
                if (newReceiverId != null) getPrivateConversationId(newSenderId, newReceiverId) else msg.conversationId
            } else {
                msg.conversationId
            }
            msg.copy(senderId = newSenderId, receiverId = newReceiverId, conversationId = newConvoId)
        }

        // Update Active Quiz references
        val quiz = _activeQuiz.value
        if (quiz != null) {
            val updatedSubmissions = mutableMapOf<String, Int>()
            quiz.submissions.forEach { (subId, opt) ->
                val key = if (subId.equals(oldId, ignoreCase = true)) newId else subId
                updatedSubmissions[key] = opt
            }
            val newWinnerId = if (quiz.winnerId?.equals(oldId, ignoreCase = true) == true) newId else quiz.winnerId
            _activeQuiz.value = quiz.copy(submissions = updatedSubmissions, winnerId = newWinnerId)
        }

        // Update Active Election references
        val election = _activeElection.value
        if (election != null) {
            val newCandidates = election.candidateIds.map { if (it.equals(oldId, ignoreCase = true)) newId else it }
            val newVotes = mutableMapOf<String, String>()
            election.votes.forEach { (voter, cand) ->
                val voterKey = if (voter.equals(oldId, ignoreCase = true)) newId else voter
                val candVal = if (cand.equals(oldId, ignoreCase = true)) newId else cand
                newVotes[voterKey] = candVal
            }
            val newElectionWinner = if (election.winnerStudentId?.equals(oldId, ignoreCase = true) == true) newId else election.winnerStudentId
            _activeElection.value = election.copy(candidateIds = newCandidates, votes = newVotes, winnerStudentId = newElectionWinner)
        }

        addLog("Student ID Modified", "Admin changed ID for ${target.name} from $oldId to $newId.", "ACCOUNT")
        return Result.success(updated)
    }

    fun updateAdminCredentials(newPasskey: String, newCode: String): Result<Unit> {
        val trimmedPass = newPasskey.trim()
        val trimmedCode = newCode.trim()
        if (trimmedPass.isEmpty() || trimmedCode.isEmpty()) {
            return Result.failure(Exception("Passkey and Code cannot be blank."))
        }
        adminPasskey = trimmedPass
        adminSecurityCode = trimmedCode
        prefs.edit()
            .putString("admin_passkey", adminPasskey)
            .putString("admin_security_code", adminSecurityCode)
            .apply()
        addLog("Admin Credentials Changed", "Admin Master Passkey and Security Code updated successfully.", "SECURITY")
        return Result.success(Unit)
    }

    fun updateAdminPhone(phone: String): Result<Unit> {
        val clean = phone.trim()
        if (clean.isEmpty()) {
            return Result.failure(Exception("Phone number cannot be blank."))
        }
        adminPhone = clean
        prefs.edit().putString("admin_phone", clean).apply()
        _accounts.value = _accounts.value.map {
            if (it.role == Role.ADMIN) it.copy(phone = clean) else it
        }
        saveAccounts()
        addLog("Admin Phone Updated", "Admin SMS notification phone updated to $clean.", "SECURITY")
        return Result.success(Unit)
    }

    fun handleGoogleLoginAttempt(user: com.google.firebase.auth.FirebaseUser): Result<Account> {
        val userEmail = user.email?.trim() ?: ""
        val displayName = user.displayName?.trim() ?: "Student"
        val derivedId = if (userEmail.contains("@")) userEmail.substringBefore("@").uppercase() else user.uid.take(8).uppercase()

        val existing = _accounts.value.find {
            it.id.equals(derivedId, ignoreCase = true) ||
                    (userEmail.isNotBlank() && it.phone.equals(userEmail, ignoreCase = true))
        }

        if (existing != null) {
            return when (existing.approvalStatus) {
                ApprovalStatus.APPROVED -> {
                    val updated = existing.copy(lastLoginAt = System.currentTimeMillis())
                    _accounts.value = _accounts.value.map { if (it.id == updated.id) updated else it }
                    saveAccounts()
                    _currentUser.value = updated
                    addLog("Google Login", "${updated.name} signed in with Google.", "LOGIN")
                    Result.success(updated)
                }
                ApprovalStatus.PENDING_APPROVAL -> {
                    Result.failure(Exception("PENDING_ADMIN_APPROVAL:Your Google sign-in request for ${existing.name} ($derivedId) is pending Admin SMS Approval. Please dispatch the approval SMS to Admin ($adminPhone)."))
                }
                ApprovalStatus.REJECTED -> {
                    Result.failure(Exception("Registration rejected by Admin."))
                }
            }
        }

        // New student requesting access via Google
        val newPending = Account(
            id = derivedId,
            name = displayName,
            secretCode = "GGL-${user.uid.take(4).uppercase()}",
            phone = if (userEmail.isNotEmpty()) userEmail else "Google Account",
            batchInfo = "VISWAM CBSE 7A",
            role = Role.MEMBER,
            isPhoneConfirmed = true,
            isSecured = false,
            approvalStatus = ApprovalStatus.PENDING_APPROVAL
        )
        _accounts.value = _accounts.value + newPending
        saveAccounts()
        addLog("Google Sign-In Request", "New student $displayName ($userEmail) requested login. SMS Approval required.", "ACCOUNT")

        return Result.failure(Exception("PENDING_ADMIN_APPROVAL:New student Google sign-in detected ($displayName). Admin approval required. An approval request SMS is ready to send to Admin ($adminPhone)."))
    }

    // --- Admin Impersonation / "Access Anyone's Account" ---

    fun startImpersonating(studentId: String): Result<Account> {
        val target = _accounts.value.find { it.id == studentId }
            ?: return Result.failure(Exception("Account not found."))
        _impersonatedUser.value = target
        addLog("Admin Impersonation", "Admin entered inspect view of ${target.name} (${target.id}).", "ACCOUNT")
        return Result.success(target)
    }

    fun stopImpersonating() {
        _impersonatedUser.value = null
    }

    // --- Chats ---

    fun sendMessage(conversationId: String, text: String, receiverId: String? = null): Result<ChatMessage> {
        val sender = _impersonatedUser.value ?: _currentUser.value
            ?: return Result.failure(Exception("Not logged in."))

        val trimmedText = text.trim()
        if (trimmedText.isEmpty()) {
            return Result.failure(Exception("Message cannot be empty."))
        }

        val msg = ChatMessage(
            id = UUID.randomUUID().toString(),
            conversationId = conversationId,
            senderId = sender.id,
            senderName = sender.name,
            receiverId = receiverId,
            text = trimmedText,
            timestamp = System.currentTimeMillis()
        )
        _chatMessages.value = _chatMessages.value + msg
        return Result.success(msg)
    }

    // --- Quiz & 1-Day Admin ---

    fun createQuiz(
        title: String,
        question: String,
        options: List<String>,
        correctOptionIndex: Int,
        caption: String
    ): Result<Quiz> {
        if (title.isBlank() || question.isBlank() || options.size < 2) {
            return Result.failure(Exception("Please provide a valid question and at least 2 options."))
        }

        val newQuiz = Quiz(
            id = UUID.randomUUID().toString(),
            title = title.trim(),
            question = question.trim(),
            options = options.map { it.trim() },
            correctOptionIndex = correctOptionIndex,
            caption = if (caption.isBlank()) "Winner receives 1-Day Admin Status!" else caption.trim(),
            isActive = true
        )
        _activeQuiz.value = newQuiz
        addLog("Quiz Created", "Admin started new quiz: '${newQuiz.title}'.", "QUIZ")
        return Result.success(newQuiz)
    }

    fun submitQuizAnswer(quizId: String, studentId: String, optionIndex: Int): Result<Unit> {
        val currentQuiz = _activeQuiz.value
            ?: return Result.failure(Exception("No active quiz."))
        if (currentQuiz.id != quizId || !currentQuiz.isActive) {
            return Result.failure(Exception("Quiz is closed or not available."))
        }

        val updatedSubmissions = currentQuiz.submissions.toMutableMap()
        updatedSubmissions[studentId] = optionIndex
        _activeQuiz.value = currentQuiz.copy(submissions = updatedSubmissions)

        val sender = _accounts.value.find { it.id == studentId }
        addLog("Quiz Response", "${sender?.name ?: studentId} submitted an answer to the quiz.", "QUIZ")
        return Result.success(Unit)
    }

    fun declareQuizWinner(quizId: String, winnerStudentId: String, winnerCaption: String): Result<Account> {
        val currentQuiz = _activeQuiz.value
            ?: return Result.failure(Exception("No active quiz."))

        val winnerAcc = _accounts.value.find { it.id == winnerStudentId }
            ?: return Result.failure(Exception("Winner account not found."))

        // 1-Day Admin expires in 24 hours (86,400,000 ms)
        val expiryTime = System.currentTimeMillis() + 86_400_000L
        val updatedWinner = winnerAcc.copy(
            role = Role.ONE_DAY_ADMIN,
            oneDayAdminExpiresAt = expiryTime
        )

        _accounts.value = _accounts.value.map { if (it.id == winnerStudentId) updatedWinner else it }
        saveAccounts()

        _activeQuiz.value = currentQuiz.copy(
            isActive = false,
            winnerId = winnerStudentId,
            winnerName = winnerAcc.name,
            winnerCaption = winnerCaption
        )

        addLog(
            "1-Day Admin Crowned 👑",
            "${winnerAcc.name} ($winnerStudentId) won the quiz and is crowned 1-Day Admin! Caption: $winnerCaption",
            "QUIZ"
        )
        return Result.success(updatedWinner)
    }

    // --- Admin Elections & Voting ---

    fun createElection(title: String, candidateIds: List<String>, isNearVotingDays: Boolean): Result<Election> {
        if (candidateIds.size > 3) {
            return Result.failure(Exception("ELECTION POLICY VIOLATION: Maximum 3 candidates allowed to stand for Admin election!"))
        }
        if (candidateIds.isEmpty()) {
            return Result.failure(Exception("Select at least 1 candidate."))
        }

        _isNearVotingDays.value = isNearVotingDays
        prefs.edit().putBoolean("near_voting_days", isNearVotingDays).apply()

        val profiles = candidateIds.map { cId ->
            val acc = _accounts.value.find { it.id == cId }
            CandidateProfile(
                studentId = cId,
                studentName = acc?.name ?: cId,
                examScore = 90 + (cId.hashCode() % 10).coerceAtLeast(0),
                canTakeCareScore = 88 + (cId.hashCode() % 10).coerceAtLeast(0),
                aiEvaluation = "AI Certified: Top 3 Exam performer. Qualified for 1-Year Temporary Admin."
            )
        }

        val election = Election(
            id = UUID.randomUUID().toString(),
            title = if (title.isBlank()) "Annual Admin Election 2027" else title.trim(),
            candidateIds = candidateIds,
            candidateProfiles = profiles,
            isActive = true,
            isNearVotingDays = isNearVotingDays,
            votingEndsAt = System.currentTimeMillis() + 365L * 86_400_000L,
            votes = emptyMap(),
            year = 2027,
            electionDateText = "November 14, 2027"
        )
        _activeElection.value = election
        addLog("Election Launched", "New Admin election 2027 with ${candidateIds.size} candidates. Near Voting Days: $isNearVotingDays.", "ELECTION")
        return Result.success(election)
    }

    fun toggleNearVotingDays(enabled: Boolean) {
        _isNearVotingDays.value = enabled
        prefs.edit().putBoolean("near_voting_days", enabled).apply()
        _activeElection.value = _activeElection.value?.copy(isNearVotingDays = enabled)
        val logMsg = if (enabled) "Near Voting Days ACTIVATED: New ID registration is now frozen!" else "Near Voting Days DEACTIVATED: New ID registration reopened."
        addLog("Voting Policy", logMsg, "ELECTION")
    }

    fun castVote(voterStudentId: String, candidateStudentId: String): Result<Unit> {
        val election = _activeElection.value
            ?: return Result.failure(Exception("No active election."))

        if (!election.isActive) {
            return Result.failure(Exception("Voting has concluded."))
        }

        if (election.votes.containsKey(voterStudentId)) {
            return Result.failure(Exception("VOTING POLICY: One student can vote only ONE time! Your vote is already recorded."))
        }

        if (!election.candidateIds.contains(candidateStudentId)) {
            return Result.failure(Exception("Invalid candidate selected."))
        }

        val updatedVotes = election.votes.toMutableMap()
        updatedVotes[voterStudentId] = candidateStudentId

        val updatedProfiles = election.candidateProfiles.map { prof ->
            val count = updatedVotes.values.count { it == prof.studentId }
            prof.copy(voteCount = count)
        }

        _activeElection.value = election.copy(votes = updatedVotes, candidateProfiles = updatedProfiles)
        addLog("Vote Cast", "Member $voterStudentId voted in Admin Election 2027.", "ELECTION")
        return Result.success(Unit)
    }

    fun finalizeElection(): Result<Account> {
        val election = _activeElection.value
            ?: return Result.failure(Exception("No active election."))

        val tally = mutableMapOf<String, Int>()
        for (cand in election.candidateIds) {
            tally[cand] = 0
        }
        for ((_, cand) in election.votes) {
            tally[cand] = (tally[cand] ?: 0) + 1
        }

        val winnerId = tally.maxByOrNull { it.value }?.key
            ?: election.candidateIds.firstOrNull()
            ?: return Result.failure(Exception("No candidates in election."))

        var winnerAcc = _accounts.value.find { it.id == winnerId }
            ?: return Result.failure(Exception("Winner account not found."))

        // Head Admin (ADMIN-001) is the permanent owner and cannot be removed.
        // The winner receives TEMPORARY ADMIN for 1 YEAR (expires after 365 days).
        val tempCode = "TEMP-ADMIN-" + UUID.randomUUID().toString().take(4).uppercase()
        val oneYearExpiry = System.currentTimeMillis() + 365L * 86_400_000L

        winnerAcc = winnerAcc.copy(
            role = Role.TEMPORARY_ADMIN,
            tempAdminPasskey = tempCode,
            tempAdminExpiresAt = oneYearExpiry
        )
        _accounts.value = _accounts.value.map { if (it.id == winnerId) winnerAcc else it }
        saveAccounts()

        _activeElection.value = election.copy(
            isActive = false,
            isFinalized = true,
            winnerStudentId = winnerId,
            winnerTempAdminCode = tempCode
        )

        addLog(
            "Election Winner Proclaimed 🎉",
            "${winnerAcc.name} ($winnerId) won the 2027 Election with ${tally[winnerId] ?: 0} votes! Temporary Admin passkey $tempCode issued (Expires in 1 Year).",
            "ELECTION"
        )
        return Result.success(winnerAcc)
    }

    fun getAiExamQuestions(): List<ExamQuestion> {
        return listOf(
            ExamQuestion(
                id = 1,
                question = "What are the ONLY two powers permitted for a Temporary Admin?",
                options = listOf(
                    "Change App Name and Ban Students",
                    "Add New Members & Modify Game Skins Only",
                    "Delete Head Admin and Edit Code",
                    "Modify App Colors and Shut Down Server"
                ),
                correctOptionIndex = 1,
                category = "Admin Duties",
                aiExplanation = "Classmate AI: Review Rule #4. Temporary Admin can ONLY Add New Members and Modify Game Skins. All app modification is reserved exclusively for Head Admin."
            ),
            ExamQuestion(
                id = 2,
                question = "Can a Temporary Admin delete or change the password of the Head Admin?",
                options = listOf(
                    "Yes, anytime they choose",
                    "Only during weekends",
                    "NO, Head Admin is permanent and cannot be modified or removed",
                    "Yes, if students vote for it"
                ),
                correctOptionIndex = 2,
                category = "App Rules",
                aiExplanation = "Classmate AI: Rule #94 explicitly protects the Head Admin. Temporary Admin has zero authority over the Head Admin."
            ),
            ExamQuestion(
                id = 3,
                question = "A student asks in chat to hack another student's account or steal diamonds. What must be done?",
                options = listOf(
                    "Accept and help them hack",
                    "Block the request as ILLEGAL, report/notify Head Admin, and advise the student",
                    "Charge 500 Coins to do it",
                    "Ignore it completely"
                ),
                correctOptionIndex = 1,
                category = "Illegal Requests",
                aiExplanation = "Classmate AI: Rule #81 covers Legal vs Illegal checks. Any breach, hack, or abuse is strictly illegal and triggers Head Admin escalation."
            ),
            ExamQuestion(
                id = 4,
                question = "Which game skins can a Temporary Admin modify, customize, or add new variants for?",
                options = listOf(
                    "Only Gun bullets",
                    "Bundle Skins, Gun Skins, Character Dress, Backpacks, Cars & Skateboards",
                    "App source code and logo icons",
                    "None, skins are locked"
                ),
                correctOptionIndex = 1,
                category = "Game Skins",
                aiExplanation = "Classmate AI: Rule #3 outlines all permitted game customization skins that a Temporary Admin can enhance for student enjoyment."
            ),
            ExamQuestion(
                id = 5,
                question = "How long does the Temporary Admin term last before the next Admin Election?",
                options = listOf(
                    "1 Week only",
                    "Exactly 1 Year (Expires automatically after 1 year)",
                    "Lifetime permanent",
                    "1 Month"
                ),
                correctOptionIndex = 1,
                category = "Admin Duties",
                aiExplanation = "Classmate AI: Elections occur annually. The temporary code has an expiration timestamp of 365 days."
            )
        )
    }

    fun askAiDoubt(question: ExamQuestion, userDoubt: String): String {
        return "Classmate AI: ${question.aiExplanation}\n(Notice: AI can explain the topic and concept, but cannot give the direct option answer to maintain exam integrity.)"
    }

    fun submitAiExam(student: Account, answers: Map<Int, Int>): Pair<Int, Int> {
        val questions = getAiExamQuestions()
        var correct = 0
        for (q in questions) {
            if (answers[q.id] == q.correctOptionIndex) {
                correct++
            }
        }
        val examScore = ((correct.toFloat() / questions.size.toFloat()) * 100).toInt()
        val careScore = if (examScore >= 80) 90 + ((examScore - 80) / 2) else (examScore * 0.8).toInt()

        val aiEval = if (careScore >= 85)
            "AI Decision: He CAN take care of the app! Outstanding leadership and policy grasp."
        else
            "AI Decision: Care score $careScore/100 is below threshold. He cannot take care of the app yet."

        val election = _activeElection.value
        if (election != null && careScore >= 85) {
            val existingProfiles = election.candidateProfiles.toMutableList()
            val existingIndex = existingProfiles.indexOfFirst { it.studentId == student.id }
            val newProfile = CandidateProfile(
                studentId = student.id,
                studentName = student.name,
                examScore = examScore,
                canTakeCareScore = careScore,
                aiEvaluation = aiEval,
                voteCount = election.votes.values.count { it == student.id }
            )

            if (existingIndex >= 0) {
                existingProfiles[existingIndex] = newProfile
            } else {
                existingProfiles.add(newProfile)
            }

            val top3 = existingProfiles.sortedByDescending { it.examScore }.take(3)
            val updatedCandidateIds = top3.map { it.studentId }

            _activeElection.value = election.copy(
                candidateIds = updatedCandidateIds,
                candidateProfiles = top3
            )
        }

        addLog("AI Exam Completed", "${student.name} (${student.id}) completed AI Exam: Score $examScore/100, Care Score $careScore/100.", "ELECTION")
        return Pair(examScore, careScore)
    }

    fun submitAdminReport(
        reporter: Account,
        reportedAdminId: String,
        category: String,
        reason: String
    ): Result<AdminReport> {
        if (reason.isBlank()) {
            return Result.failure(Exception("Please provide a detailed reason for the report."))
        }

        val reportedAdmin = _accounts.value.find { it.id == reportedAdminId }
        val adminName = reportedAdmin?.name ?: reportedAdminId

        var aiAction: String? = null

        val shouldAiRemove = category == "Trying To Modify App" || category == "Abuse" || category == "Stealing Diamonds"
        if (shouldAiRemove && reportedAdmin?.role == Role.TEMPORARY_ADMIN) {
            val demoted = reportedAdmin.copy(role = Role.MEMBER, tempAdminPasskey = null)
            _accounts.value = _accounts.value.map { if (it.id == reportedAdminId) demoted else it }
            saveAccounts()

            val election = _activeElection.value
            if (election != null) {
                _activeElection.value = election.copy(
                    isAiHandlingDueToReport = true,
                    aiHandlingEndsAt = System.currentTimeMillis() + 7L * 86_400_000L,
                    winnerStudentId = null,
                    winnerTempAdminCode = null,
                    isActive = true
                )
            }

            aiAction = "AI Enforcement: Temporary Admin removed for policy violation ($category). Classmate AI will take care of the app for 1 WEEK before holding a new election."
            addLog("AI Admin Removal", "Temporary Admin $adminName was removed by AI due to verified violation: $reason. AI is handling the app for 7 days.", "REPORT")
        } else {
            addLog("Report Filed", "Student ${reporter.name} reported Admin $adminName for '$category': $reason. Forwarded to Head Admin.", "REPORT")
        }

        val report = AdminReport(
            id = UUID.randomUUID().toString(),
            reportedAdminId = reportedAdminId,
            reportedAdminName = adminName,
            reporterStudentId = reporter.id,
            reporterStudentName = reporter.name,
            category = category,
            reason = reason.trim(),
            status = if (shouldAiRemove) "RESOLVED_BY_AI" else "PENDING_HEAD_ADMIN_REVIEW",
            aiActionTaken = aiAction
        )

        _adminReports.value = listOf(report) + _adminReports.value
        return Result.success(report)
    }

    fun logout() {
        _currentUser.value = null
        _impersonatedUser.value = null
    }

    companion object {
        @Volatile
        private var instance: ClassmateRepository? = null

        fun getInstance(context: Context): ClassmateRepository {
            return instance ?: synchronized(this) {
                instance ?: ClassmateRepository(context).also { instance = it }
            }
        }
    }
}
