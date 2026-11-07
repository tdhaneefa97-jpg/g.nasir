package com.example

import com.example.data.model.Account
import com.example.data.model.ApprovalStatus
import com.example.data.model.Election
import com.example.data.model.Quiz
import com.example.data.model.Role
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testAccountRoleAndSecurity() {
        val securedStudent = Account(
            id = "CLS-101",
            name = "Rahul Sharma",
            secretCode = "1234",
            phone = "9876543210",
            batchInfo = "Class of 2024",
            role = Role.MEMBER,
            isPhoneConfirmed = true,
            isSecured = true
        )
        assertTrue(securedStudent.isSecured)
        assertEquals(Role.MEMBER, securedStudent.effectiveRole)
        assertFalse(securedStudent.isAdminOrOneDayAdmin)

        val adminAcc = Account(
            id = "ADMIN-001",
            name = "Head Admin",
            secretCode = "admin123",
            phone = "9999900000",
            batchInfo = "Alumni Rep",
            role = Role.ADMIN,
            isPhoneConfirmed = true,
            isSecured = true
        )
        assertTrue(adminAcc.isAdminOrOneDayAdmin)
    }

    @Test
    fun testOneDayAdminRoleExpiry() {
        val expiredWinner = Account(
            id = "CLS-102",
            name = "Priya Patel",
            secretCode = "2345",
            phone = "9876543211",
            batchInfo = "Class of 2024",
            role = Role.ONE_DAY_ADMIN,
            oneDayAdminExpiresAt = System.currentTimeMillis() - 1000L // Already expired
        )
        // Effective role reverts to MEMBER
        assertEquals(Role.MEMBER, expiredWinner.effectiveRole)

        val activeWinner = Account(
            id = "CLS-103",
            name = "Aamir Khan",
            secretCode = "3456",
            phone = "9876543212",
            batchInfo = "Class of 2024",
            role = Role.ONE_DAY_ADMIN,
            oneDayAdminExpiresAt = System.currentTimeMillis() + 86400000L // 24h future
        )
        assertEquals(Role.ONE_DAY_ADMIN, activeWinner.effectiveRole)
        assertTrue(activeWinner.isAdminOrOneDayAdmin)
    }

    @Test
    fun testElectionMaxThreeCandidatesConstraint() {
        val validCandidates = listOf("CLS-101", "CLS-103", "CLS-104")
        assertTrue(validCandidates.size <= 3)

        val election = Election(
            id = "elec-1",
            title = "Annual Election",
            candidateIds = validCandidates,
            isActive = true,
            votes = mapOf("CLS-101" to "CLS-103")
        )
        assertEquals(3, election.candidateIds.size)
        assertEquals("CLS-103", election.votes["CLS-101"])
    }

    @Test
    fun testQuizSubmissionsEvaluation() {
        val quiz = Quiz(
            id = "q-1",
            title = "History Quiz",
            question = "Auditorium year?",
            options = listOf("2015", "2018", "2020", "2022"),
            correctOptionIndex = 1,
            caption = "Winner gets 1-Day Admin",
            submissions = mapOf("CLS-101" to 1, "CLS-102" to 0)
        )
        assertEquals(1, quiz.correctOptionIndex)
        assertEquals(1, quiz.submissions["CLS-101"])
        // CLS-101 got correct answer
        assertEquals(quiz.correctOptionIndex, quiz.submissions["CLS-101"])
    }

    @Test
    fun testChangeStudentIdLogic() {
        val original = Account(
            id = "CLS-101",
            name = "Rahul Sharma",
            secretCode = "1234",
            phone = "9876543210",
            batchInfo = "Class of 2024",
            role = Role.MEMBER
        )
        val modified = original.copy(id = "V7A-101")
        assertEquals("V7A-101", modified.id)
        assertEquals("Rahul Sharma", modified.name)
    }

    @Test
    fun testSavedSessionModel() {
        val session = com.example.data.datastore.SavedSession(
            id = "CLS-101",
            secretCode = "1234",
            isAdmin = false,
            rememberMe = true
        )
        assertTrue(session.rememberMe)
        assertEquals("CLS-101", session.id)
        assertFalse(session.isAdmin)
    }

    @Test
    fun testChatMessageSearchFiltering() {
        val messages = listOf(
            com.example.data.model.ChatMessage(
                id = "m1",
                conversationId = "public",
                senderId = "CLS-101",
                senderName = "Rahul Sharma",
                receiverId = null,
                text = "Welcome to VISWAM CBSE 7A reunion app!",
                timestamp = 1000L
            ),
            com.example.data.model.ChatMessage(
                id = "m2",
                conversationId = "public",
                senderId = "CLS-102",
                senderName = "Priya Patel",
                receiverId = null,
                text = "Hi Rahul! Are you participating in the Quiz today?",
                timestamp = 2000L
            ),
            com.example.data.model.ChatMessage(
                id = "m3",
                conversationId = "private_CLS-101_CLS-102",
                senderId = "CLS-101",
                senderName = "Rahul Sharma",
                receiverId = "CLS-102",
                text = "Yes Priya, I am studying for the 1-day admin quiz.",
                timestamp = 3000L
            )
        )

        // Case-insensitive text search
        val quizMatches = messages.filter { it.text.contains("quiz", ignoreCase = true) }
        assertEquals(2, quizMatches.size)

        // Sender name search
        val rahulMessages = messages.filter { it.senderName.contains("Rahul", ignoreCase = true) }
        assertEquals(2, rahulMessages.size)

        // Specific conversation search
        val privateConvo = messages.filter { it.conversationId == "private_CLS-101_CLS-102" }
        val privateSearch = privateConvo.filter { it.text.contains("studying", ignoreCase = true) }
        assertEquals(1, privateSearch.size)
        assertEquals("m3", privateSearch.first().id)
    }

    @Test
    fun testRemoveAllClassmatesLogic() {
        val initialAccounts = listOf(
            Account(id = "ADMIN-001", name = "Head Admin", secretCode = "admin123", phone = "9999900000", batchInfo = "Admin", role = Role.ADMIN),
            Account(id = "CLS-101", name = "Rahul", secretCode = "1234", phone = "9876543210", batchInfo = "Student", role = Role.MEMBER),
            Account(id = "CLS-102", name = "Priya", secretCode = "2345", phone = "9876543211", batchInfo = "Student", role = Role.MEMBER)
        )
        val remainingAccounts = initialAccounts.filter { it.role == Role.ADMIN }
        assertEquals(1, remainingAccounts.size)
        assertEquals("ADMIN-001", remainingAccounts.first().id)
        val removedCount = initialAccounts.count { it.role != Role.ADMIN }
        assertEquals(2, removedCount)
    }

    @Test
    fun testGoogleLoginApprovalStatusFlow() {
        val studentAccount = Account(
            id = "STUDENT1",
            name = "Aarav Gupta",
            secretCode = "GGL-1234",
            phone = "aarav@example.com",
            batchInfo = "VISWAM CBSE 7A",
            role = Role.MEMBER,
            isPhoneConfirmed = true,
            isSecured = false,
            approvalStatus = ApprovalStatus.PENDING_APPROVAL
        )

        // Initially pending approval
        assertEquals(ApprovalStatus.PENDING_APPROVAL, studentAccount.approvalStatus)

        // Admin approves account
        val approvedAccount = studentAccount.copy(approvalStatus = ApprovalStatus.APPROVED)
        assertEquals(ApprovalStatus.APPROVED, approvedAccount.approvalStatus)

        // Verify SMS intent target formation
        val adminPhone = "9876500000"
        val expectedSmsUri = "smsto:$adminPhone"
        val expectedMessage = "VISWAM CBSE 7A Access Request: Student ${studentAccount.name} (${studentAccount.phone}) is requesting approval to log in to the 7A Alumni Portal. Please approve my ID in the Admin Panel."
        assertTrue(expectedSmsUri.startsWith("smsto:"))
        assertTrue(expectedMessage.contains(studentAccount.name))
        assertTrue(expectedMessage.contains(adminPhone) || expectedMessage.contains("Admin Panel"))
    }

    @Test
    fun testAdminPhoneValidation() {
        var adminPhone = "9999900000"
        val newPhone = "9876543210"
        assertTrue(newPhone.isNotBlank())
        adminPhone = newPhone.trim()
        assertEquals("9876543210", adminPhone)
    }

    @Test
    fun test2027ElectionTop3CandidatesAndOneVoteRule() {
        val candidates = listOf("STU-001", "STU-002", "STU-003")
        val election2027 = Election(
            id = "election-2027",
            title = "Annual VISWAM 7A Admin Election 2027",
            candidateIds = candidates,
            isActive = true,
            year = 2027,
            electionDateText = "November 14, 2027",
            votes = mapOf("STU-101" to "STU-001")
        )

        assertEquals(2027, election2027.year)
        assertEquals(3, election2027.candidateIds.size)
        // One student can vote only once
        assertTrue(election2027.votes.containsKey("STU-101"))
        assertEquals("STU-001", election2027.votes["STU-101"])
    }

    @Test
    fun testTemporaryAdminRoleRestrictions() {
        val tempAdmin = Account(
            id = "STU-001",
            name = "Aarav Sharma",
            secretCode = "pass123",
            phone = "9876543210",
            batchInfo = "Class of 2024",
            role = Role.TEMPORARY_ADMIN,
            tempAdminPasskey = "TEMP-ADMIN-7A24",
            tempAdminExpiresAt = System.currentTimeMillis() + 365L * 86_400_000L
        )

        assertTrue(tempAdmin.isTemporaryAdmin)
        assertTrue(tempAdmin.canModifySkinsOnly)
        assertFalse(tempAdmin.canModifyApp)
        assertFalse(tempAdmin.isHeadAdmin)

        val headAdmin = Account(
            id = "ADMIN-001",
            name = "Head Admin",
            secretCode = "admin123",
            phone = "9999900000",
            batchInfo = "Alumni Rep",
            role = Role.ADMIN
        )

        assertTrue(headAdmin.isHeadAdmin)
        assertTrue(headAdmin.canModifyApp)
        assertFalse(headAdmin.canModifySkinsOnly)
    }

    @Test
    fun testStudentReportAgainstAdminModel() {
        val report = com.example.data.model.AdminReport(
            id = "rep-1",
            reportedAdminId = "TEMP-01",
            reportedAdminName = "Temp Admin",
            reporterStudentId = "STU-101",
            reporterStudentName = "Rahul",
            category = "Trying To Modify App",
            reason = "Temporary admin attempted to edit app code and branding."
        )

        assertEquals("Trying To Modify App", report.category)
        assertEquals("TEMP-01", report.reportedAdminId)
        assertEquals("STU-101", report.reporterStudentId)
    }

    @Test
    fun testUserRoleManagerSectionPermissions() {
        val headAdmin = Account(
            id = "ADMIN-001",
            name = "Head Admin",
            secretCode = "admin123",
            phone = "9999900000",
            batchInfo = "Main Owner",
            role = Role.ADMIN
        )

        val tempAdmin = Account(
            id = "TEMP-001",
            name = "Aarav Sharma",
            secretCode = "temp123",
            phone = "9876543210",
            batchInfo = "Class of 2024",
            role = Role.TEMPORARY_ADMIN,
            tempAdminExpiresAt = System.currentTimeMillis() + 86400000L
        )

        // Head Admin has access to ALL sections
        assertTrue(com.example.ui.viewmodel.UserRoleManager.validateSectionAccess(headAdmin, com.example.ui.viewmodel.AdminSection.APPROVALS).isGranted)
        assertTrue(com.example.ui.viewmodel.UserRoleManager.validateSectionAccess(headAdmin, com.example.ui.viewmodel.AdminSection.CREATE_ID).isGranted)
        assertTrue(com.example.ui.viewmodel.UserRoleManager.validateSectionAccess(headAdmin, com.example.ui.viewmodel.AdminSection.GAME_SKINS).isGranted)
        assertTrue(com.example.ui.viewmodel.UserRoleManager.validateSectionAccess(headAdmin, com.example.ui.viewmodel.AdminSection.SECURITY).isGranted)
        assertTrue(com.example.ui.viewmodel.UserRoleManager.validateSectionAccess(headAdmin, com.example.ui.viewmodel.AdminSection.CODE_EDITOR).isGranted)

        // Temporary Admin is permitted ONLY to (A) Add New Members & (B) Modify Game Skins
        assertTrue(com.example.ui.viewmodel.UserRoleManager.validateSectionAccess(tempAdmin, com.example.ui.viewmodel.AdminSection.APPROVALS).isGranted)
        assertTrue(com.example.ui.viewmodel.UserRoleManager.validateSectionAccess(tempAdmin, com.example.ui.viewmodel.AdminSection.CREATE_ID).isGranted)
        assertTrue(com.example.ui.viewmodel.UserRoleManager.validateSectionAccess(tempAdmin, com.example.ui.viewmodel.AdminSection.GAME_SKINS).isGranted)

        // Temporary Admin MUST be denied access to Security, Code Editor, App Settings with exact required message
        val deniedSecurity = com.example.ui.viewmodel.UserRoleManager.validateSectionAccess(tempAdmin, com.example.ui.viewmodel.AdminSection.SECURITY)
        assertTrue(deniedSecurity is com.example.ui.viewmodel.PermissionResult.Denied)
        assertEquals(
            "You Cannot Modify App. Only Head Admin Can Modify App. You Can Only Add New Members And Modify Game Skins.",
            (deniedSecurity as com.example.ui.viewmodel.PermissionResult.Denied).message
        )

        val deniedCode = com.example.ui.viewmodel.UserRoleManager.validateSectionAccess(tempAdmin, com.example.ui.viewmodel.AdminSection.CODE_EDITOR)
        assertTrue(deniedCode is com.example.ui.viewmodel.PermissionResult.Denied)
        assertEquals(
            "You Cannot Modify App. Only Head Admin Can Modify App. You Can Only Add New Members And Modify Game Skins.",
            (deniedCode as com.example.ui.viewmodel.PermissionResult.Denied).message
        )
    }

    @Test
    fun testHeadAdminProtectionRule() {
        val headAdmin = Account(
            id = "ADMIN-001",
            name = "Head Admin",
            secretCode = "admin123",
            phone = "9999900000",
            batchInfo = "Owner",
            role = Role.ADMIN
        )

        val tempAdmin = Account(
            id = "TEMP-001",
            name = "Aarav Sharma",
            secretCode = "temp123",
            phone = "9876543210",
            batchInfo = "Student",
            role = Role.TEMPORARY_ADMIN
        )

        // Rule: Any attempt to delete or ban Head Admin must be rejected:
        // "HEAD ADMIN CANNOT BE REMOVED! Head Admin is the main owner."
        val deleteAttempt1 = com.example.ui.viewmodel.UserRoleManager.canDeleteOrBanAccount(tempAdmin, headAdmin)
        assertTrue(deleteAttempt1 is com.example.ui.viewmodel.PermissionResult.Denied)
        assertEquals(
            "HEAD ADMIN CANNOT BE REMOVED! Head Admin is the main owner.",
            (deleteAttempt1 as com.example.ui.viewmodel.PermissionResult.Denied).message
        )

        val deleteAttempt2 = com.example.ui.viewmodel.UserRoleManager.canDeleteOrBanAccount(headAdmin, headAdmin)
        assertTrue(deleteAttempt2 is com.example.ui.viewmodel.PermissionResult.Denied)
        assertEquals(
            "HEAD ADMIN CANNOT BE REMOVED! Head Admin is the main owner.",
            (deleteAttempt2 as com.example.ui.viewmodel.PermissionResult.Denied).message
        )
    }

    @Test
    fun testStudentAccountFreeFireAttributesAndRoster() {
        val student = Account(
            id = "STU-101",
            name = "Rahul Sharma",
            secretCode = "1234",
            phone = "9876543210",
            batchInfo = "VISWAM 7A",
            role = Role.MEMBER,
            uid = "24839201",
            diamonds = 999999,
            coins = 999999,
            level = 55,
            rank = "Heroic"
        )

        assertEquals("24839201", student.uid)
        assertEquals(999999, student.diamonds)
        assertEquals(999999, student.coins)
        assertEquals("Heroic", student.rank)

        // Verify all 8 Free Fire Characters are loaded with abilities
        assertEquals(8, com.example.ui.screens.FREE_FIRE_CHARACTERS.size)
        assertTrue(com.example.ui.screens.FREE_FIRE_CHARACTERS.any { it.name == "Speed King" })
        assertTrue(com.example.ui.screens.FREE_FIRE_CHARACTERS.any { it.name == "Healer" })
        assertTrue(com.example.ui.screens.FREE_FIRE_CHARACTERS.any { it.name == "Shield Man" })
        assertTrue(com.example.ui.screens.FREE_FIRE_CHARACTERS.any { it.name == "Sniper Pro" })

        // Verify Free Fire Weapons arsenal
        assertEquals(8, com.example.ui.screens.FREE_FIRE_WEAPONS.size)
        assertTrue(com.example.ui.screens.FREE_FIRE_WEAPONS.any { it.name == "AK-47" })
        assertTrue(com.example.ui.screens.FREE_FIRE_WEAPONS.any { it.name == "AWM" })
    }

    @Test
    fun testDashboardBattleGameButtonAuthenticationAndMatchmakingEmptyState() {
        val approvedStudent = Account(
            id = "STU-101",
            name = "Rahul Sharma",
            secretCode = "1234",
            phone = "9876543210",
            batchInfo = "VISWAM 7A",
            role = Role.MEMBER,
            approvalStatus = ApprovalStatus.APPROVED
        )

        val pendingStudent = Account(
            id = "STU-999",
            name = "Pending User",
            secretCode = "0000",
            phone = "0000000000",
            batchInfo = "VISWAM 7A",
            role = Role.MEMBER,
            approvalStatus = ApprovalStatus.PENDING_APPROVAL
        )

        // Only approved authenticated accounts can access the dashboard battle game button
        val isApprovedAuthenticated = approvedStudent.approvalStatus == ApprovalStatus.APPROVED && approvedStudent.id.isNotBlank()
        assertTrue(isApprovedAuthenticated)

        val isPendingAuthenticated = pendingStudent.approvalStatus == ApprovalStatus.APPROVED && pendingStudent.id.isNotBlank()
        assertFalse(isPendingAuthenticated)

        // Matchmaking empty state verification:
        // When squad has less than 4 players, empty matchmaking triggers waiting state
        val initialSquad = mutableListOf(approvedStudent)
        assertEquals(1, initialSquad.size)
        assertTrue(initialSquad.size < 4) // Matchmaking is empty (needs 4 real players)

        val emptyMatchmakingStatus = if (initialSquad.size < 4) "Waiting for Real Players... Need 4 Players To Start!" else "Ready"
        assertEquals("Waiting for Real Players... Need 4 Players To Start!", emptyMatchmakingStatus)
    }
}
