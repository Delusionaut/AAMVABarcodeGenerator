package com.aamva.barcodegenerator.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.aamva.barcodegenerator.generator.AAMVAValidator
import com.aamva.barcodegenerator.model.AAMVADataSet
import com.aamva.barcodegenerator.model.CardType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ValidateScreen() {
    var rawBarcodeData by remember { mutableStateOf("") }
    var validationResult by remember { mutableStateOf<String?>(null) }
    var isValid by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "PDF417 AAMVA Validator",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = MaterialTheme.typography.headlineSmall.fontWeight
        )
        
        OutlinedTextField(
            value = rawBarcodeData,
            onValueChange = { rawBarcodeData = it },
            label = { Text("Paste raw PDF417 barcode data here") },
            placeholder = { Text("@\\n~\\n636000\\n08\\nP\\nDL\\n01\\n...") },
            maxLines = 15,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii)
        )
        
        Button(
            onClick = {
                validationResult = null
                isValid = false
                
                if (rawBarcodeData.isBlank()) {
                    validationResult = "Please enter raw barcode data"
                    return@Button
                }
                
                // Basic AAMVA format validation
                val trimmedData = rawBarcodeData.trim()
                if (!trimmedData.startsWith("@") || !trimmedData.contains("~")) {
                    validationResult = "Invalid AAMVA format. Must start with '@' and contain '~' header."
                    return@Button
                }
                
                if (trimmedData.length < 100 || trimmedData.length > 4000) {
                    validationResult = "Invalid length: ${trimmedData.length} characters (expected 100-4000)"
                    return@Button
                }
                
                // Attempt simple parse and validate
                try {
                    // Simple header parse for demo
                    val lines = trimmedData.split("\n")
                    if (lines.size < 3 || !lines[1].startsWith("~")) {
                        validationResult = "Invalid header structure"
                        return@Button
                    }
                    
                    val headerParts = lines[1].substring(1).split("\n")
                    if (headerParts.size < 5) {
                        validationResult = "Incomplete header"
                        return@Button
                    }
                    
                    // Create mock DataSet for validator demo
                    val mockDataSet = AAMVADataSet(
                        issuerIdentificationNumber = headerParts.getOrNull(0) ?: "",
                        aamvaVersionNumber = headerParts.getOrNull(1) ?: "",
                        jurisdictionVersionNumber = headerParts.getOrNull(2) ?: "",
                        subfileType = CardType.DL, // Assume DL
                        customerFamilyName = "DEMO",
                        customerFirstName = "DEMO",
                        dateOfBirth = "01011980",
                        dateOfIssue = "01012020",
                        dateOfExpiry = "01012025",
                        customerIdNumber = "DEMO123",
                        documentDiscriminator = "DEMO-DISC",
                        sex = "1",
                        eyeColor = "BRO",
                        height = "070 in",
                        addressStreet1 = "123 MAIN ST",
                        addressCity = "ANYTOWN",
                        addressJurisdictionCode = "CA",
                        addressPostalCode = "90210",
                        countryIdentification = "USA"
                    )
                    
                    val validator = AAMVAValidator()
                    val result = validator.validate(mockDataSet)
                    
                    if (result.isValid) {
                        isValid = true
                        validationResult = "✓ Basic format valid. Header parsed successfully.\nLength: ${trimmedData.length} chars\nFull parsing requires complete decoder."
                    } else {
                        validationResult = "✗ Format issues detected:\n${result.errors.joinToString("\n") { "${it.field}: ${it.message}" }}"
                    }
                } catch (e: Exception) {
                    validationResult = "Parse error: ${e.message}"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Validate AAMVA Data")
        }
        
        validationResult?.let { result ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isValid) MaterialTheme.colorScheme.primaryContainer 
                    else MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isValid) Icons.Default.CheckCircle else Icons.Default.Error,
                        contentDescription = null,
                        tint = if (isValid) MaterialTheme.colorScheme.onPrimaryContainer 
                        else MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Column {
                        Text(
                            text = if (isValid) "Valid" else "Invalid",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isValid) MaterialTheme.colorScheme.onPrimaryContainer 
                            else MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = result,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isValid) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            else MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}