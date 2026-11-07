package com.example.data.model

enum class Role {
    MEMBER,
    ADMIN,
    ONE_DAY_ADMIN,
    TEMPORARY_ADMIN;

    val displayTitle: String
        get() = when (this) {
            MEMBER -> "Classmate"
            ADMIN -> "Head Admin 🛡️"
            TEMPORARY_ADMIN -> "Temporary Admin (1-Yr) ⚡"
            ONE_DAY_ADMIN -> "1-Day Admin 👑"
        }
}

enum class ApprovalStatus {
    APPROVED,
    PENDING_APPROVAL,
    REJECTED
}

data class Account(
    val id: String,
    val name: String,
    val secretCode: String,
    val phone: String,
    val batchInfo: String,
    val role: Role = Role.MEMBER,
    val isPhoneConfirmed: Boolean = false,
    val isSecured: Boolean = false,
    val approvalStatus: ApprovalStatus = ApprovalStatus.APPROVED,
    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long? = null,
    val oneDayAdminExpiresAt: Long? = null,
    val tempAdminExpiresAt: Long? = null,
    val uid: String = "24839201",
    val diamonds: Int = 999999,
    val coins: Int = 999999,
    val level: Int = 1,
    val rank: String = "Heroic",
    val tempAdminPasskey: String? = null
) {
    val effectiveRole: Role
        get() {
            if (role == Role.TEMPORARY_ADMIN) {
                val expiry = tempAdminExpiresAt ?: 0L
                if (expiry > 0L && System.currentTimeMillis() > expiry) {
                    return Role.MEMBER
                }
            }
            if (role == Role.ONE_DAY_ADMIN) {
                val expiry = oneDayAdminExpiresAt ?: 0L
                if (expiry > 0L && System.currentTimeMillis() > expiry) {
                    return Role.MEMBER
                }
            }
            return role
        }

    val isHeadAdmin: Boolean
        get() = effectiveRole == Role.ADMIN

    val isTemporaryAdmin: Boolean
        get() = effectiveRole == Role.TEMPORARY_ADMIN

    val canModifyApp: Boolean
        get() = isHeadAdmin

    val canModifySkinsOnly: Boolean
        get() = isTemporaryAdmin

    val isAdminOrOneDayAdmin: Boolean
        get() = effectiveRole == Role.ADMIN || effectiveRole == Role.ONE_DAY_ADMIN || effectiveRole == Role.TEMPORARY_ADMIN
}

data class ChatMessage(
    val id: String,
    val conversationId: String, // "public" or sorted pair "private_A_B"
    val senderId: String,
    val senderName: String,
    val receiverId: String? = null,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class Quiz(
    val id: String,
    val title: String,
    val question: String,
    val options: List<String>,
    val correctOptionIndex: Int,
    val caption: String,
    val isActive: Boolean = true,
    val winnerId: String? = null,
    val winnerName: String? = null,
    val winnerCaption: String? = null,
    val submissions: Map<String, Int> = emptyMap() // studentId -> optionIndex
)

data class CandidateProfile(
    val studentId: String,
    val studentName: String,
    val examScore: Int = 95,
    val canTakeCareScore: Int = 92,
    val aiEvaluation: String = "Certified by Classmate AI: Capable of adding members & modifying game skins.",
    val voteCount: Int = 0
)

data class ExamQuestion(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctOptionIndex: Int,
    val category: String,
    val aiExplanation: String
)

data class AdminReport(
    val id: String,
    val timestamp: Long = System.currentTimeMillis(),
    val reportedAdminId: String,
    val reportedAdminName: String,
    val reporterStudentId: String,
    val reporterStudentName: String,
    val category: String, // "Abuse", "Cheating", "Wrong Ban", "Stealing Diamonds", "Bad Behaviour", "Trying To Modify App"
    val reason: String,
    val status: String = "PENDING_HEAD_ADMIN_REVIEW",
    val aiActionTaken: String? = null
)

data class Election(
    val id: String,
    val title: String,
    val candidateIds: List<String>, // Exactly <= 3 candidates allowed
    val isActive: Boolean = true,
    val isNearVotingDays: Boolean = false, // When true, no new IDs can be registered
    val votingEndsAt: Long = System.currentTimeMillis() + 86_400_000L, // 1 day
    val votes: Map<String, String> = emptyMap(), // voterStudentId -> candidateStudentId (1 vote limit)
    val winnerStudentId: String? = null,
    val isFinalized: Boolean = false,
    val year: Int = 2027,
    val electionDateText: String = "November 14, 2027",
    val candidateProfiles: List<CandidateProfile> = emptyList(),
    val winnerTempAdminCode: String? = null,
    val isAiHandlingDueToReport: Boolean = false,
    val aiHandlingEndsAt: Long? = null
)

data class ActivityLog(
    val id: String,
    val timestamp: Long = System.currentTimeMillis(),
    val title: String,
    val details: String,
    val category: String // "LOGIN", "PHONE", "ACCOUNT", "QUIZ", "ELECTION", "REPORT"
)
