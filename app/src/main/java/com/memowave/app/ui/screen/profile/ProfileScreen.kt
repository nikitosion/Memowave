package com.memowave.app.ui.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.memowave.app.R
import com.memowave.app.domain.model.user.User
import com.memowave.app.ui.theme.MemowaveTheme

@Composable
fun ProfileRoute(navController: NavController, profileViewModel: ProfileViewModel) {
    ProfileScreen(navController, profileViewModel)
}

@Composable
fun ProfileScreen(navController: NavController, profileViewModel: ProfileViewModel) {
    val uiState by profileViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        profileViewModel.loadUserProfile(4L)
    }

    ProfileScreenContent(
        user = uiState.user,
        onSettingsClick = { navController.navigate("app_settings") },
        refreshProfileInfo = { profileViewModel.loadUserProfile(4L) }
    )
}

@Composable
fun ProfileScreenContent(
    user: User,
    onSettingsClick: () -> Unit,
    refreshProfileInfo: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(top = 32.dp, bottom = 32.dp)
    ) {
        IconButton(
            modifier = Modifier
                .align(Alignment.End)
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = CircleShape
                ),
            onClick = onSettingsClick,
        ) {
            Icon(
                modifier = Modifier.size(30.dp),
                painter = painterResource(R.drawable.round_settings_24),
                contentDescription = "Settings button",
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
        Row(
            modifier = Modifier
                .padding(top = 20.dp)
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(40.dp)
                )
                .padding(16.dp)
                .clickable { refreshProfileInfo() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    modifier = Modifier
                        .size(80.dp),
                    painter = painterResource(id = R.drawable.round_circle_24),
                    contentDescription = "User profile image",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                )
                Column() {
                    Text(
                        text = user.username ?: "Username is missing",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.W500
                    )
                    Text(
                        text = user.email ?: "Email is missing",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
            Icon(
                painter = painterResource(id = R.drawable.round_chevron_right_24),
                contentDescription = "Go to profile button"
            )
        }

        Column(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(40.dp)
                )
                .padding(vertical = 24.dp, horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column() {
                    Text(
                        text = "Штурман",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "10 000 XP",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF2E666F)
                    )
                }
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Капитан",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "50 000 XP",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF2E666F)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFF0F4E57),
                        shape = RoundedCornerShape(18.dp)
                    )
                    .padding(vertical = 8.dp, horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = CircleShape
                        )
                        .size(20.dp)
                        .padding(3.dp),
                    painter = painterResource(R.drawable.round_circle_24),
                    contentDescription = "Current level icon",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                LinearProgressIndicator(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp)
                        .height(8.dp),
                    progress = { 0.15f },
                    color = Color(0xFFD2F7FF),
                    trackColor = Color(0xFF99D0DA)
                )
                Icon(
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = CircleShape
                        )
                        .size(20.dp)
                        .padding(3.dp),
                    painter = painterResource(R.drawable.round_circle_24),
                    contentDescription = "Next level icon",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Row(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(listOf(Color(0xFF006875), Color(0xFF2E29BC))),
                    shape = RoundedCornerShape(40.dp)
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.onPrimary,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(8.dp)
                    .size(35.dp)
                    .graphicsLayer(alpha = 0.99f)
                    .drawWithCache {
                        onDrawWithContent {
                            drawContent()
                            drawRect(
                                Brush.linearGradient(
                                    listOf(
                                        Color(0xFF006875),
                                        Color(0xFF2E29BC)
                                    )
                                ), blendMode = BlendMode.SrcAtop
                            )
                        }
                    },
                painter = painterResource(R.drawable.round_crown_24),
                contentDescription = "Streak icon"
            )
            Column(
                modifier = Modifier
                    .padding(start = 8.dp, top = 8.dp, bottom = 8.dp)
                    .weight(1f)
            ) {
                Text(
                    text = "Dive deeper!",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.W500
                )
                Text(
                    text = "Into the unlimited depths of the sea of knowledge",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            Icon(
                painter = painterResource(R.drawable.round_chevron_right_24),
                contentDescription = "Go to subscription details",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        Row(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(40.dp)
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .background(
                        color = Color(0xFFD4E3FF),
                        shape = RoundedCornerShape(35.dp)
                    )
                    .padding(vertical = 12.dp, horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    modifier = Modifier.size(45.dp),
                    painter = painterResource(R.drawable.round_fire_24),
                    contentDescription = "Streak fire icon",
                    tint = Color(0xFF00315D)
                )
                Text(
                    text = "25 days",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF00315D)
                )
                Text(
                    text = "streak",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF89ADE1)
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (i in 1..6) {
                        Column(
                            modifier = Modifier,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val streakColors = when (i < 4) {
                                true -> Pair(Color(0xFF00315D), Color(0xFFD4E3FF))
                                false -> Pair(
                                    Color(0xFF00315D).copy(alpha = 0.2f),
                                    MaterialTheme.colorScheme.surfaceVariant
                                )
                            }

                            Text(
                                text = i.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                color = streakColors.first
                            )
                            Icon(
                                modifier = Modifier
                                    .padding(vertical = 4.dp)
                                    .background(
                                        color = streakColors.second,
                                        shape = CircleShape
                                    )
                                    .padding(4.dp)
                                    .size(20.dp),
                                painter = painterResource(R.drawable.round_fire_24),
                                contentDescription = "Streak icon",
                                tint = streakColors.first
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp),
                        progress = { 0.3f },
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        modifier = Modifier.padding(start = 8.dp),
                        text = "30/100",
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Icon(
                modifier = Modifier.padding(end = 16.dp),
                painter = painterResource(R.drawable.round_chevron_right_24),
                contentDescription = "Go to streak details"
            )
        }

        Column(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(40.dp)
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(6.dp)
                            .size(35.dp),
                        painter = painterResource(R.drawable.round_stars_24),
                        contentDescription = "Achievements icon",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        modifier = Modifier.padding(start = 8.dp),
                        text = "Achievements",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.W500
                    )
                }
                Icon(
                    painter = painterResource(R.drawable.round_chevron_right_24),
                    contentDescription = "Go to achievements details"
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (i in 1..3) {
                    Column(
                        modifier = Modifier,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            modifier = Modifier
                                .offset(y = 16.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    shape = RoundedCornerShape(30.dp)
                                )

                                .padding(12.dp)
                                .size(50.dp),
                            painter = painterResource(R.drawable.round_public_24),
                            contentDescription = "Achievement's icon",
                            tint = MaterialTheme.colorScheme.primaryContainer
                        )
                        Text(
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.08f),
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .padding(top = 24.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
                            text = "First streak",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }

        /*Column(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(40.dp)
                )
                .padding(16.dp)
        ) {
            Row() {
                Icon(
                    painter = painterResource(R.drawable.round_area_chart_24),
                    contentDescription = "Statistics icon"
                )
                Text(
                    text = "Statistics"
                )
                Icon(
                    painter = painterResource(R.drawable.round_chevron_right_24),
                    contentDescription = "Go to achievements details"
                )
            }
        }*/
    }
}

@Preview
@Composable
fun ProfileScreenPreview() {
    MemowaveTheme {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
        ) { paddingValues ->
            Surface(
                modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.background)
                    .fillMaxSize()
                    .padding(
                        top = paddingValues.calculateTopPadding(),
                        bottom = paddingValues.calculateBottomPadding()
                    ),
            ) {
                ProfileScreenContent(
                    User(username = "Albert", email = "albert@example.com"),
                    {},
                    {})
            }
        }
    }
}