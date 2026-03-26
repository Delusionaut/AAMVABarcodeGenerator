package com.aamva.barcodegenerator.ui.screens

import com.aamva.barcodegenerator.ui.theme.GovernmentNavy

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.aamva.barcodegenerator.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aamva.barcodegenerator.generator.AAMVABarcodeGenerator
import com.aamva.barcodegenerator.generator.AAMVAComplianceException
import com.aamva.barcodegenerator.model.AAMVADataSet
import com.aamva.barcodegenerator.model.HistoryItem
import com.aamva.barcodegenerator.util.BarcodeFormatter
import com.aamva.barcodegenerator.util.BarcodeSaver
import com.aamva.barcodegenerator.util.HistoryManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.*

enum class NavTab {
    Generate, History, Validate
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var currentNavTab by remember { mutableStateOf(NavTab.Generate) }
    var currentFormTab by remember { mutableStateOf(FormTab.Personal) }

    // Form state hoisted
    var iin by remember { mutableStateOf("636000") }
    var familyName by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var middleName by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var dateOfIssue by remember { mutableStateOf("") }
    var dateOfExpiry by remember { mutableStateOf("") }
    var customerId by remember { mutableStateOf("") }
    var documentDiscriminator by remember { mutableStateOf("") }
    var sex by remember { mutableStateOf("1") }
    var eyeColor by remember { mutableStateOf("BRO") }
    var height by remember { mutableStateOf("070 in") }
    var addressStreet by remember { mutableStateOf("") }
    var addressCity by remember { mutableStateOf("") }
    var addressState by remember { mutableStateOf("") }
    var addressZip by remember { mutableStateOf("") }
    var vehicleClass by remember { mutableStateOf("") }
    var restrictions by remember { mutableStateOf("") }
    var endorsements by remember { mutableStateOf("") }

    // Generation state
    var showBarcode by remember { mutableStateOf(false) }
    var barcodeBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var rawData by remember { mutableStateOf("") }
    var showProgressDialog by remember { mutableStateOf(false) }

    // History state
    var historyItems by remember { mutableStateOf<List<HistoryItem>>(emptyList()) }
    var showSaveSuccessDialog by remember { mutableStateOf(false) }
    var showViewDialog by remember { mutableStateOf(false) }
    var viewingBarcodeBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var currentViewingItem by remember { mutableStateOf<HistoryItem?>(null) }

    val historyManager = remember { HistoryManager(context) }
    val barcodeGenerator = remember { AAMVABarcodeGenerator() }

    LaunchedEffect(Unit) {
        historyItems = historyManager.getHistory()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.headerlogo),
                            contentDescription = "AAMVA PDF417 Generator Logo",
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(),
                            contentScale = ContentScale.Fit
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = GovernmentNavy
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = androidx.compose.ui.unit.Dp(0f)
            ) {
                NavigationBarItem(
                    selected = currentNavTab == NavTab.Generate,
                    onClick = { currentNavTab = NavTab.Generate },
                    icon = { 
                        Icon(
                            imageVector = if (currentNavTab == NavTab.Generate) Icons.Filled.QrCodeScanner else Icons.Outlined.QrCode,
                            contentDescription = null
                        ) 
                    },
                    label = { Text("Generate") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
                NavigationBarItem(
                    selected = currentNavTab == NavTab.History,
                    onClick = { currentNavTab = NavTab.History },
                    icon = { 
                        Icon(
                            imageVector = if (currentNavTab == NavTab.History) Icons.Filled.History else Icons.Outlined.History,
                            contentDescription = null
                        ) 
                    },
                    label = { Text("History") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
                NavigationBarItem(
                    selected = currentNavTab == NavTab.Validate,
                    onClick = { currentNavTab = NavTab.Validate },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.QrCodeScanner,
                            contentDescription = null
                        )
                    },
                    label = { Text("Validate") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = currentNavTab == NavTab.Generate,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                ExtendedFloatingActionButton(
                    text = { 
                        Text(
                            "Generate Barcode",
                            style = MaterialTheme.typography.labelLarge
                        ) 
                    },
                    icon = { Icon(Icons.Outlined.QrCode, contentDescription = null) },
                    onClick = {
                        showProgressDialog = true
                        errorMessage = null
                        showBarcode = false
                        barcodeBitmap = null
                        scope.launch {
                            try {
                                // Build the data set
                                val dataSet = AAMVADataSet(
                                    issuerIdentificationNumber = iin.ifBlank { "636000" },
                                    customerFamilyName = familyName.ifBlank { "DOE" },
                                    customerFirstName = firstName.ifBlank { "JOHN" },
                                    customerMiddleName = middleName,
                                    dateOfBirth = dateOfBirth.ifBlank { "01011990" },
                                    dateOfIssue = dateOfIssue.ifBlank { SimpleDateFormat("MMddyyyy", Locale.US).format(Date()) },
                                    dateOfExpiry = dateOfExpiry.ifBlank { "01012030" },
                                    customerIdNumber = customerId.ifBlank { "D1234567" },
                                    documentDiscriminator = documentDiscriminator.ifBlank { "DOC123456" },
                                    sex = sex.ifBlank { "1" },
                                    eyeColor = eyeColor.ifBlank { "BRO" },
                                    height = height.ifBlank { "070 in" },
                                    addressStreet1 = addressStreet.ifBlank { "123 MAIN ST" },
                                    addressCity = addressCity.ifBlank { "SPRINGFIELD" },
                                    addressJurisdictionCode = addressState.ifBlank { "VA" },
                                    addressPostalCode = addressZip.ifBlank { "12345" },
                                    vehicleClass = vehicleClass,
                                    restrictionCodes = restrictions,
                                    endorsementCodes = endorsements,
                                    countryIdentification = "USA"
                                )

                                // Validate the data
                                val validationResult = barcodeGenerator.validateDataSet(dataSet)
                                if (!validationResult.isValid) {
                                    errorMessage = validationResult.errors.joinToString("\n")
                                    showProgressDialog = false
                                    return@launch
                                }

                                // Generate barcode
                                rawData = barcodeGenerator.generateAndValidateBarcode(dataSet)

                                // Format barcode to bitmap
                                val bitmap = BarcodeFormatter.generatePDF417BitmapWithECL(
                                    data = rawData,
                                    width = 800,
                                    height = 250,
                                    errorCorrectionLevel = 5
                                )

                                if (bitmap != null) {
                                    barcodeBitmap = bitmap
                                    showBarcode = true
                                    errorMessage = null
                                } else {
                                    errorMessage = "Failed to render barcode image"
                                }
                            } catch (e: AAMVAComplianceException) {
                                errorMessage = e.errors.joinToString("\n\n") { "• ${it.field}: ${it.message}" }
                            } catch (e: Exception) {
                                errorMessage = "Generation failed: ${e.javaClass.simpleName}: ${e.message}"
                            } finally {
                                showProgressDialog = false
                            }
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentNavTab) {
                NavTab.Generate -> {
                    GenerateScreen(
                        currentFormTab = currentFormTab,
                        onFormTabChange = { currentFormTab = it },
                        iin = iin, onIinChange = { iin = it },
                        familyName = familyName, onFamilyNameChange = { familyName = it },
                        firstName = firstName, onFirstNameChange = { firstName = it },
                        middleName = middleName, onMiddleNameChange = { middleName = it },
                        dateOfBirth = dateOfBirth, onDateOfBirthChange = { dateOfBirth = it },
                        dateOfIssue = dateOfIssue, onDateOfIssueChange = { dateOfIssue = it },
                        onRandomIssueDate = { dateOfIssue = generateRandomIssueDate() },
                        dateOfExpiry = dateOfExpiry, onDateOfExpiryChange = { dateOfExpiry = it },
                        onCalculateExpiry = { dateOfExpiry = calculateExpiryDate(dateOfBirth, dateOfIssue) },
                        customerId = customerId, onCustomerIdChange = { customerId = it },
                        documentDiscriminator = documentDiscriminator, onDocumentDiscriminatorChange = { documentDiscriminator = it },
                        onCalculateDocDisc = { documentDiscriminator = calculateDocumentDiscriminator(dateOfIssue, dateOfExpiry) },
                        sex = sex, onSexChange = { sex = it },
                        eyeColor = eyeColor, onEyeColorChange = { eyeColor = it },
                        height = height, onHeightChange = { height = it },
                        addressStreet = addressStreet, onAddressStreetChange = { addressStreet = it },
                        addressCity = addressCity, onAddressCityChange = { addressCity = it },
                        addressState = addressState, onAddressStateChange = { addressState = it },
                        addressZip = addressZip, onAddressZipChange = { addressZip = it },
                        vehicleClass = vehicleClass, onVehicleClassChange = { vehicleClass = it },
                        restrictions = restrictions, onRestrictionsChange = { restrictions = it },
                        endorsements = endorsements, onEndorsementsChange = { endorsements = it }
                    )

                    // Barcode preview and save
                    if (showBarcode && barcodeBitmap != null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "Generated PDF417 Barcode",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(Modifier.height(16.dp))
                                Image(
                                    bitmap = barcodeBitmap!!.asImageBitmap(),
                                    contentDescription = "Barcode",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                )
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    "Length: ${rawData.length} characters",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.height(16.dp))
                                Button(
                                    onClick = {
                                        scope.launch {
                                            val filePath = BarcodeSaver.saveBarcodeToStorage(
                                                context, barcodeBitmap!!, firstName, familyName
                                            )
                                            if (filePath != null) {
                                                val historyItem = BarcodeSaver.createHistoryItem(
                                                    firstName, familyName, dateOfBirth, customerId, filePath
                                                )
                                                historyManager.addHistoryItem(historyItem)
                                                historyItems = historyManager.getHistory()
                                                showSaveSuccessDialog = true
                                            } else {
                                                Toast.makeText(context, "Save failed", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text("Save PNG")
                                }
                            }
                        }
                    }

                    errorMessage?.let { error ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text(
                                    "Error",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    error,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }
                NavTab.History -> {
                    HistoryScreen(
                        historyItems = historyItems,
                        onDeleteItem = { item ->
                            historyManager.deleteHistoryItem(item.id)
                            historyItems = historyManager.getHistory()
                        },
                        onViewItem = { item ->
                            scope.launch {
                                val bitmap = loadBitmap(context, item.filePath)
                                if (bitmap != null) {
                                    viewingBarcodeBitmap = bitmap
                                    currentViewingItem = item
                                    showViewDialog = true
                                } else {
                                    Toast.makeText(context, "Load failed", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    )
                }
                NavTab.Validate -> {
                    ValidateScreen()
                }
            }
        }
    }

    // Progress Dialog
    if (showProgressDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Generating...") },
            text = { 
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Validating data, generating barcode, rendering image...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            confirmButton = { },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Save Success
    if (showSaveSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSaveSuccessDialog = false },
            title = { Text("Saved!") },
            text = { Text("Saved to Pictures/AAMVA_Barcodes/") },
            confirmButton = {
                TextButton(onClick = { showSaveSuccessDialog = false }) { 
                    Text("OK", color = MaterialTheme.colorScheme.primary) 
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // View Dialog
    currentViewingItem?.let { item ->
        viewingBarcodeBitmap?.let { bitmap ->
            val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.US)
            AlertDialog(
                onDismissRequest = {
                    showViewDialog = false
                    viewingBarcodeBitmap = null
                    currentViewingItem = null
                },
                title = { 
                    Text(
                        "${item.familyName}, ${item.firstName}",
                        fontWeight = FontWeight.SemiBold
                    ) 
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            bitmap = bitmap.asImageBitmap(), 
                            contentDescription = "Barcode", 
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(Modifier.height(16.dp))
                        Text("ID: ${item.customerId}", style = MaterialTheme.typography.bodyMedium)
                        Text("DOB: ${item.dateOfBirth}", style = MaterialTheme.typography.bodySmall)
                        Text(
                            "Saved: ${dateFormat.format(item.timestamp)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        showViewDialog = false
                        viewingBarcodeBitmap = null
                        currentViewingItem = null
                    }) { 
                        Text("Close", color = MaterialTheme.colorScheme.primary) 
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

private suspend fun loadBitmap(context: Context, filePath: String): Bitmap? = withContext(Dispatchers.IO) {
    try {
        val inputStream = if (filePath.startsWith("content://")) {
            val uri = Uri.parse(filePath)
            context.contentResolver.openInputStream(uri)
        } else {
            FileInputStream(File(filePath))
        }
        inputStream?.use { android.graphics.BitmapFactory.decodeStream(it) }
    } catch (e: Exception) {
        null
    }
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

private fun calculateExpiryDate(dob: String, issue: String): String {
    if (dob.length != 8 || issue.length != 8) return ""
    val mmdd = dob.substring(0, 4)
    val issueYear = issue.substring(4, 8).toIntOrNull() ?: return ""
    val expiryYear = issueYear + 5
    return mmdd + expiryYear.toString().padStart(4, '0')
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
