package com.aamva.barcodegenerator.ui.screens

import com.aamva.barcodegenerator.ui.theme.GovernmentNavy

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
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
    Generate, History, Validate, Settings
}

object SettingsManager {
    private const val PREFS_NAME = "AAMVA_Settings"
    private const val KEY_SAVE_LOCATION = "save_location"
    private const val KEY_AUTO_SAVE = "auto_save"
    
    const val LOCATION_PICTURES = "Pictures/AAMVA_Barcodes"
    const val LOCATION_DOWNLOADS = "Downloads/AAMVA_Barcodes"
    const val LOCATION_DOCUMENTS = "Documents/AAMVA_Barcodes"
    
    fun getSaveLocation(context: Context): String {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_SAVE_LOCATION, LOCATION_PICTURES) ?: LOCATION_PICTURES
    }
    
    fun setSaveLocation(context: Context, location: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_SAVE_LOCATION, location)
            .apply()
    }
    
    fun getAutoSave(context: Context): Boolean {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_AUTO_SAVE, true)
    }
    
    fun setAutoSave(context: Context, autoSave: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_AUTO_SAVE, autoSave)
            .apply()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val prefs = remember { context.getSharedPreferences("AAMVA_Settings", Context.MODE_PRIVATE) }

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
    var currentFilePath by remember { mutableStateOf<String?>(null) }

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

    // Barcode generation function
    fun generateAndSaveBarcode() {
        Toast.makeText(context, "Generating barcode...", Toast.LENGTH_SHORT).show()
        showProgressDialog = true
        errorMessage = null
        showBarcode = false
        currentFilePath = null
        
        scope.launch {
            try {
                val dataSet = AAMVADataSet(
                    issuerIdentificationNumber = iin,
                    customerFamilyName = familyName,
                    customerFirstName = firstName,
                    customerMiddleName = middleName,
                    dateOfBirth = dateOfBirth,
                    dateOfIssue = dateOfIssue.ifEmpty { SimpleDateFormat("MMddyyyy", Locale.US).format(Date()) },
                    dateOfExpiry = dateOfExpiry,
                    customerIdNumber = customerId,
                    documentDiscriminator = documentDiscriminator,
                    sex = sex,
                    eyeColor = eyeColor,
                    height = height,
                    addressStreet1 = addressStreet,
                    addressCity = addressCity,
                    addressJurisdictionCode = addressState,
                    addressPostalCode = addressZip,
                    vehicleClass = vehicleClass,
                    restrictionCodes = restrictions,
                    endorsementCodes = endorsements,
                    countryIdentification = "USA"
                )

                val validationResult = barcodeGenerator.validateDataSet(dataSet)
                if (!validationResult.isValid) {
                    errorMessage = validationResult.errors.joinToString("\n")
                    showProgressDialog = false
                    Toast.makeText(context, "Validation failed", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                rawData = barcodeGenerator.generateAndValidateBarcode(dataSet)

                val bitmap = BarcodeFormatter.generatePDF417BitmapWithECL(
                    data = rawData,
                    width = 800,
                    height = 250,
                    errorCorrectionLevel = 5
                )
                
                if (bitmap != null) {
                    barcodeBitmap = bitmap
                    showBarcode = true
                    
                    // Auto-save if enabled
                    if (SettingsManager.getAutoSave(context)) {
                        val filePath = BarcodeSaver.saveBarcodeToStorage(
                            context, bitmap, firstName, familyName, addressState, dateOfBirth
                        )
                        if (filePath != null) {
                            currentFilePath = filePath
                            val historyItem = BarcodeSaver.createHistoryItem(
                                firstName, familyName, dateOfBirth, customerId, filePath
                            )
                            historyManager.addHistoryItem(historyItem)
                            historyItems = historyManager.getHistory()
                            showSaveSuccessDialog = true
                            Toast.makeText(context, "Saved to $filePath", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, "Barcode generated but save failed", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Barcode generated! (Auto-save disabled)", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    errorMessage = "Failed to render barcode image"
                    Toast.makeText(context, "Render failed", Toast.LENGTH_SHORT).show()
                }
            } catch (e: AAMVAComplianceException) {
                errorMessage = e.errors.joinToString("\n\n") { "• ${it.field}: ${it.message}" }
                Toast.makeText(context, "Compliance error", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                errorMessage = "Generation failed: ${e.message}"
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                showProgressDialog = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Image(
                        painter = painterResource(id = R.drawable.headerlogo),
                        contentDescription = "AAMVA PDF417 Generator Logo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        contentScale = ContentScale.FillBounds
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = GovernmentNavy
                ),
                windowInsets = WindowInsets(0.dp)
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
                NavigationBarItem(
                    selected = currentNavTab == NavTab.Settings,
                    onClick = { currentNavTab = NavTab.Settings },
                    icon = {
                        Icon(
                            imageVector = if (currentNavTab == NavTab.Settings) Icons.Filled.Settings else Icons.Outlined.Settings,
                            contentDescription = null
                        )
                    },
                    label = { Text("Settings") },
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
            if (currentNavTab == NavTab.Generate) {
                ExtendedFloatingActionButton(
                    onClick = { generateAndSaveBarcode() },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Outlined.QrCode, contentDescription = "Generate")
                    Spacer(Modifier.width(8.dp))
                    Text("GENERATE & SAVE", fontWeight = FontWeight.Bold)
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxWidth()
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

                    // Barcode preview
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
                                currentFilePath?.let { path ->
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        "Saved: $path",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
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
                NavTab.Settings -> {
                    SettingsScreen()
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
                        "Validating data, generating barcode, saving...",
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
            text = { Text("Saved to ${SettingsManager.getSaveLocation(context)}") },
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

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    var saveLocation by remember { mutableStateOf(SettingsManager.getSaveLocation(context)) }
    var autoSave by remember { mutableStateOf(SettingsManager.getAutoSave(context)) }
    var showLocationDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Settings",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = GovernmentNavy
        )
        
        Spacer(Modifier.height(24.dp))
        
        // Auto-save toggle
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Auto-save Barcodes",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        "Automatically save to storage after generation",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = autoSave,
                    onCheckedChange = { 
                        autoSave = it
                        SettingsManager.setAutoSave(context, it)
                    }
                )
            }
        }
        
        Spacer(Modifier.height(12.dp))
        
        // Save location
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showLocationDialog = true },
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Save Location",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        saveLocation,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Spacer(Modifier.height(24.dp))
        
        // Info section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Filename Format",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Firstname_First3LettersLastname_DOB_State.png",
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Example: John_Doe_01011990_CA.png",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
    
    // Location selection dialog
    if (showLocationDialog) {
        AlertDialog(
            onDismissRequest = { showLocationDialog = false },
            title = { Text("Save Location") },
            text = {
                Column {
                    LocationOption(
                        title = "Pictures",
                        path = SettingsManager.LOCATION_PICTURES,
                        selected = saveLocation == SettingsManager.LOCATION_PICTURES,
                        onClick = {
                            saveLocation = SettingsManager.LOCATION_PICTURES
                            SettingsManager.setSaveLocation(context, SettingsManager.LOCATION_PICTURES)
                            showLocationDialog = false
                        }
                    )
                    LocationOption(
                        title = "Downloads",
                        path = SettingsManager.LOCATION_DOWNLOADS,
                        selected = saveLocation == SettingsManager.LOCATION_DOWNLOADS,
                        onClick = {
                            saveLocation = SettingsManager.LOCATION_DOWNLOADS
                            SettingsManager.setSaveLocation(context, SettingsManager.LOCATION_DOWNLOADS)
                            showLocationDialog = false
                        }
                    )
                    LocationOption(
                        title = "Documents",
                        path = SettingsManager.LOCATION_DOCUMENTS,
                        selected = saveLocation == SettingsManager.LOCATION_DOCUMENTS,
                        onClick = {
                            saveLocation = SettingsManager.LOCATION_DOCUMENTS
                            SettingsManager.setSaveLocation(context, SettingsManager.LOCATION_DOCUMENTS)
                            showLocationDialog = false
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showLocationDialog = false }) {
                    Text("Cancel")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
private fun LocationOption(
    title: String,
    path: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(
                path,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (selected) {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
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
    val today = Calendar.getInstance()
    val todayMillis = today.timeInMillis
    
    // Generate a random date in the past, up to 5 years ago, but never after today
    val fiveYearsAgo = Calendar.getInstance().apply {
        add(Calendar.YEAR, -5)
    }.timeInMillis
    
    // Get random time between 5 years ago and today
    val randomMillis = (fiveYearsAgo..todayMillis).random()
    val randomCalendar = Calendar.getInstance().apply {
        timeInMillis = randomMillis
    }
    
    // Ensure day is valid for the month
    val maxDay = randomCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val day = minOf((1..28).random(), maxDay)
    randomCalendar.set(Calendar.DAY_OF_MONTH, day)
    
    return SimpleDateFormat("MMddyyyy", Locale.US).format(randomCalendar.time)
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
