package com.pryanikov.domain.model

/**
 * Enum, що представляє всі можливі статуси документа.
 */
enum class Status {
    NEW,         // Новий (щойно отриманий)
    IN_REVIEW,   // На рецензії
    IN_PROGRESS, // В роботі (редактура, верстка, дизайн)
    APPROVED,    // Затверджено
    REJECTED,    // Відхилено
    ARCHIVED     // В архіві
}