package com.pryanikov.domain.model

/**
 * Бізнес-сутність "Користувач".
 * Описує аутентифікованого користувача системи.
 *
 * @param id Унікальний ідентифікатор (наприклад, з Firebase Auth або БД).
 * @param email Email користувача, що використовується для входу.
 * @param name Відображуване ім'я (може бути необов'язковим).
 * @param role Роль користувача в системі (ADMIN, EDITOR, READER).
 */
data class User(
    val id: String,
    val email: String,
    val name: String? = null, // Ім'я робимо необов'язковим (nullable)
    val role: Role
)