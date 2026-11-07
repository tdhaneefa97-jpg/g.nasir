package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Account
import com.example.data.model.ChatMessage
import com.example.data.repository.ClassmateRepository
import com.example.ui.components.AvatarCircle
import com.example.ui.components.RoleBadge
import com.example.ui.theme.AlumniBlue
import com.example.ui.theme.AlumniEmerald
import com.example.ui.theme.AlumniNavyDark
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    currentUser: Account,
    repository: ClassmateRepository,
    allAccounts: List<Account>,
    messages: List<ChatMessage>
) {
    var selectedChatTab by remember { mutableIntStateOf(0) } // 0: Public Chat, 1: Private Chats
    var selectedPrivatePartnerId by remember { mutableStateOf<String?>(null) }

    // Admin Monitor Mode: allows admin to select any User A and User B
    val isAdmin = currentUser.isAdminOrOneDayAdmin
    var adminInspectorMode by remember { mutableStateOf(false) }
    var inspectUserA by remember { mutableStateOf(allAccounts.firstOrNull { it.id != currentUser.id }?.id ?: "") }
    var inspectUserB by remember { mutableStateOf(allAccounts.find { it.id != currentUser.id && it.id != inspectUserA }?.id ?: "") }

    Column(modifier = Modifier.fillMaxSize()) {
        // Chat Type Tabs
        PrimaryTabRow(selectedTabIndex = selectedChatTab) {
            Tab(
                selected = selectedChatTab == 0,
                onClick = {
                    selectedChatTab = 0
                    selectedPrivatePartnerId = null
                },
                text = { Text("Public Classroom Chat", fontWeight = FontWeight.SemiBold) },
                icon = { Icon(Icons.Default.People, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.testTag("tab_public_chat")
            )
            Tab(
                selected = selectedChatTab == 1,
                onClick = { selectedChatTab = 1 },
                text = { Text("Private Direct Chats", fontWeight = FontWeight.SemiBold) },
                icon = { Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.testTag("tab_private_chat")
            )
        }

        if (selectedChatTab == 0) {
            // Public Chat
            PublicChatView(
                currentUser = currentUser,
                messages = messages.filter { it.conversationId == "public" },
                onSendMessage = { text ->
                    repository.sendMessage("public", text, null)
                }
            )
        } else {
            // Private Chats
            if (isAdmin && adminInspectorMode) {
                // Admin Overseer Monitor
                AdminChatInspectorView(
                    currentUser = currentUser,
                    allAccounts = allAccounts,
                    userAId = inspectUserA,
                    userBId = inspectUserB,
                    onSelectUserA = { inspectUserA = it },
                    onSelectUserB = { inspectUserB = it },
                    messages = messages,
                    onExitInspector = { adminInspectorMode = false }
                )
            } else if (selectedPrivatePartnerId != null) {
                // Active 1-on-1 Private Chat
                val partner = allAccounts.find { it.id == selectedPrivatePartnerId }
                if (partner != null) {
                    val conversationId = repository.getPrivateConversationId(currentUser.id, partner.id)
                    PrivateConversationView(
                        currentUser = currentUser,
                        partner = partner,
                        messages = messages.filter { it.conversationId == conversationId },
                        onBack = { selectedPrivatePartnerId = null },
                        onSendMessage = { text ->
                            repository.sendMessage(conversationId, text, partner.id)
                        }
                    )
                } else {
                    selectedPrivatePartnerId = null
                }
            } else {
                // Private Chat Directory (Select partner)
                PrivateChatDirectoryView(
                    currentUser = currentUser,
                    allAccounts = allAccounts.filter { it.id != currentUser.id },
                    messages = messages,
                    repository = repository,
                    isAdmin = isAdmin,
                    onOpenInspector = { adminInspectorMode = true },
                    onSelectPartner = { partnerId ->
                        selectedPrivatePartnerId = partnerId
                    }
                )
            }
        }
    }
}

@Composable
fun PublicChatView(
    currentUser: Account,
    messages: List<ChatMessage>,
    onSendMessage: (String) -> Unit
) {
    var textInput by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    val filteredMessages = remember(messages, searchQuery) {
        if (searchQuery.isBlank()) {
            messages
        } else {
            messages.filter { msg ->
                msg.text.contains(searchQuery, ignoreCase = true) ||
                        msg.senderName.contains(searchQuery, ignoreCase = true) ||
                        msg.senderId.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    LaunchedEffect(filteredMessages.size) {
        if (filteredMessages.isNotEmpty() && !isSearching) {
            listState.animateScrollToItem(filteredMessages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFECE5DD).copy(alpha = 0.35f)) // Classic WhatsApp light backdrop
    ) {
        // Banner info + Search toggle button
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Forum,
                        contentDescription = null,
                        tint = AlumniBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Public Classroom Wall • All classmates can view and chat here",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = {
                        isSearching = !isSearching
                        if (!isSearching) searchQuery = ""
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("btn_search_public_chat")
                ) {
                    Icon(
                        imageVector = if (isSearching) Icons.Default.Close else Icons.Default.Search,
                        contentDescription = if (isSearching) "Close Search" else "Search Messages",
                        tint = if (isSearching) Color.Red else AlumniBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Active Search Bar
        if (isSearching) {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search messages or sender...", fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = AlumniBlue)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = { searchQuery = "" },
                                    modifier = Modifier.testTag("btn_clear_public_search")
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear search", modifier = Modifier.size(18.dp))
                                }
                            }
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AlumniBlue,
                            unfocusedBorderColor = Color.LightGray
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("chat_search_public_input")
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (searchQuery.isBlank()) {
                                "Type keyword to find messages"
                            } else {
                                "${filteredMessages.size} matching message${if (filteredMessages.size != 1) "s" else ""} found"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (filteredMessages.isEmpty() && searchQuery.isNotBlank()) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (searchQuery.isNotBlank()) {
                            Text(
                                text = "Filtered results",
                                fontSize = 10.sp,
                                color = AlumniBlue,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Messages list
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (filteredMessages.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
                        ) {
                            Text(
                                text = if (searchQuery.isNotBlank()) "No messages matched \"$searchQuery\"" else "No messages yet.",
                                modifier = Modifier.padding(14.dp),
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            } else {
                items(filteredMessages, key = { it.id }) { msg ->
                    val isMe = msg.senderId == currentUser.id
                    MessageBubble(
                        message = msg,
                        isMe = isMe,
                        showSenderName = !isMe,
                        highlightQuery = if (isSearching) searchQuery else ""
                    )
                }
            }
        }

        // Message input bar
        MessageInputBar(
            value = textInput,
            onValueChange = { textInput = it },
            onSend = {
                if (textInput.isNotBlank()) {
                    onSendMessage(textInput)
                    textInput = ""
                }
            }
        )
    }
}

@Composable
fun PrivateConversationView(
    currentUser: Account,
    partner: Account,
    messages: List<ChatMessage>,
    onBack: () -> Unit,
    onSendMessage: (String) -> Unit
) {
    var textInput by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    val filteredMessages = remember(messages, searchQuery) {
        if (searchQuery.isBlank()) {
            messages
        } else {
            messages.filter { msg ->
                msg.text.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    LaunchedEffect(filteredMessages.size) {
        if (filteredMessages.isNotEmpty() && !isSearching) {
            listState.animateScrollToItem(filteredMessages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE5DDD5).copy(alpha = 0.4f))
    ) {
        // Conversation Top Bar
        Surface(
            color = AlumniNavyDark,
            shadowElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                AvatarCircle(name = partner.name, size = 36)
                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = partner.name,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "${partner.id} • ${partner.batchInfo}",
                        color = Color(0xFF93C5FD),
                        fontSize = 11.sp
                    )
                }

                RoleBadge(role = partner.role)

                Spacer(modifier = Modifier.width(6.dp))

                // Search button in top bar
                IconButton(
                    onClick = {
                        isSearching = !isSearching
                        if (!isSearching) searchQuery = ""
                    },
                    modifier = Modifier.testTag("btn_search_private_chat")
                ) {
                    Icon(
                        imageVector = if (isSearching) Icons.Default.Close else Icons.Default.Search,
                        contentDescription = if (isSearching) "Close Search" else "Search Chat",
                        tint = Color.White
                    )
                }
            }
        }

        // Active Search Bar in Private Chat
        if (isSearching) {
            Surface(
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search in this chat...", fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = AlumniBlue)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = { searchQuery = "" },
                                    modifier = Modifier.testTag("btn_clear_private_search")
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear search", modifier = Modifier.size(18.dp))
                                }
                            }
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AlumniBlue,
                            unfocusedBorderColor = Color.LightGray
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("chat_search_private_input")
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (searchQuery.isBlank()) {
                                "Type keyword to find messages with ${partner.name}"
                            } else {
                                "${filteredMessages.size} matching message${if (filteredMessages.size != 1) "s" else ""} found"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (filteredMessages.isEmpty() && searchQuery.isNotBlank()) Color.Red else Color.DarkGray
                        )
                    }
                }
            }
        }

        // Security notice banner
        Surface(
            color = Color(0xFFFEF3C7),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = Color(0xFF92400E),
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "End-to-end between you & ${partner.name}. Per school policy, Admins oversee all portal chats.",
                    fontSize = 10.sp,
                    color = Color(0xFF78350F)
                )
            }
        }

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (filteredMessages.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
                        ) {
                            Text(
                                text = if (searchQuery.isNotBlank()) {
                                    "No messages matched \"$searchQuery\""
                                } else {
                                    "Start a private conversation with ${partner.name}!"
                                },
                                modifier = Modifier.padding(12.dp),
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            } else {
                items(filteredMessages, key = { it.id }) { msg ->
                    val isMe = msg.senderId == currentUser.id
                    MessageBubble(
                        message = msg,
                        isMe = isMe,
                        showSenderName = false,
                        highlightQuery = if (isSearching) searchQuery else ""
                    )
                }
            }
        }

        // Input bar
        MessageInputBar(
            value = textInput,
            onValueChange = { textInput = it },
            onSend = {
                if (textInput.isNotBlank()) {
                    onSendMessage(textInput)
                    textInput = ""
                }
            }
        )
    }
}

@Composable
fun PrivateChatDirectoryView(
    currentUser: Account,
    allAccounts: List<Account>,
    messages: List<ChatMessage>,
    repository: ClassmateRepository,
    isAdmin: Boolean,
    onOpenInspector: () -> Unit,
    onSelectPartner: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filtered = allAccounts.filter { partner ->
        val convoId = repository.getPrivateConversationId(currentUser.id, partner.id)
        val hasMatchingMsg = messages.any {
            it.conversationId == convoId && it.text.contains(searchQuery, ignoreCase = true)
        }
        partner.name.contains(searchQuery, ignoreCase = true) ||
                partner.id.contains(searchQuery, ignoreCase = true) ||
                hasMatchingMsg
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        if (isAdmin) {
            // Admin Special Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenInspector() }
                    .testTag("admin_open_inspector_card")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = null,
                        tint = AlumniEmerald,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Admin Chat Inspector Mode",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "View all private conversations between any two classmates.",
                            color = Color.LightGray,
                            fontSize = 11.sp
                        )
                    }
                    Text(
                        text = "Inspect →",
                        color = AlumniEmerald,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search classmates or past messages") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("directory_search_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Select a classmate to send a private message:",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
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
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (searchQuery.isNotBlank()) "No classmates matching \"$searchQuery\"" else "No other classmates registered yet.",
                                modifier = Modifier.padding(16.dp),
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
            items(filtered, key = { it.id }) { partner ->
                val convoId = repository.getPrivateConversationId(currentUser.id, partner.id)
                val lastMsg = messages.filter { it.conversationId == convoId }.maxByOrNull { it.timestamp }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectPartner(partner.id) }
                        .testTag("chat_card_${partner.id}"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AvatarCircle(name = partner.name, size = 42)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = partner.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = partner.id,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = lastMsg?.text ?: "No messages yet • Tap to chat",
                                fontSize = 12.sp,
                                color = if (lastMsg != null) MaterialTheme.colorScheme.onSurfaceVariant else Color.Gray,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminChatInspectorView(
    currentUser: Account,
    allAccounts: List<Account>,
    userAId: String,
    userBId: String,
    onSelectUserA: (String) -> Unit,
    onSelectUserB: (String) -> Unit,
    messages: List<ChatMessage>,
    onExitInspector: () -> Unit
) {
    var expandedA by remember { mutableStateOf(false) }
    var expandedB by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val userA = allAccounts.find { it.id == userAId }
    val userB = allAccounts.find { it.id == userBId }

    val conversationId = if (userA != null && userB != null) {
        if (userA.id < userB.id) "private_${userA.id}_${userB.id}" else "private_${userB.id}_${userA.id}"
    } else ""

    val convoMessages = messages.filter { it.conversationId == conversationId }
    val filteredMessages = remember(convoMessages, searchQuery) {
        if (searchQuery.isBlank()) {
            convoMessages
        } else {
            convoMessages.filter {
                it.text.contains(searchQuery, ignoreCase = true) ||
                        it.senderName.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A)) // High-tech admin dark style
    ) {
        // Inspector Top Bar
        Surface(
            color = Color(0xFF1E293B),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onExitInspector) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Exit",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Admin Oversight: Private Chat Monitor",
                        color = AlumniEmerald,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Viewing logged private chats between classmates",
                        color = Color.LightGray,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Dropdown selectors for User A and User B
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = "Select Two Classmates to Inspect:",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Selector A
                    ExposedDropdownMenuBox(
                        expanded = expandedA,
                        onExpandedChange = { expandedA = !expandedA },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = "${userA?.name ?: userAId} (${userA?.id ?: ""})",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Person 1", color = Color.LightGray) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedA) },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedA,
                            onDismissRequest = { expandedA = false }
                        ) {
                            allAccounts.forEach { acc ->
                                DropdownMenuItem(
                                    text = { Text("${acc.name} (${acc.id})") },
                                    onClick = {
                                        onSelectUserA(acc.id)
                                        expandedA = false
                                    }
                                )
                            }
                        }
                    }

                    // Selector B
                    ExposedDropdownMenuBox(
                        expanded = expandedB,
                        onExpandedChange = { expandedB = !expandedB },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = "${userB?.name ?: userBId} (${userB?.id ?: ""})",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Person 2", color = Color.LightGray) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedB) },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedB,
                            onDismissRequest = { expandedB = false }
                        ) {
                            allAccounts.forEach { acc ->
                                DropdownMenuItem(
                                    text = { Text("${acc.name} (${acc.id})") },
                                    onClick = {
                                        onSelectUserB(acc.id)
                                        expandedB = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Admin in-chat search
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search keywords in this monitored conversation...", fontSize = 12.sp, color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AlumniEmerald) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.LightGray)
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = AlumniEmerald,
                        unfocusedBorderColor = Color.DarkGray
                    ),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("chat_search_admin_input")
                )
            }
        }

        // Messages Display
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (filteredMessages.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (searchQuery.isNotBlank()) {
                                "No messages matched \"$searchQuery\" in this conversation."
                            } else {
                                "No recorded messages between ${userA?.name} and ${userB?.name}."
                            },
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                items(filteredMessages, key = { it.id }) { msg ->
                    val isFromUserA = msg.senderId == userAId
                    MessageBubble(
                        message = msg,
                        isMe = isFromUserA,
                        showSenderName = true,
                        customLabel = if (isFromUserA) "${userA?.name} (${userA?.id})" else "${userB?.name} (${userB?.id})",
                        highlightQuery = searchQuery
                    )
                }
            }
        }
    }
}

@Composable
fun MessageBubble(
    message: ChatMessage,
    isMe: Boolean,
    showSenderName: Boolean,
    customLabel: String? = null,
    highlightQuery: String = ""
) {
    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val formattedTime = timeFormat.format(Date(message.timestamp))

    val hasHighlightMatch = highlightQuery.isNotBlank() &&
            message.text.contains(highlightQuery, ignoreCase = true)

    val bubbleColor = if (isMe) {
        if (hasHighlightMatch) Color(0xFFC7F9BE) else Color(0xFFD9FDD3)
    } else {
        if (hasHighlightMatch) Color(0xFFFEF9C3) else Color.White
    }
    val align = if (isMe) Alignment.End else Alignment.Start

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = align
    ) {
        Card(
            shape = RoundedCornerShape(
                topStart = 14.dp,
                topEnd = 14.dp,
                bottomStart = if (isMe) 14.dp else 2.dp,
                bottomEnd = if (isMe) 2.dp else 14.dp
            ),
            colors = CardDefaults.cardColors(containerColor = bubbleColor),
            elevation = CardDefaults.cardElevation(defaultElevation = if (hasHighlightMatch) 3.dp else 1.dp),
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .widthIn(min = 80.dp, max = 290.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                if (customLabel != null) {
                    Text(
                        text = customLabel,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = if (isMe) Color(0xFF15803D) else Color(0xFF1D4ED8)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                } else if (showSenderName) {
                    Text(
                        text = "${message.senderName} (${message.senderId})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Color(0xFF1D4ED8)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }

                HighlightedText(
                    text = message.text,
                    highlight = highlightQuery,
                    fontSize = 14.sp,
                    color = Color(0xFF111827)
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formattedTime,
                        fontSize = 9.sp,
                        color = Color.Gray
                    )
                    if (isMe) {
                        Spacer(modifier = Modifier.width(3.dp))
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = "Delivered",
                            tint = Color(0xFF3B82F6),
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HighlightedText(
    text: String,
    highlight: String,
    fontSize: TextUnit,
    color: Color,
    modifier: Modifier = Modifier
) {
    if (highlight.isBlank() || !text.contains(highlight, ignoreCase = true)) {
        Text(text = text, fontSize = fontSize, color = color, modifier = modifier)
    } else {
        val annotatedString = buildAnnotatedString {
            var currentIndex = 0
            val lowerText = text.lowercase(Locale.getDefault())
            val lowerHighlight = highlight.lowercase(Locale.getDefault())
            while (currentIndex < text.length) {
                val index = lowerText.indexOf(lowerHighlight, currentIndex)
                if (index == -1) {
                    append(text.substring(currentIndex))
                    break
                }
                append(text.substring(currentIndex, index))
                withStyle(
                    style = SpanStyle(
                        background = Color(0xFFFEF08A), // Soft yellow highlight
                        color = Color(0xFF854D0E),
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append(text.substring(index, index + highlight.length))
                }
                currentIndex = index + highlight.length
            }
        }
        Text(text = annotatedString, fontSize = fontSize, color = color, modifier = modifier)
    }
}

@Composable
fun MessageInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text("Type a message...", fontSize = 14.sp) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input_field")
            )

            Spacer(modifier = Modifier.width(6.dp))

            IconButton(
                onClick = onSend,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(AlumniBlue)
                    .testTag("btn_send_chat")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
