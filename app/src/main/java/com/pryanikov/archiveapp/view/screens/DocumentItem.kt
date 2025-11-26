package com.pryanikov.archiveapp.view.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pryanikov.domain.model.Document
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * Composable-функція для відображення одного елемента документа у списку.
 *
 * @param document Документ для відображення.
 * @param onClick Функція, що викликається при натисканні на елемент.
 */
@Composable
fun DocumentItem(
    document: Document,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick), // Робимо картку клікабельною
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Іконка зліва (згідно з вашим описом)
            Icon(
                imageVector = Icons.Default.Description,
                contentDescription = "Іконка документа",
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            // Колонка з текстом
            Column(
                modifier = Modifier.weight(1f) // Займає решту місця
            ) {
                // Назва (згідно з вашим описом)
                Text(
                    text = document.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis // "Назва..."
                )

                // Додаткова інформація (статус або дата)
                Text(
                    text = "Статус: ${document.status.name} | Оновлено: ${
                        document.updatedAt.atZone(
                            java.time.ZoneId.systemDefault()
                        ).format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))
                    }",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
