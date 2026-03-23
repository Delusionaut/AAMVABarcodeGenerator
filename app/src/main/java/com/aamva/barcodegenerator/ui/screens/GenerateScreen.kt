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
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Official Header Banner
            item {
                DMVOfficialHeader()
            }

            // Personal Information Section
            item { SectionHeader("PERSONAL INFORMATION") }
            item { 
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FormTextField(
                        value = familyName,
                        onValueChange = onFamilyNameChange,
                        label = "LAST NAME",
                        required = true,
                        modifier = Modifier.weight(1f),
                        hint = "Surname"
                    )
                    FormTextField(
                        value = firstName,
                        onValueChange = onFirstNameChange,
                        label = "FIRST NAME",
                        required = true,
                        modifier = Modifier.weight(1f),
                        hint = "Given name"
                    )
                }
            }
            item {
                FormTextField(
                    value = middleName,
                    onValueChange = onMiddleNameChange,
                    label = "MIDDLE NAME",
                    required = false,
                    hint = "Middle name or initial"
                )
            }

            // Dates Section
            item { SectionHeader("DATES") }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FormTextField(
                        value = dateOfBirth,
                        onValueChange = { onDateOfBirthChange(formatDateToMMDDYYYY(it)) },
                        label = "DATE OF BIRTH",
                        required = true,
                        modifier = Modifier.weight(1f),
                        hint = "MMDDYYYY",
                        keyboardType = KeyboardType.Number,
                        maxChars = 8
                    )
                    FormTextFieldWithAction(
                        value = dateOfIssue,
                        onValueChange = { onDateOfIssueChange(formatDateToMMDDYYYY(it)) },
                        label = "DATE OF ISSUE",
                        required = true,
                        onActionClick = onRandomIssueDate,
                        modifier = Modifier.weight(1f),
                        hint = "MMDDYYYY",
                        maxChars = 8
                    )
                }
            }
            item {
                FormTextFieldWithAction(
                    value = dateOfExpiry,
                    onValueChange = { onDateOfExpiryChange(formatDateToMMDDYYYY(it)) },
                    label = "DATE OF EXPIRY",
                    required = true,
                    onActionClick = onCalculateExpiry,
                    hint = "MMDDYYYY",
                    maxChars = 8
                )
            }

            // Identification Numbers Section
            item { SectionHeader("IDENTIFICATION NUMBERS") }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FormTextField(
                        value = iin,
                        onValueChange = { onIinChange(it.take(6)) },
                        label = "IIN",
                        required = true,
                        modifier = Modifier.weight(0.4f),
                        hint = "6 digits",
                        maxChars = 6
                    )
                    FormTextField(
                        value = customerId,
                        onValueChange = onCustomerIdChange,
                        label = "LICENSE/ID NUMBER",
                        required = true,
                        modifier = Modifier.weight(0.6f),
                        hint = "State ID number"
                    )
                }
            }
            item {
                FormTextFieldWithAction(
                    value = documentDiscriminator,
                    onValueChange = onDocumentDiscriminatorChange,
                    label = "DOCUMENT DISCRIMINATOR",
                    required = true,
                    onActionClick = onCalculateDocDisc,
                    hint = "Unique document ID"
                )
            }

            // Physical Characteristics Section
            item { SectionHeader("PHYSICAL CHARACTERISTICS") }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FormDropdownField(
                        value = sex,
                        onValueChange = { onSexChange(it.take(1)) },
                        label = "SEX",
                        required = true,
                        modifier = Modifier.weight(0.33f),
                        options = listOf("1" to "Male", "2" to "Female", "9" to "Unknown")
                    )
                    FormDropdownField(
                        value = eyeColor,
                        onValueChange = { onEyeColorChange(it.take(3).uppercase()) },
                        label = "EYE COLOR",
                        required = true,
                        modifier = Modifier.weight(0.33f),
                        options = listOf("BLK" to "Black", "BLU" to "Blue", "BRO" to "Brown", 
                                       "GRY" to "Gray", "GRN" to "Green", "HAZ" to "Hazel"),
                        maxChars = 3
                    )
                    FormTextField(
                        value = height,
                        onValueChange = onHeightChange,
                        label = "HEIGHT",
                        required = true,
                        modifier = Modifier.weight(0.34f),
                        hint = "XXX in"
                    )
                }
            }

            // Address Section
            item { SectionHeader("ADDRESS") }
            item {
                FormTextField(
                    value = addressStreet,
                    onValueChange = onAddressStreetChange,
                    label = "STREET ADDRESS",
                    required = true,
                    hint = "Number and street name"
                )
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FormTextField(
                        value = addressCity,
                        onValueChange = onAddressCityChange,
                        label = "CITY",
                        required = true,
                        modifier = Modifier.weight(1f),
                        hint = "City name"
                    )
                    FormTextField(
                        value = addressState,
                        onValueChange = { onAddressStateChange(it.take(2).uppercase()) },
                        label = "STATE",
                        required = true,
                        modifier = Modifier.weight(0.35f),
                        maxChars = 2,
                        hint = "XX"
                    )
                    FormTextField(
                        value = addressZip,
                        onValueChange = { onAddressZipChange(it.take(11)) },
                        label = "ZIP CODE",
                        required = true,
                        modifier = Modifier.weight(0.45f),
                        maxChars = 11,
                        hint = "12345"
                    )
                }
            }

            // Vehicle Information Section
            item { SectionHeader("VEHICLE INFORMATION") }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FormTextField(
                        value = vehicleClass,
                        onValueChange = onVehicleClassChange,
                        label = "VEHICLE CLASS",
                        required = false,
                        modifier = Modifier.weight(1f),
                        hint = "C, M, A, B, D"
                    )
                    FormTextField(
                        value = restrictions,
                        onValueChange = onRestrictionsChange,
                        label = "RESTRICTIONS",
                        required = false,
                        modifier = Modifier.weight(1f),
                        hint = "B, C, G, K, L"
                    )
                    FormTextField(
                        value = endorsements,
                        onValueChange = onEndorsementsChange,
                        label = "ENDORSEMENTS",
                        required = false,
                        modifier = Modifier.weight(1f),
                        hint = "H, N, P, S, T"
                    )
                }
            }
            
            // Bottom spacing for navigation
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun DMVOfficialHeader() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        color = GovernmentNavy,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "AAMVA BARCODE DATA FORM",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    letterSpacing = 1.sp
                ),
                color = OfficialWhite
            )
            Text(
                text = "Driver License / State ID Application",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp
                ),
                color = GovernmentGrayLight
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        color = GovernmentNavy.copy(alpha = 0.08f),
        shape = RoundedCornerShape(2.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                letterSpacing = 0.5.sp
            ),
            color = GovernmentNavy,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    required: Boolean,
    modifier: Modifier = Modifier,
    hint: String = "",
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
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
                if (required) {
                    Text(
                        text = " *",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = GovernmentRed
                    )
                }
            }
        },
        placeholder = {
            Text(
                text = hint,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 11.sp
                ),
                color = GovernmentGrayLight
            )
        },
        supportingText = if (required && value.isEmpty()) {
            @Composable {
                Text(
                    text = "Required",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 9.sp
                    ),
                    color = GovernmentRed
                )
            }
        } else null,
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        isError = required && value.isEmpty(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GovernmentNavy,
            unfocusedBorderColor = OfficialBorder,
            focusedLabelColor = GovernmentNavy,
            unfocusedLabelColor = GovernmentGray,
            cursorColor = GovernmentNavy,
            errorBorderColor = GovernmentRed,
            focusedContainerColor = OfficialWhite,
            unfocusedContainerColor = OfficialWhite,
            errorContainerColor = OfficialWhite
        ),
        shape = RoundedCornerShape(2.dp)
    )
}

@Composable
private fun FormTextFieldWithAction(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    required: Boolean,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier,
    hint: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    maxChars: Int? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.Top
    ) {
        FormTextField(
            value = value,
            onValueChange = onValueChange,
            label = label,
            required = required,
            modifier = Modifier.weight(1f),
            hint = hint,
            keyboardType = keyboardType,
            maxChars = maxChars
        )
        IconButton(
            onClick = onActionClick,
            modifier = Modifier
                .size(40.dp)
                .padding(top = 4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(GovernmentNavy.copy(alpha = 0.1f))
                .border(BorderStroke(1.dp, OfficialBorder), RoundedCornerShape(2.dp))
        ) {
            Icon(
                imageVector = Icons.Default.Calculate,
                contentDescription = "Auto-calculate",
                tint = GovernmentNavy,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormDropdownField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    required: Boolean,
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    if (required) {
                        Text(
                            text = " *",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = GovernmentRed
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth(),
            singleLine = true,
            isError = required && value.isEmpty(),
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
