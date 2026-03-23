package com.aamva.barcodegenerator.ui.screens

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class FormTab {
    Personal,
    Dates,
    IdNumbers,
    Physical,
    Address,
    Vehicle
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
    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = currentFormTab.ordinal
        ) {
            FormTab.values().forEachIndexed { index, tab ->
                Tab(
                    selected = tab == currentFormTab,
                    onClick = { onFormTabChange(tab) },
                    text = { Text(tab.name.replace("_", " ")) }
                )
            }
        }
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (currentFormTab) {
                FormTab.Personal -> {
                    item {
                        TooltipTextField(
                            value = familyName,
                            onValueChange = onFamilyNameChange,
                            label = "Family Name",
                            tooltipTitle = "Last Name / Surname",
                            tooltipContent = "The license holder's family name (last name). This field is required and must match the name on the physical ID card. Maximum 50 characters. Use uppercase letters as per AAMVA standard.",
                            isRequired = true
                        )
                        TooltipTextField(
                            value = firstName,
                            onValueChange = onFirstNameChange,
                            label = "First Name",
                            tooltipTitle = "First Name / Given Name",
                            tooltipContent = "The license holder's first (given) name. This field is required. Maximum 50 characters. Use uppercase letters as per AAMVA standard.",
                            isRequired = true
                        )
                        TooltipTextField(
                            value = middleName,
                            onValueChange = onMiddleNameChange,
                            label = "Middle Name",
                            tooltipTitle = "Middle Name",
                            tooltipContent = "The license holder's middle name or middle initial. This is an optional field. If provided, use uppercase letters. Maximum 50 characters.",
                            isRequired = false
                        )
                    }
                }
                FormTab.Dates -> {
                    item {
                        TooltipTextField(
                            value = dateOfBirth,
                            onValueChange = { onDateOfBirthChange(formatDateToMMDDYYYY(it)) },
                            label = "Date of Birth",
                            tooltipTitle = "Date of Birth (DBA)",
                            tooltipContent = "The license holder's date of birth in MMDDYYYY format. This is used to verify identity and calculate age. The barcode stores the exact date. Example: 01152001 means January 15, 2001.",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            maxChars = 8,
                            isRequired = true
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TooltipTextField(
                                value = dateOfIssue,
                                onValueChange = { onDateOfIssueChange(formatDateToMMDDYYYY(it)) },
                                label = "Date of Issue",
                                tooltipTitle = "Date of Issue (DOI)",
                                tooltipContent = "The date when the license/ID was issued. Format: MMDDYYYY. This date is used to calculate the expiry date and for the Document Discriminator. Click the calculator icon to generate a random valid date.",
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                maxChars = 8,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = onRandomIssueDate) {
                                Icon(
                                    imageVector = Icons.Default.Calculate,
                                    contentDescription = "Generate random issue date"
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TooltipTextField(
                                value = dateOfExpiry,
                                onValueChange = { onDateOfExpiryChange(formatDateToMMDDYYYY(it)) },
                                label = "Date of Expiry",
                                tooltipTitle = "Date of Expiry (DOE)",
                                tooltipContent = "The expiration date of the license/ID. Format: MMDDYYYY. Most licenses are valid for 5 years from issue date. Click the calculator icon to auto-calculate: it uses the month/day from Date of Birth and adds 5 years to the issue year.",
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                maxChars = 8,
                                isRequired = true,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = onCalculateExpiry) {
                                Icon(
                                    imageVector = Icons.Default.Calculate,
                                    contentDescription = "Calculate expiry date from DOB and DOI"
                                )
                            }
                        }
                    }
                }
                FormTab.IdNumbers -> {
                    item {
                        TooltipTextField(
                            value = iin,
                            onValueChange = { onIinChange(it.take(6)) },
                            label = "IIN",
                            tooltipTitle = "Issuer Identification Number (IIN)",
                            tooltipContent = "A 6-digit number that identifies the issuing authority. This is assigned by AAMVA. The default value 636000 is the AAMVA reserved prefix used for testing. In production, use your assigned IIN.",
                            maxChars = 6,
                            isRequired = true
                        )
                        TooltipTextField(
                            value = customerId,
                            onValueChange = onCustomerIdChange,
                            label = "Customer ID",
                            tooltipTitle = "Customer Identifier (CID)",
                            tooltipContent = "The unique ID number assigned by the issuing authority. This is typically the driver's license number or state ID number. Usually 8-12 characters. This field is required and must be unique per holder.",
                            isRequired = true
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TooltipTextField(
                                value = documentDiscriminator,
                                onValueChange = onDocumentDiscriminatorChange,
                                label = "Doc Discriminator",
                                tooltipTitle = "Document Discriminator (DC)",
                                tooltipContent = "A code that uniquely identifies the specific document, even if the customer has multiple IDs. Generated by the issuing authority. Format: MM/DD/YYYY#####/AAFD/YY (last 2 digits of expiry year). Example: 08/25/202165508/AAFD/26. Click the calculator icon to auto-generate.",
                                modifier = Modifier.weight(1f),
                                isRequired = true
                            )
                            IconButton(onClick = onCalculateDocDisc) {
                                Icon(
                                    imageVector = Icons.Default.Calculate,
                                    contentDescription = "Calculate document discriminator"
                                )
                            }
                        }
                    }
                }
                FormTab.Physical -> {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TooltipTextField(
                                value = sex,
                                onValueChange = { onSexChange(it.take(1)) },
                                label = "Sex",
                                tooltipTitle = "Sex Code (S)",
                                tooltipContent = "Biological sex identifier. Valid codes: 1 = Male, 2 = Female, 9 = Unknown/Other. This is a single character field. The code is used for identification purposes.",
                                maxChars = 1,
                                modifier = Modifier.weight(1f),
                                isRequired = true
                            )
                            TooltipTextField(
                                value = eyeColor,
                                onValueChange = { onEyeColorChange(it.take(3).uppercase()) },
                                label = "Eye Color",
                                tooltipTitle = "Eye Color Code (EC)",
                                tooltipContent = "Three-letter code for eye color. Valid codes: BLU (Blue), BRO (Brown), GRN (Green), GRY (Gray), HAZ (Hazel), MAR (Maroon), PNK (Pink). Used for physical identification.",
                                maxChars = 3,
                                modifier = Modifier.weight(1f),
                                isRequired = true
                            )
                            TooltipTextField(
                                value = height,
                                onValueChange = onHeightChange,
                                label = "Height",
                                tooltipTitle = "Height (H)",
                                tooltipContent = "Height in inches, formatted as 'XXX in' (3 digits followed by space and 'in'). Example: 070 in = 70 inches (5'10\"). Used for physical identification.",
                                modifier = Modifier.weight(1f),
                                isRequired = true
                            )
                        }
                    }
                }
                FormTab.Address -> {
                    item {
                        TooltipTextField(
                            value = addressStreet,
                            onValueChange = onAddressStreetChange,
                            label = "Street Address",
                            tooltipTitle = "Street Address (AS1)",
                            tooltipContent = "The street address of the license holder's residence. Include apartment number if applicable. Use uppercase letters. Example: 123 MAIN STREET APT 4B.",
                            isRequired = true
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TooltipTextField(
                                value = addressCity,
                                onValueChange = onAddressCityChange,
                                label = "City",
                                tooltipTitle = "City (CTY)",
                                tooltipContent = "The city of the license holder's residence. Use uppercase letters. Example: SPRINGFIELD.",
                                modifier = Modifier.weight(1f),
                                isRequired = true
                            )
                            TooltipTextField(
                                value = addressState,
                                onValueChange = { onAddressStateChange(it.take(2).uppercase()) },
                                label = "State",
                                tooltipTitle = "Jurisdiction Code (JC)",
                                tooltipContent = "Two-letter US state or territory code. Example: CA, NY, TX, FL. Auto-converts to uppercase. This identifies the issuing state.",
                                maxChars = 2,
                                modifier = Modifier.weight(0.5f),
                                isRequired = true
                            )
                            TooltipTextField(
                                value = addressZip,
                                onValueChange = { onAddressZipChange(it.take(11)) },
                                label = "ZIP Code",
                                tooltipTitle = "Postal Code (PC)",
                                tooltipContent = "ZIP code (5-digit) or ZIP+4 (9-digit). Format: 12345 or 12345-6789. Maximum 11 characters. Used for mailing and identification.",
                                maxChars = 11,
                                modifier = Modifier.weight(1f),
                                isRequired = true
                            )
                        }
                    }
                }
                FormTab.Vehicle -> {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TooltipTextField(
                                value = vehicleClass,
                                onValueChange = onVehicleClassChange,
                                label = "Vehicle Class",
                                tooltipTitle = "Vehicle Class (VC)",
                                tooltipContent = "Class of vehicle the driver is authorized to drive. Common classes: C (Standard automobile), M (Motorcycle), A (Truck over 26,000 lbs), B (Truck 10,000-26,000 lbs), D (Standard with trailer). Leave blank for ID cards.",
                                modifier = Modifier.weight(1f),
                                isRequired = false
                            )
                            TooltipTextField(
                                value = restrictions,
                                onValueChange = onRestrictionsChange,
                                label = "Restrictions",
                                tooltipTitle = "Restriction Codes (RC)",
                                tooltipContent = "Driving restrictions that apply to the license. Multiple codes can be combined. Common codes: B (Corrective lenses), C (Prosthetic device), D (Outside mirror), G (Daylight only), K (Moped restricted), L (Vehicle without air brakes), M (Class M only).",
                                modifier = Modifier.weight(1f),
                                isRequired = false
                            )
                            TooltipTextField(
                                value = endorsements,
                                onValueChange = onEndorsementsChange,
                                label = "Endorsements",
                                tooltipTitle = "Endorsement Codes (EC)",
                                tooltipContent = "Additional driving privileges or certifications. Common codes: H (Hazardous materials), N (Tank vehicle), P (Passenger), S (School bus), T (Doubles/Triples). Leave blank for standard license or ID card.",
                                modifier = Modifier.weight(1f),
                                isRequired = false
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TooltipTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    tooltipTitle: String,
    tooltipContent: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    maxChars: Int? = null,
    isRequired: Boolean = false
) {
    var showHelpDialog by remember { mutableStateOf(false) }
    
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                val limitedValue = maxChars?.let { newValue.take(it) } ?: newValue
                onValueChange(limitedValue)
            },
            label = { 
                Text(
                    text = if (isRequired) "$label *" else label,
                    style = MaterialTheme.typography.bodyMedium
                ) 
            },
            modifier = Modifier.weight(1f),
            singleLine = singleLine,
            keyboardOptions = keyboardOptions,
            isError = isRequired && value.isEmpty(),
            supportingText = if (isRequired && value.isEmpty()) {
                { Text("Required", color = MaterialTheme.colorScheme.error) }
            } else null
        )
        IconButton(onClick = { showHelpDialog = true }) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "More information about $label",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
    
    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            title = { 
                Text(
                    text = tooltipTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                ) 
            },
            text = { 
                Column {
                    Text(
                        text = tooltipContent,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Format: $label",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showHelpDialog = false }) {
                    Text("Got it!")
                }
            }
        )
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