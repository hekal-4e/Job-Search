package com.example.jobsearchapp.ui.profile.Edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.jobsearchapp.Skill

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SkillScreen(
    skills: List<com.example.jobsearchapp.Skill>,
    onEditClick: () -> Unit,
    onBackClick: () -> Unit,
    onRemoveSkill: (Skill) -> Unit,
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
            IconButton(onClick = onBackClick) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Skills (${skills.size})",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.weight(1f)
            )
        }
        if (skills.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 16.dp)
            ) {
                skills.forEach { skill ->
                    SkillChipWithRemoveButton(
                        skill = skill.skillName,
                        onRemoveClick = { onRemoveSkill(skill) }
                    )
                }
            }
        } else {
            Text(
                text = "No skills added yet.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 16.dp)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = onEditClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Skills",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(text = "ADD SKILLS")
        }
    }
}

@Composable
fun SkillChipWithRemoveButton(
    skill: String,
    onRemoveClick: () -> Unit
) {
    InputChip(
        selected = true,
        onClick = { },
        label = { Text(text = skill) },
        trailingIcon = {
            IconButton(onClick = onRemoveClick, modifier = Modifier.size(24.dp)) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove",
                    modifier = Modifier.size(16.dp)
                )
            }
        },
        modifier = Modifier.padding(end = 4.dp, bottom = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = InputChipDefaults.inputChipColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            labelColor = MaterialTheme.colorScheme.onPrimaryContainer,
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

@Composable
fun AddSkillScreen(
    availableSkills: List<com.example.jobsearchapp.Skill>,
    userSkills: List<com.example.jobsearchapp.Skill>,
    onSkillAdded: (Skill) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    val userSkillNames = userSkills.map { it.skillName }
    val filteredSkills = remember(searchQuery, availableSkills, userSkills) {
        if (searchQuery.isEmpty()) {
            availableSkills.filter { it.skillName !in userSkillNames }
        } else {
            availableSkills.filter {
                it.skillName.contains(searchQuery, ignoreCase = true) &&
                        it.skillName !in userSkillNames
            }
        }
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
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Add Skills",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.weight(1f)
            )
        }
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search skills") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        )
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(top = 16.dp)
        ) {
            items(
                items = filteredSkills
            ) { skill ->
                SkillChipWithAddButton(skill = skill.skillName) { onSkillAdded(skill) }
            }
        }
    }
}

@Composable
fun SkillChipWithAddButton(
    skill: String,
    onSkillAdded: () -> Unit,
) {
    SuggestionChip(
        onClick = onSkillAdded,
        label = { Text(text = skill) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 4.dp, bottom = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = SuggestionChipDefaults.suggestionChipColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            labelColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        icon = {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    )
}
