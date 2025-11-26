package com.pryanikov.archiveapp.view.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pryanikov.domain.model.Document
import com.pryanikov.domain.model.Status
import com.pryanikov.archiveapp.view.viewmodel.EditDocumentViewModel
import com.pryanikov.archiveapp.view.viewmodel.MainViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * Вміст для ModalBottomSheet, що відповідає за
 * створення або редагування документа.
 *
 * @param mainViewModel Потрібен, щоб отримати ID документа
 * для редагування та для закриття діалогу.
 * @param editViewModel ViewModel, що керує логікою
 * завантаження/збереження.
 */
@Composable
fun EditDocumentSheetContent(
    mainViewModel: MainViewModel,
    editViewModel: EditDocumentViewModel = hiltViewModel()
) {
    val editState by editViewModel.uiState.collectAsState()
    val mainState by mainViewModel.uiState.collectAsState()

    // --- Логіка завантаження ---
    // LaunchedEffect спрацьовує один раз, коли діалог відкривається.
    // Він передає ID документа (або null) у EditViewModel.
    LaunchedEffect(key1 = mainState.selectedDocumentId) {
        editViewModel.loadDocument(mainState.selectedDocumentId)
    }

    // --- Спостерігач за успішним збереженням ---
    LaunchedEffect(key1 = editState.saveSuccess) {
        if (editState.saveSuccess) {
            // Якщо збереження пройшло успішно,
            // кажемо MainViewModel закрити цей діалог.
            mainViewModel.onDismissSheet()
        }
    }

    // --- Скидання стану ---
    // Коли діалог закривається, ми "скидаємо"
    // стан EditViewModel.
    DisposableEffect(key1 = Unit) {
        onDispose {
            editViewModel.dismiss()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            editState.isLoading -> {
                CircularProgressIndicator()
            }
            editState.document != null -> {
                // Документ завантажено (або створено порожній)
                val document = editState.document!!
                EditDocumentForm( // <-- Ми оновлюємо тільки цю функцію
                    document = document,
                    isSaving = editState.isSaving,
                    error = editState.error,
                    onDocumentChange = { updatedDocument ->
                        editViewModel.onDocumentChanged(updatedDocument)
                    },
                    onSaveClick = {
                        editViewModel.onSaveClick()
                    }
                )
            }
            else -> {
                // Помилка (або документ == null)
                val error = editState.error
                if (error != null) {
                    Text(text = error, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

/**
 * Внутрішня Composable-функція для відображення самої форми.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditDocumentForm(
    document: Document,
    isSaving: Boolean,
    error: String?,
    onDocumentChange: (Document) -> Unit,
    onSaveClick: () -> Unit
) {
    // --- Стани для UI-елементів ---
    var showDatePicker by remember { mutableStateOf(false) }
    var showTypeDropdown by remember { mutableStateOf(false) }

    // Список доступних типів документів
    val documentTypes = listOf("Рукопис", "Договір", "Обкладинка", "Рецензія", "Маркетинг")

    // --- Логіка відображення помилок ---
    val localError = error // Безпечне копіювання
    val isTitleError = localError?.contains("Назва") == true
    val isAuthorError = localError?.contains("Автор") == true

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp) // Додатковий відступ
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (document.id.isBlank()) "Новий документ" else "Редагувати",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        // Поле "Назва"
        OutlinedTextField(
            value = document.title,
            onValueChange = { onDocumentChange(document.copy(title = it)) },
            label = { Text("Назва*") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = isTitleError // Підсвічування помилки
        )

        // Поле "Автори"
        OutlinedTextField(
            value = document.author.joinToString(", "),
            onValueChange = {
                val authors = it.split(",").map { str -> str.trim() }
                onDocumentChange(document.copy(author = authors))
            },
            label = { Text("Автори (через кому)*") },
            modifier = Modifier.fillMaxWidth(),
            isError = isAuthorError // Підсвічування помилки
        )

        // Поле "Теги"
        OutlinedTextField(
            value = document.tags.joinToString(", "),
            onValueChange = {
                val tags = it.split(",").map { str -> str.trim() }
                onDocumentChange(document.copy(tags = tags))
            },
            label = { Text("Теги (через кому)") },
            modifier = Modifier.fillMaxWidth()
        )

        // --- Вибір Типу (Dropdown) ---
        ExposedDropdownMenuBox(
            expanded = showTypeDropdown,
            onExpandedChange = { showTypeDropdown = !showTypeDropdown }
        ) {
            OutlinedTextField(
                value = document.type,
                onValueChange = {}, // Змінюється тільки через меню
                readOnly = true,
                label = { Text("Тип документа") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = showTypeDropdown)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor() // Прив'язка до меню
            )
            ExposedDropdownMenu(
                expanded = showTypeDropdown,
                onDismissRequest = { showTypeDropdown = false }
            ) {
                documentTypes.forEach { typeName ->
                    DropdownMenuItem(
                        text = { Text(typeName) },
                        onClick = {
                            onDocumentChange(document.copy(type = typeName))
                            showTypeDropdown = false
                        }
                    )
                }
            }
        }

        // --- Вибір Дати (DatePicker) ---
        OutlinedTextField(
            value = document.documentDate
                ?.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
                ?: "Не вказано",
            onValueChange = {},
            readOnly = true,
            label = { Text("Дата документа") },
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(Icons.Default.DateRange, "Обрати дату")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true }
        )

        // Поле "Опис"
        OutlinedTextField(
            value = document.description ?: "",
            onValueChange = { onDocumentChange(document.copy(description = it)) },
            label = { Text("Опис") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        // Кнопка "Зберегти"
        Button(
            onClick = onSaveClick,
            enabled = !isSaving,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isSaving) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text("ЗБЕРЕГТИ")
            }
        }

        // Помилка збереження (загальна)
        if(localError != null && !isTitleError && !isAuthorError) {
            Text(
                text = localError,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }

    // --- Логіка Діалогу DatePicker ---
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = document.documentDate
                ?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                        val selectedDate = datePickerState.selectedDateMillis?.let {
                            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                        }
                        onDocumentChange(document.copy(documentDate = selectedDate))
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Скасувати") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}