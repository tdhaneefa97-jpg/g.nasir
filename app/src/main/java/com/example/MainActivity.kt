package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.data.datastore.AuthDataStore
import com.example.data.repository.ClassmateRepository
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MainAppScreen
import com.example.ui.screens.ViswamIntroScreen
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = ClassmateRepository.getInstance(this)

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ClassmateApp(repository = repository)
                }
            }
        }
    }
}

@Composable
fun ClassmateApp(repository: ClassmateRepository) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val authDataStore = remember { AuthDataStore(context) }
    val credentialManager = remember { androidx.credentials.CredentialManager.create(context) }

    var showIntroAnimation by rememberSaveable { mutableStateOf(true) }

    val currentUser by repository.currentUser.collectAsState()
    val impersonatedUser by repository.impersonatedUser.collectAsState()
    val accounts by repository.accounts.collectAsState()
    val chatMessages by repository.chatMessages.collectAsState()
    val activeQuiz by repository.activeQuiz.collectAsState()
    val activeElection by repository.activeElection.collectAsState()
    val activityLogs by repository.activityLogs.collectAsState()
    val isNearVotingDays by repository.isNearVotingDays.collectAsState()

    // Automatic login restoration via Firebase Auth + DataStore if 'Remember Me' was chosen
    LaunchedEffect(Unit) {
        com.example.data.auth.FirebaseAuthManager.attemptAutoSignIn(
            context = context,
            credentialManager = credentialManager,
            scope = coroutineScope,
            onAuthSuccess = { /* Firebase user auto-restored */ },
            onUnauthenticated = { /* Continues to normal auth state */ }
        )

        val session = authDataStore.getSavedSessionOnce()
        if (session != null && session.rememberMe) {
            if (session.isAdmin && !session.adminPasskey.isNullOrBlank()) {
                val res = repository.adminLogin(session.adminPasskey, session.secretCode)
                if (res.isFailure) {
                    authDataStore.clearSession()
                }
            } else {
                val res = repository.loginWithIdAndCode(session.id, session.secretCode)
                if (res.isFailure) {
                    authDataStore.clearSession()
                }
            }
        }
    }

    if (showIntroAnimation) {
        ViswamIntroScreen(
            onFinished = { showIntroAnimation = false }
        )
    } else if (currentUser == null) {
        LoginScreen(
            repository = repository,
            authDataStore = authDataStore,
            isNearVotingDays = isNearVotingDays,
            onLoginSuccess = { /* Session saved in DataStore inside LoginScreen, StateFlow updates automatically */ }
        )
    } else {
        MainAppScreen(
            currentUser = currentUser!!,
            impersonatedUser = impersonatedUser,
            repository = repository,
            accounts = accounts,
            chatMessages = chatMessages,
            activeQuiz = activeQuiz,
            activeElection = activeElection,
            activityLogs = activityLogs,
            isNearVotingDays = isNearVotingDays,
            onLogout = {
                coroutineScope.launch {
                    authDataStore.clearSession()
                }
                com.example.data.auth.FirebaseAuthManager.signOut(context, credentialManager, coroutineScope) {
                    repository.logout()
                }
            }
        )
    }
}
