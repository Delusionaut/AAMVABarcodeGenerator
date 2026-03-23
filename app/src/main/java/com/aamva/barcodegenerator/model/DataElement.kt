package com.aamva.barcodegenerator.model

/**
 * Represents a data element in the AAMVA PDF417 barcode standard.
 * Each data element has an ID, value, and whether it's mandatory or optional.
 */
data class DataElement(
    val elementId: String,           // 3-letter element ID (e.g., "DCS", "DBA")
    val value: String,               // The actual data value
    val isMandatory: Boolean,        // Whether this element is mandatory
    val fieldType: FieldType,        // Fixed or variable length
    val maxLength: Int,              // Maximum allowed length
    val validCharacters: String = "ANS"  // Valid character types: A=alpha, N=numeric, S=special
)

enum class FieldType {
    FIXED,   // F - Fixed length
    VARIABLE // V - Variable length
}