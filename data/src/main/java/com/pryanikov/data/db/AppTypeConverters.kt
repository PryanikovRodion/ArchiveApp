package com.pryanikov.data.db

import androidx.room.TypeConverter
import com.pryanikov.domain.model.Role
import com.pryanikov.domain.model.Status
import java.time.Instant
import java.time.LocalDate

class AppTypeConverters {


    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return value?.joinToString(separator = "||") ?: ""
    }

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        return value?.split("||")?.filter { it.isNotEmpty() } ?: emptyList()
    }


    @TypeConverter
    fun fromRole(role: Role): String = role.name

    @TypeConverter
    fun toRole(value: String): Role {

        return runCatching { Role.valueOf(value) }
            .getOrDefault(Role.UNKNOWN)
    }


    @TypeConverter
    fun fromStatus(status: Status): String = status.name

    @TypeConverter
    fun toStatus(value: String): Status {
        // Если статус не распознан, считаем документ NEW
        return runCatching { Status.valueOf(value) }
            .getOrDefault(Status.NEW)
    }


    @TypeConverter
    fun fromInstant(value: Instant?): Long? = value?.toEpochMilli()

    @TypeConverter
    fun toInstant(value: Long?): Instant? = value?.let { Instant.ofEpochMilli(it) }


    @TypeConverter
    fun fromLocalDate(value: LocalDate?): Long? = value?.toEpochDay()

    @TypeConverter
    fun toLocalDate(value: Long?): LocalDate? = value?.let { LocalDate.ofEpochDay(it) }
}