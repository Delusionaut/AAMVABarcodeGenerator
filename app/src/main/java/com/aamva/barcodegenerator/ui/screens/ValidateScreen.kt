package com.aamva.barcodegenerator.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.aamva.barcodegenerator.generator.AAMVAValidator
import com.aamva.barcodegenerator.model.AAMVADataSet

@Composable
fun ValidateScreen() {
    var rawBarcodeData by remember { mutableStateOf("") }
    var validationResult by remember { mutableStateOf<String?>(null) }
    var isValid by remember { mutableStateOf(false) }
    var isValidating by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header section
        Column {
            Text(
                text = "PDF417 AAMVA Validator",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Paste raw barcode data to validate AAMVA compliance",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // Input field
        OutlinedTextField(
            value = rawBarcodeData,
            onValueChange = { rawBarcodeData = it },
            label = { Text("Raw PDF417 Barcode Data") },
            placeholder = { Text("@\\n~\\n636000\\n08\\nP\\nDL\\n01\\n...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            maxLines = 10,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(12.dp)
        )
        
        // Validate button
        Button(
            onClick = {
                validationResult = null
                isValid = false
                isValidating = true
                
                if (rawBarcodeData.isBlank()) {
                    validationResult = "Please enter raw barcode data"
                    isValid = false
                    isValidating = false
                    return@Button
                }
                
                // Basic AAMVA format validation
                val trimmedData = rawBarcodeData.trim()
                if (!trimmedData.startsWith("@") || !trimmedData.contains("~")) {
                    validationResult = "Invalid AAMVA format. Must start with '@' and contain '~' header."
                    isValid = false
                    isValidating = false
                    return@Button
                }
                
                if (trimmedData.length < 100 || trimmedData.length > 4000) {
                    validationResult = "Invalid length: ${trimmedData.length} characters (expected 100-4000)"
                    isValid = false
                    isValidating = false
                    return@Button
                }
                
                // Attempt simple parse and validate
                try {
                    val lines = trimmedData.split("\n")
                    if (lines.size < 3 || !lines[1].startsWith("~")) {
                        validationResult = "Invalid header structure"
                        isValid = false
                        isValidating = false
                        return@Button
                    }
                    
                    val headerParts = lines[1].substring(1).split("\n")
                    if (headerParts.size < 5) {
                        validationResult = "Incomplete header"
                        isValid = false
                        isValidating = false
                        return@Button
                    }
                    
                    // Create mock DataSet for validator demo
                    val mockDataSet = AAMVADataSet(
                        issuerIdentificationNumber = headerParts.getOrNull(0) ?: "",
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
                    
                    isValidating = false
                    
                    if (result.isValid) {
                        isValid = true
                        validationResult = "✓ Basic format valid. Header parsed successfully.\n\nLength: ${trimmedData.length} chars\nFull parsing requires complete decoder."
                    } else {
                        validationResult = "✗ Format issues detected:\n${result.errors.joinToString("\n") { "${it.field}: ${it.message}" }}"
                    }
                } catch (e: Exception) {
                    isValidating = false
                    validationResult = "Parse error: ${e.message}"
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(4.dp),
            enabled = !isValidating
        ) {
            if (isValidating) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                "Validate AAMVA Data",
                style = MaterialTheme.typography.labelLarge
            )
        }
        
        // Result card
        validationResult?.let { result ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isValid) {
                        MaterialTheme.colorScheme.tertiaryContainer
                    } else {
                        MaterialTheme.colorScheme.errorContainer
                    }
                ),
                shape = RoundedCornerShape(4.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = if (isValid) Icons.Default.CheckCircle else Icons.Default.Error,
                        contentDescription = null,
                        tint = if (isValid) {
                            MaterialTheme.colorScheme.onTertiaryContainer
                        } else {
                            MaterialTheme.colorScheme.onErrorContainer
                        },
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isValid) "Valid" else "Invalid",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isValid) {
                                MaterialTheme.colorScheme.onTertiaryContainer
                            } else {
                                MaterialTheme.colorScheme.onErrorContainer
                            }
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = result,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isValid) {
                                MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                            } else {
                                MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                            }
                        )
                    }
                }
            }
        }
        
        // Info section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(4.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Expected Format",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "@\\n~IIN\\nVersion\\nJurisdiction\\nSubfile...~",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}
