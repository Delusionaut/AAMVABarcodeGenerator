package com.aamva.barcodegenerator.model

import java.util.Date

/**
 * Represents a saved barcode in history
 */
data class HistoryItem(
    val id: String,
    val firstName: String,
    val familyName: String,
    val dateOfBirth: String,
    val customerId: String,
    val timestamp: Date,
    val filePath: String
)