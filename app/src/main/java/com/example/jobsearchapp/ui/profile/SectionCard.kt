package com.example.jobsearchapp.ui.profile

import OrangeTheme.Orange
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.jobsearchapp.Education
import com.example.jobsearchapp.WorkExperience

@Composable
fun SectionCard(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit,
    onEditClick: () -> Unit,
    onAddClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Orange,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(
                    modifier = Modifier.weight(1f))
                if (onAddClick != null) {
                    IconButton(
                        onClick = onAddClick,
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.CenterVertically)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            tint = Orange
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.CenterVertically)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Orange
                    )
                }
            }
            Divider(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                thickness = 1.dp,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))
            content()
        }
    }
}

@Composable
fun AboutMeSection(
    aboutMe: String?,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    SectionCard(
        title = "About me",
        icon = Icons.Default.Person,
        onEditClick = onEditClick,
        modifier = modifier,
        content = {
            if (aboutMe != null && aboutMe.isNotEmpty()) {
                Text(
                    text = aboutMe,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            } else {
                Text(
                    text = "Add your bio here...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    )
}

@Composable
fun WorkExperienceSection(
    workExperiences: List<WorkExperience>,
    onEditClick: (Any?) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    SectionCard(
        title = "Work Experience",
        icon = Icons.Default.Work,
        onEditClick = { onEditClick(Any()) },
        onAddClick = onAddClick,
        modifier = modifier,
        content = {
            if (workExperiences.isNotEmpty()) {
                workExperiences.forEach { experience ->
                    WorkExperienceItem(experience)
                }
            } else {
                Text(
                    text = "No work experience added yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    )
}

@Composable
fun WorkExperienceItem(experience: WorkExperience) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = experience.position,
            style = MaterialTheme.typography.titleSmall
        )
        Text(
            text = "${experience.company}, ${experience.startDate} - ${experience.endDate}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun EducationSection(
    educations: List<com.example.jobsearchapp.Education>,
    onEditClick: (Int) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
    navController: NavController
) {
    SectionCard(
        title = "Education",
        icon = Icons.Default.School,
        onEditClick = {
            if (educations.isNotEmpty()) {
                navController.navigate("edit_education/${educations[0].id}")
            }
        },
        onAddClick = {
            navController.navigate("add_education")
        },
        modifier = modifier,
        content = {
            if (educations.isNotEmpty()) {
                educations.forEachIndexed { index, edu ->
                    EducationItem(edu)
                    Spacer(modifier = Modifier.height(4.dp))
                }
            } else {
                Text(
                    text = "No education added yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    )
}

@Composable
fun EducationItem(education: Education) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = education.degree,
            style = MaterialTheme.typography.titleSmall
        )
        Text(
            text = "${education.institution}, ${education.degree} - ${education.graduationDate}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SkillsSection(
    skills: List<String>,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    SectionCard(
        title = "Skills (${skills.size})",  // Show count of skills
        icon = Icons.Default.Star,
        onEditClick = onEditClick,
        modifier = modifier,
        content = {
            if (skills.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    skills.forEach { skill ->
                        SkillChip(
                            skill = skill,
                        )
                    }
                }
            } else {
                Text(
                    text = "No skills added yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    )
}

@Composable
fun SkillChip(skill: String) {
    SuggestionChip(
        onClick = { },
        label = { Text(text = skill) },
        modifier = Modifier.padding(end = 4.dp, bottom = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = SuggestionChipDefaults.suggestionChipColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            labelColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

@Composable
fun LanguageChip(
    language: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = false,
        onClick = onClick,
        label = { Text(text = language) },
        modifier = Modifier.padding(end = 4.dp, bottom = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            labelColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LanguagesSection(
    languages: List<String>,
    onAddClick: () -> Unit,
    onEditClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    SectionCard(
        title = "Language",
        icon = Icons.Default.Language,
        onEditClick = { onAddClick() },
        modifier = modifier,
        content = {
            if (languages.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    languages.forEach { language ->
                        LanguageChip(
                            language = language,
                            onClick = { onEditClick(language) }  // Pass language name when clicked
                        )
                    }
                }
            } else {
                Text(
                    text = "No languages added yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    )
}

@Composable
fun AppreciationSection(
    appreciations: List<String>,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    SectionCard(
        title = "Appreciation",
        icon = Icons.Default.ThumbUp,
        onEditClick = onEditClick,
        modifier = modifier,
        content = {
            if (appreciations.isNotEmpty()) {
                appreciations.forEach { appreciation ->
                    Text(
                        text = appreciation,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            } else {
                Text(
                    text = "No appreciations yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    )
}
