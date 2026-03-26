package com.aamva.barcodegenerator.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aamva.barcodegenerator.ui.theme.GovernmentNavy
import com.aamva.barcodegenerator.ui.theme.GovernmentGray
import com.aamva.barcodegenerator.ui.theme.GovernmentGrayDark
import com.aamva.barcodegenerator.ui.theme.GovernmentGrayLight
import com.aamva.barcodegenerator.ui.theme.GovernmentGrayPale
import com.aamva.barcodegenerator.ui.theme.OfficialWhite
import com.aamva.barcodegenerator.ui.theme.OfficialLight
import com.aamva.barcodegenerator.ui.theme.OfficialBorder
import com.aamva.barcodegenerator.ui.theme.GovernmentRed
import com.aamva.barcodegenerator.ui.theme.GovernmentGreen
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// Legacy tab enum for backwards compatibility with MainScreen
enum class FormTab {
    Personal,
    Dates,
    IdNumbers,
    Physical,
    Address,
    Vehicle
}

// Section definitions for unified layout
enum class FormSection(val title: String) {
    PERSONAL("Personal Information"),
    DATES("Dates"),
    IDENTIFICATION("Identification Numbers"),
    PHYSICAL("Physical Characteristics"),
    ADDRESS("Address"),
    VEHICLE("Vehicle Information")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateScreen(
    currentFormTab: FormTab,
    onFormTabChange: (FormTab) -> Unit,
    iin: String,
    onIinChange: (String) -> Unit,
    familyName: String,
    onFamilyNameChange: (String) -> Unit,
    firstName: String,
    onFirstNameChange: (String) -> Unit,
    middleName: String,
    onMiddleNameChange: (String) -> Unit,
    dateOfBirth: String,
    onDateOfBirthChange: (String) -> Unit,
    dateOfIssue: String,
    onDateOfIssueChange: (String) -> Unit,
    onRandomIssueDate: () -> Unit,
    dateOfExpiry: String,
    onDateOfExpiryChange: (String) -> Unit,
    onCalculateExpiry: () -> Unit,
    customerId: String,
    onCustomerIdChange: (String) -> Unit,
    documentDiscriminator: String,
    onDocumentDiscriminatorChange: (String) -> Unit,
    onCalculateDocDisc: () -> Unit,
    sex: String,
    onSexChange: (String) -> Unit,
    eyeColor: String,
    onEyeColorChange: (String) -> Unit,
    height: String,
    onHeightChange: (String) -> Unit,
    addressStreet: String,
    onAddressStreetChange: (String) -> Unit,
    addressCity: String,
    onAddressCityChange: (String) -> Unit,
    addressState: String,
    onAddressStateChange: (String) -> Unit,
    addressZip: String,
    onAddressZipChange: (String) -> Unit,
    vehicleClass: String,
    onVehicleClassChange: (String) -> Unit,
    restrictions: String,
    onRestrictionsChange: (String) -> Unit,
    endorsements: String,
    onEndorsementsChange: (String) -> Unit
) {
    val listState = rememberLazyListState()
    
    // DMV Form Container
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = OfficialLight
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .imePadding(),
            contentPadding = PaddingValues(4.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            // Official Header Banner
            item {
                DMVOfficialHeader()
            }

            // Personal Information Section
            item { CompactSectionHeader("1. PERSONAL INFORMATION") }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    CompactTextField(
                        value = familyName,
                        onValueChange = onFamilyNameChange,
                        label = "LAST NAME *",
                        modifier = Modifier.weight(1f)
                    )
                    CompactTextField(
                        value = firstName,
                        onValueChange = onFirstNameChange,
                        label = "FIRST NAME *",
                        modifier = Modifier.weight(1f)
                    )
                    CompactTextField(
                        value = middleName,
                        onValueChange = onMiddleNameChange,
                        label = "MIDDLE NAME",
                        modifier = Modifier.weight(0.8f)
                    )
                }
            }

            // Dates Section
            item { CompactSectionHeader("2. DATES") }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CompactTextField(
                        value = dateOfBirth,
                        onValueChange = { onDateOfBirthChange(formatDateToMMDDYYYY(it)) },
                        label = "DATE OF BIRTH *",
                        modifier = Modifier.weight(1f),
                        keyboardType = KeyboardType.Number,
                        maxChars = 8
                    )
                    CompactTextField(
                        value = dateOfIssue,
                        onValueChange = { onDateOfIssueChange(formatDateToMMDDYYYY(it)) },
                        label = "DATE OF ISSUE *",
                        modifier = Modifier.weight(1f),
                        keyboardType = KeyboardType.Number,
                        maxChars = 8
                    )
                    IconButton(
                        onClick = onRandomIssueDate,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = "Random Issue Date",
                            tint = GovernmentNavy,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    CompactTextField(
                        value = dateOfExpiry,
                        onValueChange = { onDateOfExpiryChange(formatDateToMMDDYYYY(it)) },
                        label = "DATE OF EXPIRY *",
                        modifier = Modifier.weight(1f),
                        keyboardType = KeyboardType.Number,
                        maxChars = 8
                    )
                    IconButton(
                        onClick = onCalculateExpiry,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = "Calculate Expiry",
                            tint = GovernmentNavy,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Identification Numbers Section
            item { CompactSectionHeader("3. IDENTIFICATION NUMBERS") }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CompactTextField(
                        value = iin,
                        onValueChange = { onIinChange(it.take(6)) },
                        label = "IIN *",
                        modifier = Modifier.weight(0.3f),
                        maxChars = 6
                    )
                    CompactTextField(
                        value = customerId,
                        onValueChange = onCustomerIdChange,
                        label = "LICENSE/ID NUMBER *",
                        modifier = Modifier.weight(0.5f)
                    )
                    CompactTextField(
                        value = documentDiscriminator,
                        onValueChange = onDocumentDiscriminatorChange,
                        label = "DOC DISC *",
                        modifier = Modifier.weight(0.5f)
                    )
                    IconButton(
                        onClick = onCalculateDocDisc,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = "Calculate Doc Disc",
                            tint = GovernmentNavy,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Physical Characteristics Section
            item { CompactSectionHeader("4. PHYSICAL CHARACTERISTICS") }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    CompactTextField(
                        value = sex,
                        onValueChange = { onSexChange(it.take(1)) },
                        label = "SEX *",
                        modifier = Modifier.weight(0.25f),
                        maxChars = 1
                    )
                    CompactTextField(
                        value = eyeColor,
                        onValueChange = { onEyeColorChange(it.take(3).uppercase()) },
                        label = "EYE COLOR *",
                        modifier = Modifier.weight(0.35f),
                        maxChars = 3
                    )
                    CompactTextField(
                        value = height,
                        onValueChange = onHeightChange,
                        label = "HEIGHT *",
                        modifier = Modifier.weight(0.4f)
                    )
                }
            }

            // Address Section
            item { CompactSectionHeader("5. ADDRESS") }
            item {
                CompactTextField(
                    value = addressStreet,
                    onValueChange = onAddressStreetChange,
                    label = "STREET ADDRESS *"
                )
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    CompactTextField(
                        value = addressCity,
                        onValueChange = onAddressCityChange,
                        label = "CITY *",
                        modifier = Modifier.weight(1f)
                    )
                    CompactTextField(
                        value = addressState,
                        onValueChange = { onAddressStateChange(it.take(2).uppercase()) },
                        label = "STATE *",
                        modifier = Modifier.weight(0.25f),
                        maxChars = 2
                    )
                    CompactTextField(
                        value = addressZip,
                        onValueChange = { onAddressZipChange(it.take(11)) },
                        label = "ZIP *",
                        modifier = Modifier.weight(0.35f),
                        maxChars = 11
                    )
                }
            }

            // Vehicle Information Section
            item { CompactSectionHeader("6. VEHICLE INFORMATION") }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    CompactTextField(
                        value = vehicleClass,
                        onValueChange = onVehicleClassChange,
                        label = "VEH CLASS",
                        modifier = Modifier.weight(1f)
                    )
                    CompactTextField(
                        value = restrictions,
                        onValueChange = onRestrictionsChange,
                        label = "RESTRICTIONS",
                        modifier = Modifier.weight(1f)
                    )
                    CompactTextField(
                        value = endorsements,
                        onValueChange = onEndorsementsChange,
                        label = "ENDORSEMENTS",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            // Bottom spacing for navigation
            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}

@Composable
private fun DMVOfficialHeader() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = GovernmentNavy
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "AAMVA BARCODE DATA FORM",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    letterSpacing = 0.5.sp
                ),
                color = OfficialWhite
            )
            Text(
                text = "Driver License / State ID Application",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 10.sp,
                    letterSpacing = 0.sp
                ),
                color = GovernmentGrayLight
            )
        }
    }
}

@Composable
private fun CompactSectionHeader(title: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        color = GovernmentNavy.copy(alpha = 0.1f),
        shape = RoundedCornerShape(1.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium.copy(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                letterSpacing = 0.sp
            ),
            color = GovernmentNavy,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun CompactTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    maxChars: Int? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = { newValue: String ->
            val limitedValue: String = if (maxChars != null && newValue.length > maxChars) {
                newValue.substring(0, maxChars)
            } else {
                newValue
            }
            onValueChange(limitedValue)
        },
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        },
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GovernmentNavy,
            unfocusedBorderColor = OfficialBorder,
            focusedLabelColor = GovernmentNavy,
            unfocusedLabelColor = GovernmentGray,
            cursorColor = GovernmentNavy,
            focusedContainerColor = OfficialWhite,
            unfocusedContainerColor = OfficialWhite
        ),
        shape = RoundedCornerShape(1.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompactDropdownField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    options: List<Pair<String, String>> = emptyList(),
    maxChars: Int? = null
) {
    var expanded by remember { mutableStateOf(false) }
    
    Box(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = { newValue: String ->
                val limitedValue: String = if (maxChars != null && newValue.length > maxChars) {
                    newValue.substring(0, maxChars)
                } else {
                    newValue
                }
                onValueChange(limitedValue)
            },
            label = {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GovernmentNavy,
                unfocusedBorderColor = OfficialBorder,
                focusedLabelColor = GovernmentNavy,
                unfocusedLabelColor = GovernmentGray,
                cursorColor = GovernmentNavy,
                errorBorderColor = GovernmentRed,
                focusedContainerColor = OfficialWhite,
                unfocusedContainerColor = OfficialWhite
            ),
            shape = RoundedCornerShape(2.dp),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Select",
                    tint = GovernmentGray,
                    modifier = Modifier
                        .size(16.dp)
                        .clickable { expanded = true }
                )
            },
            readOnly = true
        )
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(OfficialWhite)
        ) {
            options.forEach { (code: String, description: String) ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "$code - $description",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 12.sp
                            ),
                            color = GovernmentGrayDark
                        )
                    },
                    onClick = {
                        onValueChange(code)
                        expanded = false
                    }
                )
            }
        }
    }
}

private fun formatDateToMMDDYYYY(input: String): String {
    val digits = input.filter { it.isDigit() }
    return when {
        digits.length >= 8 -> digits.take(8)
        digits.length >= 4 -> "${digits.take(4)}${digits.drop(4).take(4).padEnd(4, '0')}"
        digits.isNotEmpty() -> digits.padEnd(8, '0')
        else -> ""
    }
}

private fun calculateDocumentDiscriminator(issueDate: String, expiryDate: String): String {
    val issueDigits = issueDate.filter { it.isDigit() }.take(8).padEnd(8, '0')
    val mm = issueDigits.substring(0, 2)
    val dd = issueDigits.substring(2, 4)
    val yyyy = issueDigits.substring(4, 8)
    val random5Digits = (10000..99999).random().toString()
    val expiryDigits = expiryDate.filter { it.isDigit() }
    val expiryYear = if (expiryDigits.length >= 4) expiryDigits.takeLast(2) else "00"
    return "$mm/$dd/$yyyy$random5Digits/AAFD/$expiryYear"
}

private fun calculateExpiryDate(dob: String, issue: String): String {
    if (dob.length != 8 || issue.length != 8) return ""
    val mmdd = dob.substring(0, 4)
    val issueYear = issue.substring(4, 8).toIntOrNull() ?: return ""
    val expiryYear = issueYear + 5
    val expiryYearStr = expiryYear.toString().padStart(4, '0')
    return mmdd + expiryYearStr
}

private fun generateRandomIssueDate(): String {
    val calendar = Calendar.getInstance()
    val currentYear = calendar.get(Calendar.YEAR)
    val startYear = currentYear - 5
    val year = (startYear until currentYear).random()
    val month = (1..12).random()
    val day = (1..28).random()
    calendar.set(year, month - 1, day)
    return SimpleDateFormat("MMddyyyy", Locale.US).format(calendar.time)
}
