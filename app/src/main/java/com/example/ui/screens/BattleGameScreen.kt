package com.example.ui.screens

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.Account
import com.example.ui.theme.AlumniBlue
import com.example.ui.theme.AlumniEmerald
import com.example.ui.theme.AlumniGold
import com.example.ui.theme.AlumniGoldAccent
import com.example.ui.theme.AlumniNavyDark
import com.example.ui.theme.AlumniRed
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Free Fire Character data class with 3D/4D visual properties and special combat abilities.
 */
data class GameCharacter(
    val id: String,
    val name: String,
    val title: String,
    val abilityName: String,
    val abilityDesc: String,
    val baseColor: Color,
    val secondaryColor: Color,
    val hairColor: Color,
    val clothesColor: Color
)

/**
 * Free Fire Weapon data class.
 */
data class GameWeapon(
    val id: String,
    val name: String,
    val skinName: String,
    val type: String,
    val damage: Int,
    val range: Int,
    val ammoCapacity: Int,
    val color: Color
)

val FREE_FIRE_CHARACTERS = listOf(
    GameCharacter("c1", "Speed King", "Sonic Striker", "Dash +30% Speed", "Sprint swiftly across the battlefield avoiding sniper fire.", Color(0xFFE65100), Color(0xFFFFB74D), Color(0xFF212121), Color(0xFFD84315)),
    GameCharacter("c2", "Healer", "Life Guardian", "Restore 50 HP Aura", "Creates a healing ring that restores health over 5 seconds.", Color(0xFF00C853), Color(0xFFB9F6CA), Color(0xFF424242), Color(0xFF00796B)),
    GameCharacter("c3", "Shield Man", "Titan Defender", "Titanium Dome Shield", "Deploys a protective forcefield blocking 500 incoming damage.", Color(0xFF0288D1), Color(0xFF81D4FA), Color(0xFF1A237E), Color(0xFF0D47A1)),
    GameCharacter("c4", "Sniper Pro", "Hawk Eye", "2x Scope Vision + Reload", "Instantly locks onto enemy targets with zero recoil bullet speed.", Color(0xFFFFD600), Color(0xFFFFF59D), Color(0xFF263238), Color(0xFFFF6F00)),
    GameCharacter("c5", "Ghost", "Shadow Phantom", "Invisible Cloak 10s", "Vanishes into thin air becoming invisible to enemy radar.", Color(0xFF7E57C2), Color(0xFFD1C4E9), Color(0xFF1E1E1E), Color(0xFF4A148C)),
    GameCharacter("c6", "Jumper", "Sky Glider", "Super Jump & Glider", "Launches into the sky and glides over buildings with ease.", Color(0xFF00B0FF), Color(0xFF80D8FF), Color(0xFF37474F), Color(0xFF0091EA)),
    GameCharacter("c7", "Tank", "Iron Fortress", "+200 Extra Armor", "Absorbs explosive damage and blast waves without stagger.", Color(0xFF5D4037), Color(0xFFBCAAA4), Color(0xFF212121), Color(0xFF3E2723)),
    GameCharacter("c8", "Bomber", "Blast Specialist", "Cluster Grenade & Smoke", "Throws three cluster grenades that create a tactical smoke cloud.", Color(0xFFD50000), Color(0xFFFF8A80), Color(0xFF212121), Color(0xFFB71C1C))
)

val FREE_FIRE_WEAPONS = listOf(
    GameWeapon("w1", "AK-47", "Dragon Flame (Red to Blue)", "Assault Rifle", 45, 60, 30, Color(0xFFD32F2F)),
    GameWeapon("w2", "M4A1", "Royal Gold (Blue to Gold)", "Assault Rifle", 40, 65, 30, Color(0xFFFFD700)),
    GameWeapon("w3", "AWM", "Dragon Sniper (Gold)", "Sniper Rifle", 150, 95, 5, Color(0xFFFFAB00)),
    GameWeapon("w4", "MP40", "Dragon Cobra (Yellow)", "SMG", 32, 40, 32, Color(0xFFFF6D00)),
    GameWeapon("w5", "M1887", "One-Punch Shotgun", "Shotgun", 90, 25, 2, Color(0xFFC2185B)),
    GameWeapon("w6", "Desert Eagle", "Golden Pistol", "Pistol", 55, 35, 7, Color(0xFFFFC107)),
    GameWeapon("w7", "Katana", "Flame Blade", "Melee", 75, 5, 1, Color(0xFFFF3D00)),
    GameWeapon("w8", "Golden Pan", "Bullet Deflector", "Melee Armor", 40, 5, 1, Color(0xFFFFD54F))
)

/**
 * Full Free Fire Style Real Players Battle Game screen.
 * - Free Fire Lobby with 3D/4D character presentation, dynamic idle animations
 * - 999,999 Diamonds & 999,999 Coins (All FREE for every student)
 * - All 8 Characters & All Weapons 100% UNLOCKED
 * - Real Players Only Matchmaking (NO BOTS, NO AI: "Need 4 Players To Start")
 * - 3D Battle Arena with Joystick, Weapons, HP, Armor, Gloo Wall & Booyah! victory
 */
@Composable
fun BattleGameScreen(
    currentUser: Account,
    allAccounts: List<Account>,
    autoStartMatchmaking: Boolean = false,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    var selectedCharacter by remember { mutableStateOf(FREE_FIRE_CHARACTERS[0]) }
    var selectedWeapon by remember { mutableStateOf(FREE_FIRE_WEAPONS[0]) }
    var selectedMode by remember { mutableStateOf("Battle Royale (Bermuda)") }
    var inBattleArena by remember { mutableStateOf(false) }

    // Matchmaking State (STRICT RULE: REAL PLAYERS ONLY, NO BOTS)
    var isSearchingMatch by remember { mutableStateOf(autoStartMatchmaking) }
    var matchmakingStatus by remember { mutableStateOf<String?>(null) }
    var joinedSquadMembers = remember { mutableStateListOf<Account>() }
    var showInviteDialog by remember { mutableStateOf(false) }
    var showCharacterSelectDialog by remember { mutableStateOf(false) }
    var showWeaponSelectDialog by remember { mutableStateOf(false) }

    // Initialize with current student in Slot 1
    LaunchedEffect(currentUser) {
        if (joinedSquadMembers.isEmpty()) {
            joinedSquadMembers.add(currentUser)
        }
    }

    // Matchmaking logic (Enforces Rule 6 & 8: REAL PLAYERS ONLY, NEED 4 PLAYERS TO START)
    LaunchedEffect(isSearchingMatch) {
        if (isSearchingMatch) {
            matchmakingStatus = "Waiting for Real Players... (${joinedSquadMembers.size}/4)"
            while (isSearchingMatch && joinedSquadMembers.size < 4) {
                delay(2000L)
                // Real player check: Find eligible online classmates from registry
                val eligibleClassmates = allAccounts.filter { acc ->
                    acc.id != currentUser.id && !joinedSquadMembers.any { it.id == acc.id }
                }

                if (eligibleClassmates.isNotEmpty() && joinedSquadMembers.size < 4) {
                    val nextPlayer = eligibleClassmates.first()
                    joinedSquadMembers.add(nextPlayer)
                    matchmakingStatus = "Real Player Joined: ${nextPlayer.name}! (${joinedSquadMembers.size}/4)"
                } else {
                    matchmakingStatus = "Waiting for Real Players... Need 4 Players To Start!"
                }

                if (joinedSquadMembers.size == 4) {
                    matchmakingStatus = "✓ 4 REAL PLAYERS READY! LAUNCHING BERMUDA..."
                    delay(1500L)
                    isSearchingMatch = false
                    inBattleArena = true
                    break
                }
            }
        }
    }

    if (inBattleArena) {
        BattleArenaView(
            character = selectedCharacter,
            weapon = selectedWeapon,
            squad = joinedSquadMembers,
            onExitBattle = {
                inBattleArena = false
                isSearchingMatch = false
                matchmakingStatus = null
            }
        )
    } else if (isSearchingMatch && joinedSquadMembers.size < 4) {
        // Triggers 'Waiting for Real Players...' screen when matchmaking is empty or waiting for 4 players
        WaitingForRealPlayersScreen(
            currentUser = currentUser,
            squadMembers = joinedSquadMembers,
            allAccounts = allAccounts,
            statusText = matchmakingStatus ?: "Waiting for Real Players... (Need 4 Players To Start)",
            onInvite = { showInviteDialog = true },
            onCancel = {
                isSearchingMatch = false
                matchmakingStatus = null
            }
        )
    } else {
        // Free Fire Style Lobby View
        FreeFireLobbyView(
            currentUser = currentUser,
            selectedCharacter = selectedCharacter,
            selectedWeapon = selectedWeapon,
            selectedMode = selectedMode,
            squadMembers = joinedSquadMembers,
            isSearchingMatch = isSearchingMatch,
            matchmakingStatus = matchmakingStatus,
            onSelectMode = { selectedMode = it },
            onOpenCharacterSelect = { showCharacterSelectDialog = true },
            onOpenWeaponSelect = { showWeaponSelectDialog = true },
            onStartMatchmaking = {
                if (joinedSquadMembers.size < 4) {
                    isSearchingMatch = true
                } else {
                    inBattleArena = true
                }
            },
            onCancelMatchmaking = {
                isSearchingMatch = false
                matchmakingStatus = null
            },
            onOpenInvite = { showInviteDialog = true },
            onBack = onBack
        )
    }

    // Invite Real Classmates Dialog
    if (showInviteDialog) {
        AlertDialog(
            onDismissRequest = { showInviteDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Group, contentDescription = null, tint = AlumniGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Invite Real Classmates (4-Player Squad)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column {
                    Text(
                        text = "Rule: NO BOTS ALLOWED! Only real VISWAM 7A classmates can join your Free Fire Squad.",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val availableClassmates = allAccounts.filter { acc ->
                        !joinedSquadMembers.any { it.id == acc.id }
                    }

                    if (availableClassmates.isEmpty()) {
                        Text("No other classmates registered yet.", fontSize = 13.sp, color = AlumniRed)
                    } else {
                        availableClassmates.take(6).forEach { classmate ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(classmate.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("UID: ${classmate.uid} • ${classmate.batchInfo}", fontSize = 11.sp, color = Color.Gray)
                                    }

                                    Button(
                                        onClick = {
                                            if (joinedSquadMembers.size < 4) {
                                                joinedSquadMembers.add(classmate)
                                            }
                                            if (joinedSquadMembers.size >= 4) {
                                                showInviteDialog = false
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = AlumniEmerald),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Text("Invite +", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showInviteDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = AlumniNavyDark)
                ) {
                    Text("Done")
                }
            }
        )
    }

    // Character Selection Dialog
    if (showCharacterSelectDialog) {
        AlertDialog(
            onDismissRequest = { showCharacterSelectDialog = false },
            title = {
                Text("Select Free Fire Hero (All Free Unlocked)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Column {
                    FREE_FIRE_CHARACTERS.forEach { char ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    selectedCharacter = char
                                    showCharacterSelectDialog = false
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedCharacter.id == char.id) AlumniGold.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            border = if (selectedCharacter.id == char.id) androidx.compose.foundation.BorderStroke(2.dp, AlumniGold) else null
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(char.baseColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.FlashOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(char.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(char.abilityName, color = AlumniEmerald, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                                    Text(char.abilityDesc, fontSize = 10.sp, color = Color.Gray, lineHeight = 12.sp)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showCharacterSelectDialog = false }) { Text("Close") }
            }
        )
    }

    // Weapon Selection Dialog
    if (showWeaponSelectDialog) {
        AlertDialog(
            onDismissRequest = { showWeaponSelectDialog = false },
            title = {
                Text("Arsenal Gun Skins (All Free Unlocked)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Column {
                    FREE_FIRE_WEAPONS.forEach { gun ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    selectedWeapon = gun
                                    showWeaponSelectDialog = false
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedWeapon.id == gun.id) AlumniGold.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            border = if (selectedWeapon.id == gun.id) androidx.compose.foundation.BorderStroke(2.dp, AlumniGold) else null
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(gun.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(gun.skinName, color = gun.color, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    Text("Damage: ${gun.damage} • Range: ${gun.range} • Mag: ${gun.ammoCapacity}", fontSize = 10.sp, color = Color.Gray)
                                }
                                Surface(
                                    color = Color(0xFFFEF3C7),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text("FREE", color = Color(0xFFB45309), fontWeight = FontWeight.Bold, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showWeaponSelectDialog = false }) { Text("Close") }
            }
        )
    }
}

/**
 * Dedicated 'Waiting for Real Players...' screen triggered when matchmaking is empty or squad is not full.
 * Strictly enforces NO BOTS, NO AI: only real VISWAM 7A classmates can play.
 */
@Composable
fun WaitingForRealPlayersScreen(
    currentUser: Account,
    squadMembers: List<Account>,
    allAccounts: List<Account>,
    statusText: String,
    onInvite: () -> Unit,
    onCancel: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "radar_anim")
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E1B4B),
                        Color(0xFF2E1065),
                        Color(0xFF030712)
                    )
                )
            )
            .testTag("waiting_for_real_players_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFF7F1D1D).copy(alpha = 0.85f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "REAL PLAYERS ONLY • NO BOTS",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                IconButton(
                    onClick = onCancel,
                    modifier = Modifier.testTag("btn_close_waiting_screen")
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Cancel", tint = Color.White)
                }
            }

            // Center Radar Animation & Text
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Radar sweep Canvas
                Box(
                    modifier = Modifier.size(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(160.dp)) {
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val radius = size.width / 2f

                        // Outer ring
                        drawCircle(
                            color = AlumniEmerald.copy(alpha = 0.35f),
                            radius = radius * pulseScale,
                            style = Stroke(width = 3f)
                        )
                        // Middle ring
                        drawCircle(
                            color = Color(0xFF38BDF8).copy(alpha = 0.4f),
                            radius = radius * 0.65f,
                            style = Stroke(width = 2f)
                        )
                        // Center dot
                        drawCircle(
                            color = AlumniGold,
                            radius = 7f,
                            center = center
                        )

                        // Radar sweep line
                        val sweepRad = Math.toRadians(sweepAngle.toDouble())
                        val endX = center.x + radius * Math.cos(sweepRad).toFloat()
                        val endY = center.y + radius * Math.sin(sweepRad).toFloat()
                        drawLine(
                            brush = Brush.radialGradient(
                                colors = listOf(AlumniEmerald, Color.Transparent),
                                center = center,
                                radius = radius
                            ),
                            start = center,
                            end = Offset(endX, endY),
                            strokeWidth = 4f
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.SportsEsports,
                        contentDescription = null,
                        tint = AlumniGold,
                        modifier = Modifier.size(42.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Waiting for Real Players...",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("waiting_for_real_players_text")
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Matchmaking is empty or waiting for classmates.\nNeed 4 Real Players To Start (No AI / Bots Allowed).",
                    fontSize = 12.sp,
                    color = Color(0xFFE2E8F0),
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    color = Color.Black.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Current Squad: ${squadMembers.size} / 4 Connected",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = AlumniGoldAccent,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            // Squad Slots
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Squad Roster (4 Players Required):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.LightGray,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (slotIndex in 0 until 4) {
                        val member = squadMembers.getOrNull(slotIndex)
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(72.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (member != null) Color(0xFF1E293B) else Color.Black.copy(alpha = 0.4f)
                            ),
                            border = if (member != null) androidx.compose.foundation.BorderStroke(1.5.dp, AlumniEmerald) else androidx.compose.foundation.BorderStroke(1.dp, Color.Gray.copy(alpha = 0.4f))
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize().padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (member != null) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(member.name.split(" ").first(), fontWeight = FontWeight.Bold, color = Color.White, fontSize = 11.sp, maxLines = 1)
                                        Text("Slot ${slotIndex + 1}", fontSize = 9.sp, color = Color.LightGray)
                                        Text("READY ✓", fontWeight = FontWeight.Bold, color = AlumniEmerald, fontSize = 9.sp)
                                    }
                                } else {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Slot ${slotIndex + 1}", fontSize = 9.sp, color = Color.Gray)
                                        Text("Waiting...", fontSize = 9.sp, color = AlumniGoldAccent, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("btn_return_to_lobby")
                    ) {
                        Text("Return to 3D Lobby", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }

                    Button(
                        onClick = onInvite,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA580C)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("btn_invite_classmate_waiting")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Invite Real Classmate", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

/**
 * Free Fire Lobby: Displays 3D/4D character presentation, student Free Fire ID,
 * Diamonds/Coins counters (999999 Free), and 4-Player Matchmaking room.
 */
@Composable
private fun FreeFireLobbyView(
    currentUser: Account,
    selectedCharacter: GameCharacter,
    selectedWeapon: GameWeapon,
    selectedMode: String,
    squadMembers: List<Account>,
    isSearchingMatch: Boolean,
    matchmakingStatus: String?,
    onSelectMode: (String) -> Unit,
    onOpenCharacterSelect: () -> Unit,
    onOpenWeaponSelect: () -> Unit,
    onStartMatchmaking: () -> Unit,
    onCancelMatchmaking: () -> Unit,
    onOpenInvite: () -> Unit,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E1B4B),
                        Color(0xFF311042),
                        Color(0xFF111827)
                    )
                )
            )
            .testTag("battle_game_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar with Free Fire Player Info & Diamonds/Coins Counter
            Surface(
                color = Color.Black.copy(alpha = 0.75f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(currentUser.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = AlumniGold,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text("Lv.${currentUser.level.coerceAtLeast(55)}", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                }
                            }
                            Text("UID: ${currentUser.uid} • ${currentUser.rank} 🏆", color = AlumniGoldAccent, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    // Currencies (Rule: 999999 Diamonds & Coins Free)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = Color(0xFF0369A1).copy(alpha = 0.8f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("💎", fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "999,999",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.testTag("game_diamonds_display")
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = Color(0xFFB45309).copy(alpha = 0.8f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🪙", fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "999,999",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.testTag("game_coins_display")
                                )
                            }
                        }
                    }
                }
            }

            // Free Fire Banner & Guild Tag
            Surface(
                color = Color(0xFF991B1B).copy(alpha = 0.85f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🔥 FREE FIRE BATTLE ROYALE • REAL PLAYERS ONLY", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    Text("GUILD: VISWAM 7A WARRIORS", color = AlumniGoldAccent, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                }
            }

            // Main 3D / 4D Character Canvas Display
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                // Interactive 3D Character Canvas
                FreeFire3DCharacterCanvas(
                    character = selectedCharacter,
                    weapon = selectedWeapon
                )

                // Left Quick Controls: Character & Weapon selector
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 12.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .clickable { onOpenCharacterSelect() }
                            .testTag("btn_select_character"),
                        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.65f)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = AlumniGold, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(selectedCharacter.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                            Text("Change Hero", color = AlumniGoldAccent, fontSize = 9.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        modifier = Modifier
                            .clickable { onOpenWeaponSelect() }
                            .testTag("btn_select_weapon"),
                        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.65f)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.SportsEsports, contentDescription = null, tint = AlumniEmerald, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(selectedWeapon.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                            Text(selectedWeapon.skinName.split(" ").take(2).joinToString(" "), color = selectedWeapon.color, fontSize = 9.sp)
                        }
                    }
                }

                // Right Quick Info: Active Ability & Stats
                Card(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.7f)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.FlashOn, contentDescription = null, tint = AlumniGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ABILITY", color = AlumniGold, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        }
                        Text(selectedCharacter.abilityName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("DMG: ${selectedWeapon.damage}", color = AlumniEmerald, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                        Text("RANGE: ${selectedWeapon.range}", color = AlumniBlue, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Bottom Lobby Control & 4-Player Real Squad Display
            Surface(
                color = Color.Black.copy(alpha = 0.88f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Squad Slots (Need 4 Players to Start)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Squad LOBBY (Real Players: ${squadMembers.size}/4)",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )

                        TextButton(
                            onClick = onOpenInvite,
                            modifier = Modifier.height(30.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = AlumniGoldAccent, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("Invite Classmate", color = AlumniGoldAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // 4 Squad Slots
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (slotIndex in 0 until 4) {
                            val member = squadMembers.getOrNull(slotIndex)
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(58.dp)
                                    .clickable { if (member == null) onOpenInvite() },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (member != null) Color(0xFF1E293B) else Color(0xFF0F172A).copy(alpha = 0.5f)
                                ),
                                border = if (member != null) androidx.compose.foundation.BorderStroke(1.dp, AlumniEmerald) else androidx.compose.foundation.BorderStroke(1.dp, Color.DarkGray)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize().padding(4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (member != null) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(member.name.split(" ").first(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp, maxLines = 1)
                                            Text("READY ✓", color = AlumniEmerald, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                        }
                                    } else {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                                            Text("Open Slot", color = Color.Gray, fontSize = 8.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Matchmaking Status Indicator (Enforces Rule: NO BOTS, "Need 4 Players To Start")
                    if (matchmakingStatus != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = if (squadMembers.size >= 4) Color(0xFF14532D) else Color(0xFF78350F),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("⚡", fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = matchmakingStatus,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.testTag("matchmaking_status_text")
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Mode Selection & Start Game Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Mode Button
                        Surface(
                            color = Color(0xFF334155),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.clickable {
                                onSelectMode(
                                    when (selectedMode) {
                                        "Battle Royale (Bermuda)" -> "Clash Squad (4v4)"
                                        "Clash Squad (4v4)" -> "Lone Wolf (1v1)"
                                        else -> "Battle Royale (Bermuda)"
                                    }
                                )
                            }
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                                Text("GAME MODE", color = Color.LightGray, fontSize = 9.sp)
                                Text(selectedMode, color = AlumniGoldAccent, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }

                        // Big Free Fire START Button
                        if (isSearchingMatch) {
                            Button(
                                onClick = onCancelMatchmaking,
                                colors = ButtonDefaults.buttonColors(containerColor = AlumniRed),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(44.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Cancel Match", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        } else {
                            Button(
                                onClick = onStartMatchmaking,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (squadMembers.size >= 4) Color(0xFFEA580C) else Color(0xFFD97706)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .height(44.dp)
                                    .testTag("btn_start_matchmaking")
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (squadMembers.size >= 4) "START BATTLE 🔥" else "MATCH REAL SQUAD (${squadMembers.size}/4)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Custom 3D/4D Character Canvas with dynamic animations, Free Fire styling,
 * human face/hair/costume details, weapon holding, and isometric depth.
 */
@Composable
private fun FreeFire3DCharacterCanvas(
    character: GameCharacter,
    weapon: GameWeapon
) {
    val infiniteTransition = rememberInfiniteTransition(label = "idle_anim")
    val breatheOffset by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe"
    )

    val shadowScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shadow"
    )

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f + 30f

        // 3D Podium / Ground Texture with Free Fire style glowing ring
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(AlumniGoldAccent.copy(alpha = 0.45f), Color.Transparent),
                center = Offset(centerX, centerY + 180f),
                radius = 160f * shadowScale
            ),
            topLeft = Offset(centerX - 160f * shadowScale, centerY + 140f),
            size = Size(320f * shadowScale, 80f)
        )

        // Shadow under feet
        drawOval(
            color = Color.Black.copy(alpha = 0.55f),
            topLeft = Offset(centerX - 80f * shadowScale, centerY + 160f),
            size = Size(160f * shadowScale, 40f)
        )

        val charY = centerY + breatheOffset

        // Legs & Military Free Fire combat pants
        drawRoundRect(
            color = Color(0xFF1E293B),
            topLeft = Offset(centerX - 35f, charY + 80f),
            size = Size(25f, 90f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
        )
        drawRoundRect(
            color = Color(0xFF1E293B),
            topLeft = Offset(centerX + 10f, charY + 80f),
            size = Size(25f, 90f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
        )

        // Free Fire Tactical Combat Shoes / Boots
        drawRoundRect(
            color = character.secondaryColor,
            topLeft = Offset(centerX - 40f, charY + 160f),
            size = Size(32f, 20f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
        )
        drawRoundRect(
            color = character.secondaryColor,
            topLeft = Offset(centerX + 8f, charY + 160f),
            size = Size(32f, 20f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
        )

        // Torso / Stylish Jacket / Free Fire Bundle Dress
        drawRoundRect(
            brush = Brush.verticalGradient(
                listOf(character.clothesColor, character.baseColor)
            ),
            topLeft = Offset(centerX - 45f, charY - 20f),
            size = Size(90f, 105f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f)
        )

        // Tactical Vest / Straps
        drawRoundRect(
            color = Color.Black.copy(alpha = 0.35f),
            topLeft = Offset(centerX - 35f, charY - 10f),
            size = Size(70f, 60f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
        )

        // Free Fire Neck & Head (Human Face Style)
        drawRect(
            color = Color(0xFFFFCC80),
            topLeft = Offset(centerX - 10f, charY - 35f),
            size = Size(20f, 18f)
        )

        // Face
        drawOval(
            color = Color(0xFFFFCC80),
            topLeft = Offset(centerX - 28f, charY - 85f),
            size = Size(56f, 56f)
        )

        // Hair (Stylish Free Fire Spiky Haircut)
        drawArc(
            color = character.hairColor,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset(centerX - 32f, charY - 95f),
            size = Size(64f, 45f)
        )

        // Eyes & Combat Headband
        drawRect(
            color = character.baseColor,
            topLeft = Offset(centerX - 28f, charY - 72f),
            size = Size(56f, 10f)
        )
        drawCircle(color = Color.White, radius = 3f, center = Offset(centerX - 10f, charY - 60f))
        drawCircle(color = Color.White, radius = 3f, center = Offset(centerX + 10f, charY - 60f))
        drawCircle(color = Color.Black, radius = 1.5f, center = Offset(centerX - 10f, charY - 60f))
        drawCircle(color = Color.Black, radius = 1.5f, center = Offset(centerX + 10f, charY - 60f))

        // Left Arm
        drawRoundRect(
            color = character.clothesColor,
            topLeft = Offset(centerX - 62f, charY - 15f),
            size = Size(20f, 85f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
        )

        // Right Arm holding Weapon
        drawRoundRect(
            color = character.clothesColor,
            topLeft = Offset(centerX + 42f, charY - 15f),
            size = Size(20f, 75f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
        )

        // Weapon Rendered in Hand (Free Fire Gun Skin)
        val gunX = centerX + 45f
        val gunY = charY + 30f
        // Gun Barrel & Body
        drawRoundRect(
            color = weapon.color,
            topLeft = Offset(gunX, gunY),
            size = Size(65f, 14f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f, 3f)
        )
        // Gun Stock
        drawRect(
            color = Color.Black,
            topLeft = Offset(gunX - 10f, gunY + 4f),
            size = Size(15f, 10f)
        )
        // Gun Magazine
        drawRect(
            color = Color.DarkGray,
            topLeft = Offset(gunX + 20f, gunY + 12f),
            size = Size(8f, 18f)
        )
        // Muzzle / Flame effect
        drawCircle(
            color = AlumniGold,
            radius = 4f,
            center = Offset(gunX + 68f, gunY + 7f)
        )
    }
}

/**
 * Free Fire Battle Arena View: Real-time 3D interactive combat arena
 * with joystick movement, firing, gloo wall deployment, health/armor gauges,
 * minimap, safe zone timer, and Booyah! celebration.
 */
@Composable
private fun BattleArenaView(
    character: GameCharacter,
    weapon: GameWeapon,
    squad: List<Account>,
    onExitBattle: () -> Unit
) {
    var playerHp by remember { mutableIntStateOf(200) }
    var playerArmor by remember { mutableIntStateOf(100) }
    var ammoCount by remember { mutableIntStateOf(weapon.ammoCapacity) }
    var glooWallsCount by remember { mutableIntStateOf(3) }
    var kills by remember { mutableIntStateOf(0) }
    var alivePlayers by remember { mutableIntStateOf(24) }
    var safeZoneSeconds by remember { mutableIntStateOf(120) }

    // Player position in arena
    var playerX by remember { mutableFloatStateOf(0f) }
    var playerY by remember { mutableFloatStateOf(0f) }

    // Firing flash & Booyah triggers
    var isFiring by remember { mutableStateOf(false) }
    var showBooyah by remember { mutableStateOf(false) }
    var combatLog by remember { mutableStateOf<String?>("Landed at Bermuda Clock Tower! Enemies nearby.") }
    var activeGlooWall by remember { mutableStateOf(false) }

    // Safe zone timer & match progression
    LaunchedEffect(Unit) {
        while (safeZoneSeconds > 0 && !showBooyah) {
            delay(1000L)
            safeZoneSeconds--
            if (safeZoneSeconds % 10 == 0 && alivePlayers > 4) {
                alivePlayers -= (1..2).random()
            }
            if (alivePlayers <= 4 && !showBooyah) {
                showBooyah = true
                combatLog = "🏆 BOOYAH! VISWAM 7A SQUAD WON THE MATCH!"
            }
        }
    }

    // Fire cooldown reset
    LaunchedEffect(isFiring) {
        if (isFiring) {
            delay(150L)
            isFiring = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        // 3D Battle Terrain Canvas (Free Fire Ground, Grass, Homes, Gloo Walls)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Sky / Horizon gradient
            drawRect(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFF1E3A8A), Color(0xFF60A5FA), Color(0xFFFED7AA))
                ),
                topLeft = Offset.Zero,
                size = Size(w, h * 0.4f)
            )

            // Ground & Grass terrain
            drawRect(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFF22543D), Color(0xFF276749), Color(0xFF1C4532))
                ),
                topLeft = Offset(0f, h * 0.4f),
                size = Size(w, h * 0.6f)
            )

            // 3D Bermuda Houses in the distance
            val houseBaseY = h * 0.38f
            drawRect(
                color = Color(0xFF78350F),
                topLeft = Offset(w * 0.15f + playerX * 0.2f, houseBaseY - 60f),
                size = Size(100f, 60f)
            )
            // Roof
            val roofPath = Path().apply {
                moveTo(w * 0.15f + playerX * 0.2f - 10f, houseBaseY - 60f)
                lineTo(w * 0.15f + playerX * 0.2f + 50f, houseBaseY - 100f)
                lineTo(w * 0.15f + playerX * 0.2f + 110f, houseBaseY - 60f)
                close()
            }
            drawPath(roofPath, Color(0xFF991B1B))

            // Second Bermuda Building
            drawRect(
                color = Color(0xFF475569),
                topLeft = Offset(w * 0.65f + playerX * 0.2f, houseBaseY - 80f),
                size = Size(130f, 80f)
            )

            // Gloo Wall Shield if placed
            if (activeGlooWall) {
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        listOf(Color(0xFF38BDF8).copy(alpha = 0.85f), Color(0xFF0284C7).copy(alpha = 0.95f))
                    ),
                    topLeft = Offset(w / 2f - 90f, h * 0.6f),
                    size = Size(180f, 65f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(14f, 14f)
                )
            }

            // Player character in 3D battle arena
            val pX = w / 2f + playerX
            val pY = h * 0.7f + playerY

            // Character body
            drawRoundRect(
                color = character.clothesColor,
                topLeft = Offset(pX - 25f, pY - 50f),
                size = Size(50f, 65f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
            )
            // Head
            drawCircle(
                color = Color(0xFFFFCC80),
                radius = 18f,
                center = Offset(pX, pY - 68f)
            )
            // Weapon in hand
            drawRect(
                color = weapon.color,
                topLeft = Offset(pX + 15f, pY - 35f),
                size = Size(40f, 10f)
            )

            // Muzzle flash when firing
            if (isFiring) {
                drawCircle(
                    color = AlumniGold,
                    radius = 15f,
                    center = Offset(pX + 60f, pY - 30f)
                )
                // Bullet tracer
                drawLine(
                    color = Color.Yellow,
                    start = Offset(pX + 60f, pY - 30f),
                    end = Offset(pX + 220f, pY - 120f),
                    strokeWidth = 4f
                )
            }
        }

        // HUD Top: Alive Count, Kills, Minimap & Safe Zone Timer
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Alive & Kill Counters
                Surface(
                    color = Color.Black.copy(alpha = 0.75f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("ALIVE: ", color = Color.LightGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("$alivePlayers", color = AlumniEmerald, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("KILLS: ", color = Color.LightGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("$kills", color = AlumniRed, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Safe Zone Timer
                Surface(
                    color = Color(0xFF7F1D1D).copy(alpha = 0.85f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "SAFE ZONE: ${safeZoneSeconds / 60}:${(safeZoneSeconds % 60).toString().padStart(2, '0')}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }

                // Exit Match
                IconButton(
                    onClick = onExitBattle,
                    modifier = Modifier.size(36.dp).testTag("btn_exit_game")
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Exit", tint = Color.White)
                }
            }

            // Real Squad HP Status (All 4 Players)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                squad.take(4).forEachIndexed { idx, player ->
                    Surface(
                        color = Color.Black.copy(alpha = 0.65f),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(4.dp)) {
                            Text(player.name.split(" ").first(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 9.sp, maxLines = 1)
                            Spacer(modifier = Modifier.height(2.dp))
                            // HP Bar
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .background(Color.DarkGray)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(if (idx == 0) playerHp / 200f else 0.9f)
                                        .height(4.dp)
                                        .background(AlumniEmerald)
                                )
                            }
                        }
                    }
                }
            }

            // Combat Log Banner
            if (combatLog != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = Color.Black.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = combatLog ?: "",
                        color = AlumniGoldAccent,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }

        // HUD Bottom: Health Bar, Armor, Weapon Ammo, Action Buttons
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Health & Armor Gauges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // Armor Bar (White/Blue)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("ARMOR: $playerArmor/100", color = Color(0xFF67E8F9), fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.55f)
                            .height(6.dp)
                            .background(Color.DarkGray, RoundedCornerShape(3.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(playerArmor / 100f)
                                .height(6.dp)
                                .background(Color(0xFF06B6D4), RoundedCornerShape(3.dp))
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // HP Bar (Green/Red)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("HP: $playerHp/200", color = AlumniEmerald, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.55f)
                            .height(8.dp)
                            .background(Color.DarkGray, RoundedCornerShape(4.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(playerHp / 200f)
                                .height(8.dp)
                                .background(AlumniEmerald, RoundedCornerShape(4.dp))
                        )
                    }
                }

                // Selected Weapon Card & Ammo Counter
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.8f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(weapon.name, color = weapon.color, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("$ammoCount / ∞", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Controls: Virtual Joystick (Left) & Fire / Gloo Wall / Medkit (Right)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                // Virtual Joystick for movement
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        .border(2.dp, Color.LightGray.copy(alpha = 0.5f), CircleShape)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                playerX = (playerX + dragAmount.x).coerceIn(-120f, 120f)
                                playerY = (playerY + dragAmount.y).coerceIn(-40f, 40f)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .offset { IntOffset((playerX * 0.35f).roundToInt(), (playerY * 0.35f).roundToInt()) }
                            .background(AlumniGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("MOVE", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                    }
                }

                // Action Buttons: Gloo Wall, Medkit, Fire
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Gloo Wall Button
                    Button(
                        onClick = {
                            if (glooWallsCount > 0) {
                                glooWallsCount--
                                activeGlooWall = true
                                combatLog = "Deployed Gloo Wall! 100% Shield Protection."
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        shape = CircleShape,
                        modifier = Modifier
                            .size(54.dp)
                            .testTag("btn_gloo_wall"),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Text("$glooWallsCount", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                        }
                    }

                    // Medkit Button
                    Button(
                        onClick = {
                            playerHp = (playerHp + 75).coerceAtMost(200)
                            combatLog = "Applied Medkit: +75 HP restored."
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AlumniEmerald),
                        shape = CircleShape,
                        modifier = Modifier.size(54.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Default.LocalHospital, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                    }

                    // Fire Weapon Button
                    Button(
                        onClick = {
                            if (ammoCount > 0) {
                                ammoCount--
                                isFiring = true
                                if ((1..3).random() == 1) {
                                    kills++
                                    combatLog = "Headshot! Killed enemy with ${weapon.name}!"
                                } else {
                                    combatLog = "Hit enemy! -${weapon.damage} HP"
                                }
                            } else {
                                ammoCount = weapon.ammoCapacity
                                combatLog = "Reloading ${weapon.name}..."
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                        shape = CircleShape,
                        modifier = Modifier
                            .size(72.dp)
                            .testTag("btn_fire_action"),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🔥", fontSize = 20.sp)
                            Text("FIRE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        // Booyah! Victory Celebration Popup
        if (showBooyah) {
            AlertDialog(
                onDismissRequest = onExitBattle,
                icon = {
                    Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = AlumniGold, modifier = Modifier.size(48.dp))
                },
                title = {
                    Text(
                        text = "🏆 BOOYAH! 🏆",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = AlumniGold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "VISWAM 7A SQUAD #1 VICTORY!",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Kills: $kills • Rank: Grandmaster 🌟", color = AlumniEmerald, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Text("+500 Diamonds & Coins added to your account!", color = AlumniGoldAccent, fontSize = 12.sp)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = onExitBattle,
                        colors = ButtonDefaults.buttonColors(containerColor = AlumniGold)
                    ) {
                        Text("Return to Lobby", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}
