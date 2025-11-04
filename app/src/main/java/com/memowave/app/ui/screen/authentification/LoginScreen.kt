package com.memowave.app.ui.screen.authentification

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.memowave.app.R
import com.memowave.app.ui.theme.MemowaveTheme

@Composable
fun LoginRoute() {
    LoginScreen()
}

@Composable
fun LoginScreen() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.memowave_logo_colored_no_surface),
            contentDescription = "Memowave Logo",
            modifier = Modifier.height(60.dp)
        )

        Row(
            modifier = Modifier.padding(top = 24.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Привет, это ",
                style = MaterialTheme.typography.headlineLarge
            )
            Text(
                modifier = Modifier.offset(y = (-4).dp),
                text = "Memowave",
                style = MaterialTheme.typography.headlineLarge.copy(fontFamily = FontFamily(Font(R.font.bagelfatone_regular)))
            )
            Text(
                text = "!",
                style = MaterialTheme.typography.headlineLarge
            )
        }
        Text(
            modifier = Modifier.padding(top = 4.dp),
            text = "Лови волну новых слов и погружайся в язык с головой!",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        val emailState = remember { TextFieldState() }

        OutlinedTextField(
            state = emailState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
            label = { Text("Email") },
            placeholder = { Text("Введите ваш email") },
            leadingIcon = {
                Icon(
                    painter = painterResource(
                        id = R.drawable.round_alternate_email_24
                    ),
                    contentDescription = "Email Icon"
                )
            },
            shape = RoundedCornerShape(30.dp),
        )

        val passwordState = remember { TextFieldState() }
        var passwordHidden by rememberSaveable { mutableStateOf(true) }

        OutlinedSecureTextField(
            state = passwordState,
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
            textObfuscationMode = if (passwordHidden) {
                TextObfuscationMode.RevealLastTyped
            } else {
                TextObfuscationMode.Visible
            },
            label = { Text("Пароль") },
            placeholder = { Text("Введите ваш пароль") },
            leadingIcon = {
                Icon(
                    painter = painterResource(
                        id = R.drawable.round_lock_24
                    ),
                    contentDescription = "Email Icon"
                )
            },
            shape = RoundedCornerShape(30.dp),
            trailingIcon = {
                IconButton(onClick = { passwordHidden = !passwordHidden }) {
                    if (passwordState.text.isEmpty()) {
                        return@IconButton
                    }
                    val iconResId = if (passwordHidden) {
                        R.drawable.round_visibility_24
                    } else {
                        R.drawable.round_visibility_off_24
                    }
                    val description = if (passwordHidden) {
                        "Показать пароль"
                    } else {
                        "Скрыть пароль"
                    }
                    Icon(
                        painter = painterResource(
                            id = iconResId
                        ), contentDescription = description
                    )
                }
            }
        )
        Button(
            modifier = Modifier
                .padding(top = 24.dp)
                .fillMaxWidth()
                .height(60.dp),
            onClick = {}
        ) {
            Text("Войти")
        }

        OutlinedButton(
            modifier = Modifier.padding(top = 20.dp),
            onClick = {},
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
        ) {
            Text("Забыли пароль?")
        }

        Row(
            modifier = Modifier
                .padding(top = 32.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.outlineVariant
            )
            Text(
                modifier = Modifier.padding(horizontal = 8.dp),
                text = "ИЛИ",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.outlineVariant
            )
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.outlineVariant
            )
        }
        Row(modifier = Modifier.padding(top = 24.dp)) {
            IconButton(
                modifier = Modifier
                    .size(65.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = CircleShape
                    ),
                onClick = {},
            ) {
                Image(
                    modifier = Modifier.size(30.dp),
                    painter = painterResource(id = R.drawable.apple_logo_colored),
                    contentDescription = "Apple Logo"
                )
            }
            IconButton(
                modifier = Modifier
                    .padding(start = 24.dp)
                    .size(65.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = CircleShape
                    ),
                onClick = {},
            ) {
                Image(
                    modifier = Modifier.size(30.dp),
                    painter = painterResource(id = R.drawable.yandex_logo_colored),
                    contentDescription = "Apple Logo"
                )
            }
            IconButton(
                modifier = Modifier
                    .padding(start = 24.dp)
                    .size(65.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = CircleShape
                    ),
                onClick = {},
            ) {
                Image(
                    modifier = Modifier.size(30.dp),
                    painter = painterResource(id = R.drawable.google_logo_colored),
                    contentDescription = "Apple Logo"
                )
            }
        }
        OutlinedButton(
            modifier = Modifier.padding(top = 32.dp),
            onClick = {},
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        ) {
            Text("Нет аккаунта? Зарегистрируйтесь")
        }
    }
}


@Preview
@Composable
fun LoginScreenPreview() {
    MemowaveTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp)
        ) {
            LoginScreen()
        }
    }
}