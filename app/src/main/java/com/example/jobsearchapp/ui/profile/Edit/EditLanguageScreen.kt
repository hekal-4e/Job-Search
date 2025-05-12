package com.example.jobsearchapp.ui.Edit



import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.jobsearchapp.Language
import com.example.jobsearchapp.ui.profile.Edit.Chip

// REMOVE THIS DATA CLASS DEFINITION
// data class Language(
//     val id: Long,
//     val userId: Long,
//     val languageName: String,
//     val languageLevel: String, // format: "oralLevel,writtenLevel"
//     val isFirstLanguage: Boolean
// )

@Composable
fun LanguageScreen(
    languages: List<Language>,
    onAddLanguageClick: () -> Unit,
    onEditLanguageClick: (Language) -> Unit,
    onDeleteLanguage: (Language) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* Handle back */ }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Text(
                text = "Languages",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = onAddLanguageClick,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(text = "Add +")
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(top = 16.dp)
        ) {
            items(languages) { language ->
                LanguageItem(
                    language = language,
                    onEditClick = onEditLanguageClick,
                    onDeleteClick = { onDeleteLanguage(language) }
                )
            }
        }

        Button(
            onClick = onSaveClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(text = "SAVE")
        }
    }
}

@Composable
fun LanguageItem(
    language: Language,
    onEditClick: (Language) -> Unit,
    onDeleteClick: (Language) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .clickable { onEditClick(language) }
        ) {
            // Display language name and proficiency
            Text(text = "${language.languageName}")
            Text(text = "Oral Level: ${language.languageLevel.split(",")[0]}")
            Text(text = "Written Level: ${language.languageLevel.split(",")[1]}")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { onDeleteClick(language) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}

@Composable
fun AddLanguageScreen(
    availableLanguages: List<String>,
    onLanguageSelected: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredLanguages = remember(searchQuery) {
        availableLanguages.filter { it.contains(searchQuery, ignoreCase = true) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Add Language",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
        }

        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search language") },
            modifier = Modifier.fillMaxWidth()
        )

        LazyColumn(modifier = Modifier.fillMaxHeight()) {
            items(filteredLanguages) { language ->
                LanguageChipWithAddButton(
                    language = language,
                    onLanguageSelected = onLanguageSelected
                )
            }
        }
    }
}

@Composable
fun LanguageChipWithAddButton(
    language: String,
    onLanguageSelected: (String) -> Unit
) {
    Chip(
        onClick = { onLanguageSelected(language) },
        modifier = Modifier.padding(end = 4.dp, bottom = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Language,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = language, color = Color.Black)
        }
    }
}

@Composable
fun EditLanguageScreen(
    language: Language, // Updated to use the main Language class
    onBackClick: () -> Unit,
    onSaveClick: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Parse the initial values from language.languageLevel
    val levels = language.languageLevel.split(",")
    var oralLevel by remember {
        mutableStateOf(levels.getOrNull(0)?.toIntOrNull() ?: 0)
    }
    var writtenLevel by remember {
        mutableStateOf(levels.getOrNull(1)?.toIntOrNull() ?: 0)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = language.languageName,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Oral Proficiency")
        Slider(
            value = oralLevel.toFloat(),
            onValueChange = { oralLevel = it.toInt() },
            valueRange = 0f..10f,
            steps = 9
        )
        Text(text = "Level: $oralLevel")

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Written Proficiency")
        Slider(
            value = writtenLevel.toFloat(),
            onValueChange = { writtenLevel = it.toInt() },
            valueRange = 0f..10f,
            steps = 9
        )
        Text(text = "Level: $writtenLevel")

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Proficiency level: 0 ~ Poor, 10 ~ Very good",
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                onSaveClick(oralLevel, writtenLevel)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "SAVE")
        }
    }
}