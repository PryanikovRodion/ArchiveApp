package com.pryanikov.archiveapp.view.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pryanikov.archiveapp.view.viewmodel.LoginViewModel

/**
 * Вміст для ModalBottomSheet, що відповідає за вхід до системи.
 * Цей Composable "дурний" - він просто отримує стан
 * від LoginViewModel та викликає його функції.
 */
@Composable
fun LoginSheetContent(
    // Ми отримуємо ViewModel, прив'язаний до Hilt
    viewModel: LoginViewModel = hiltViewModel()
) {
    // Підписуємося на стан ViewModel
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .padding(bottom = 32.dp), // Додатковий відступ знизу для зручності
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Вхід до Архіву",
            style = MaterialTheme.typography.headlineSmall
        )

        // Поле для Email
        OutlinedTextField(
            value = state.email,
            onValueChange = { viewModel.onEmailChanged(it) },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = state.error != null,
            singleLine = true
        )

        // Поле для Пароля
        OutlinedTextField(
            value = state.pass,
            onValueChange = { viewModel.onPasswordChanged(it) },
            label = { Text("Пароль") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = state.error != null,
            singleLine = true
        )

        // Або кнопка, або індикатор завантаження
        if (state.isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = { viewModel.onLoginClick() },
                modifier = Modifier.fillMaxWidth(),
                // Кнопка неактивна, якщо поля порожні
                enabled = state.email.isNotBlank() && state.pass.isNotBlank()
            ) {
                Text("УВІЙТИ")
            }
        }

        // Показуємо повідомлення про помилку
        val errorMessage = state.error
        if (errorMessage != null) {
            // --- ВИПРАВЛЕННЯ ---
            // 1. Копіюємо state.error у локальну змінну
            //val errorMessage = state.error!!

            Text(
                text = errorMessage, // 2. Використовуємо локальну змінну
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
            // --- КІНЕЦЬ ВИПРАВЛЕННЯ ---
        }
    }
}