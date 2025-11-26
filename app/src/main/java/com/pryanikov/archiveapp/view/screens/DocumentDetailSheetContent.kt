package com.pryanikov.archiveapp.view.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pryanikov.domain.model.Document // Переконайтеся, що імпорт правильний
import com.pryanikov.domain.model.Role // Переконайтеся, що імпорт правильний
import com.pryanikov.archiveapp.view.viewmodel.DocumentDetailViewModel
import com.pryanikov.archiveapp.view.viewmodel.MainViewModel
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * Вміст для ModalBottomSheet, що відображає деталі документа.
 *
 * @param mainViewModel Потрібен для доступу до ролі користувача
 * (щоб вирішити, чи показувати кнопки "Редагувати/Видалити")
 * та для запуску діалогу редагування.
 * @param detailViewModel ViewModel, що керує завантаженням/видаленням
 * цього конкретного документа.
 */
@Composable
fun DocumentDetailSheetContent(
    mainViewModel: MainViewModel,
    detailViewModel: DocumentDetailViewModel = hiltViewModel()
) {
    val detailState by detailViewModel.uiState.collectAsState()

    // Ми також стежимо за mainState, щоб знати роль користувача
    val mainState by mainViewModel.uiState.collectAsState()
    val currentUserRole = mainState.currentUser?.role

    // --- Логіка завантаження ---
    LaunchedEffect(key1 = mainState.selectedDocumentId) {
        detailViewModel.loadDocument(mainState.selectedDocumentId)
    }

    // --- Скидання стану ---
    DisposableEffect(key1 = Unit) {
        onDispose {
            detailViewModel.dismiss()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            detailState.isLoadingDocument -> {
                CircularProgressIndicator()
            }
            detailState.document != null -> {
                // Документ успішно завантажено
                val document = detailState.document!!
                DocumentDetailsView(
                    document = document,
                    userRole = currentUserRole,
                    onEditClick = {
                        // Повідомляємо MainViewModel,
                        // що потрібно закрити цей діалог і відкрити діалог редагування
                        mainViewModel.onEditDocumentClick()
                    },
                    onDeleteClick = {
                        // Повідомляємо detailViewModel,
                        // що потрібно відкрити *його* діалог (підтвердження пароля)
                        detailViewModel.onDeleteClick()
                    }
                )
            }
            else -> {
                val error = detailState.error
                if (error != null) {
                    Text(text = error, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }

    // --- Діалог підтвердження видалення (паролем) ---
    if (detailState.showConfirmDeleteDialog) {
        // --- ВИПРАВЛЕННЯ 2 ---
        // Копіюємо помилку (яка може з'явитися ПІД ЧАС валідації)
        // у локальну змінну
        val confirmError = detailState.error

        ConfirmDeleteDialog(
            isLoading = detailState.isDeleting,
            error = confirmError, // <-- Передаємо безпечну локальну змінну
            onConfirm = { password ->
                detailViewModel.onConfirmDelete(password)
            },
            onDismiss = {
                detailViewModel.onDismissDeleteDialog()
            }
        )
    }

    // --- Спостерігач за успішним видаленням ---
    LaunchedEffect(detailState.deleteSuccess) {
        if (detailState.deleteSuccess) {
            // Якщо видалення пройшло успішно,
            // кажемо MainViewModel закрити цей діалог
            mainViewModel.onDismissSheet()
        }
    }
}

/**
 * Внутрішня Composable-функція для відображення полів документа.
 */
@Composable
private fun DocumentDetailsView(
    document: Document,
    userRole: Role?,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            // Додаємо прокрутку, якщо деталей забагато
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Заголовок
        Text(
            text = document.title,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Divider()

        // --- Блок полів ---
        DetailRow(icon = Icons.Default.Person, title = "Автори", content = document.author.joinToString(", "))
        DetailRow(icon = Icons.Default.Category, title = "Тип", content = document.type)
        DetailRow(icon = Icons.Default.Info, title = "Статус", content = document.status.name)

        val docDate = document.documentDate?.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)) ?: "Не вказано"
        DetailRow(icon = Icons.Default.CalendarToday, title = "Дата документа", content = docDate)

        // Використовуємо той самий безпечний підхід для опису
        val description = document.description
        if (description != null && description.isNotBlank()) {
            DetailRow(icon = Icons.Default.Info, title = "Опис", content = description)
        }

        // ... (можна додати більше полів: теги, ким додано тощо) ...

        Divider()

        // --- Блок кнопок (для Адміна) ---
        if (userRole == Role.ADMIN) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Кнопка "Редагувати"
                Button(
                    onClick = onEditClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("РЕДАГУВATI")
                }

                // Кнопка "Видалити"
                Button(
                    onClick = onDeleteClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("ВИДАЛИТИ")
                }
            }
        }
    }
}

/**
 * Рядок для відображення "Іконка - Заголовок - Текст".
 */
@Composable
private fun DetailRow(icon: ImageVector, title: String, content: String) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary
        )
        Column {
            Text(text = title, style = MaterialTheme.typography.labelMedium)
            Text(text = content, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

/**
 * Діалог, що запитує пароль для підтвердження видалення.
 */
@Composable
private fun ConfirmDeleteDialog(
    isLoading: Boolean,
    error: String?, // Тепер це безпечно
    onConfirm: (password: String) -> Unit,
    onDismiss: () -> Unit
) {
    var password by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Підтвердьте видалення") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Для видалення документа введіть ваш пароль.")
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Пароль") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    isError = error != null // Використовуємо 'error'
                )
                if (error != null) {
                    Text(text = error, color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(password) },
                enabled = !isLoading && password.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text("ПІДТВЕРДИТИ")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isLoading) {
                Text("СКАСУВATI")
            }
        }
    )
}
