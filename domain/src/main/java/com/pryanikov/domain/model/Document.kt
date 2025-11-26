package com.pryanikov.domain.model

import java.time.Instant     // Для точної мітки часу (коли)
import java.time.LocalDate   // Для дати (який день)

/**
 * Основна бізнес-сутність "Документ".
 * Ця модель є НЕЗМІННОЮ (immutable).
 *
 * @param id Унікальний ідентифікатор документа (UUID у вигляді String).
 * @param title Назва/Заголовок.
 * @param author Список ПІБ авторів.
 * @param type Тип документа ("Рукопис", "Договір").
 * @param status Поточний статус документа.
 * @param addedByUserId ID користувача (з User.id), який додав цей документ.
 *
 * --- Поля з датами ---
 * @param createdAt Дата й час створення цього *запису* в системі.
 * @param updatedAt Дата й час останнього оновлення цього *запису*.
 * @param documentDate Фактична дата самого документа (наприклад, дата отримання
 * рукопису). Може бути необов'язковою.
 * ---------------------
 *
 * @param sourceIds Список ID пов'язаних документів (наприклад, ID рукопису, до якого
 * відноситься ця обкладинка).
 * @param description Повний опис або анотація.
 * @param tags Список тегів для швидкого пошуку.
 * @param fileUrl Посилання на фактичний файл у хмарі.
 */
data class Document(
    val id: String,
    val title: String,
    val author: List<String>,
    val type: String,
    val status: Status,
    val addedByUserId: String,

    // --- Поля з датами ---
    val createdAt: Instant,
    val updatedAt: Instant,
    val documentDate: LocalDate? = null,
    // -------------------------

    val sourceIds: List<String> = emptyList(),
    val description: String? = null,
    val tags: List<String> = emptyList(),
    val fileUrl: String? = null
)