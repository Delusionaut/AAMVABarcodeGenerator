package com.aamva.barcodegenerator.generator

import com.aamva.barcodegenerator.model.AAMVADataSet
import com.aamva.barcodegenerator.model.CardType
import com.aamva.barcodegenerator.util.AAMVAConstants

/**
 * Encodes subfiles for the AAMVA PDF417 barcode according to Table D.2 of the standard.
 * 
 * Subfile format:
 * Field 1: Subfile Type - 2 characters (DL, ID, EN, or ZX)
 * Field 2: Offset - 4 digits (byte offset from start of file)
 * Field 3: Length - 4 digits (length of subfile in bytes)
 */
class SubfileEncoder {
    
    data class SubfileData(
        val subfileType: String,     // DL, ID, EN, or Zx
        val offset: Int,             // Byte offset from start of file
        val length: Int              // Length of subfile in bytes
    )
    
    /**
     * Encodes a subfile designator.
     */
    fun encodeSubfileDesignator(subfileData: SubfileData): String {
        val sb = StringBuilder()
        
        // Field 1: Subfile Type (2 characters)
        sb.append(subfileData.subfileType.padEnd(2, ' ').take(2))
        
        // Field 2: Offset (4 digits, zero-padded)
        sb.append(subfileData.offset.toString().padStart(4, '0').take(4))
        
        // Field 3: Length (4 digits, zero-padded)
        sb.append(subfileData.length.toString().padStart(4, '0').take(4))
        
        return sb.toString()
    }
    
    /**
     * Creates the DL or ID subfile content from the data set.
     */
    fun createDLSubfile(dataSet: AAMVADataSet): String {
        val sb = StringBuilder()
        
        if (dataSet.subfileType == CardType.DL) {
            // DL-specific mandatory - use NONE if empty
            sb.append(encodeDataElement(AAMVAConstants.MandatoryElements.DCA, dataSet.vehicleClass, true))
            sb.append(encodeDataElement(AAMVAConstants.MandatoryElements.DCB, dataSet.restrictionCodes, true))
            sb.append(encodeDataElement(AAMVAConstants.MandatoryElements.DCD, dataSet.endorsementCodes, true))
        } else {
            // For ID, these are not mandatory, skip or NONE?
            // Standard uses same subfile structure, so include as NONE
            sb.append(encodeDataElement(AAMVAConstants.MandatoryElements.DCA, "", true))
            sb.append(encodeDataElement(AAMVAConstants.MandatoryElements.DCB, "", true))
            sb.append(encodeDataElement(AAMVAConstants.MandatoryElements.DCD, "", true))
        }
        
        // Common mandatory
        sb.append(encodeDataElement(AAMVAConstants.MandatoryElements.DBA, dataSet.dateOfExpiry, true))
        sb.append(encodeDataElement(AAMVAConstants.MandatoryElements.DCS, dataSet.customerFamilyName, true))
        sb.append(encodeDataElement(AAMVAConstants.MandatoryElements.DAC, dataSet.customerFirstName, true))
        sb.append(encodeDataElement(AAMVAConstants.MandatoryElements.DAD, dataSet.customerMiddleName, true))
        sb.append(encodeDataElement(AAMVAConstants.MandatoryElements.DBD, dataSet.dateOfIssue, true))
        sb.append(encodeDataElement(AAMVAConstants.MandatoryElements.DBB, dataSet.dateOfBirth, true))
        sb.append(encodeDataElement(AAMVAConstants.MandatoryElements.DBC, dataSet.sex, true))
        sb.append(encodeDataElement(AAMVAConstants.MandatoryElements.DAY, dataSet.eyeColor, true))
        sb.append(encodeDataElement(AAMVAConstants.MandatoryElements.DAU, dataSet.height, true))
        sb.append(encodeDataElement(AAMVAConstants.MandatoryElements.DAG, dataSet.addressStreet1, true))
        sb.append(encodeDataElement(AAMVAConstants.MandatoryElements.DAI, dataSet.addressCity, true))
        sb.append(encodeDataElement(AAMVAConstants.MandatoryElements.DAJ, dataSet.addressJurisdictionCode, true))
        sb.append(encodeDataElement(AAMVAConstants.MandatoryElements.DAK, dataSet.addressPostalCode, true))
        sb.append(encodeDataElement(AAMVAConstants.MandatoryElements.DAQ, dataSet.customerIdNumber, true))
        sb.append(encodeDataElement(AAMVAConstants.MandatoryElements.DCF, dataSet.documentDiscriminator, true))
        sb.append(encodeDataElement(AAMVAConstants.MandatoryElements.DCG, dataSet.countryIdentification, true))
        sb.append(encodeDataElement(AAMVAConstants.MandatoryElements.DDE, dataSet.familyNameTruncation, true))
        sb.append(encodeDataElement(AAMVAConstants.MandatoryElements.DDF, dataSet.firstNameTruncation, true))
        sb.append(encodeDataElement(AAMVAConstants.MandatoryElements.DDG, dataSet.middleNameTruncation, true))
        
        // Optional elements
        addOptionalElement(sb, AAMVAConstants.OptionalElements.DAH, dataSet.addressStreet2)
        addOptionalElement(sb, AAMVAConstants.OptionalElements.DAZ, dataSet.hairColor)
        addOptionalElement(sb, AAMVAConstants.OptionalElements.DAW, dataSet.weightRange)
        
        // Segment terminator
        sb.append(AAMVAConstants.SEGMENT_TERMINATOR)
        
        return sb.toString()
    }
    
    
    private fun encodeDataElement(elementId: String, value: String, isMandatory: Boolean = false): String {
        val effectiveValue = if (isMandatory && value.isEmpty()) "NONE" else value
        return "$elementId$effectiveValue${AAMVAConstants.DATA_ELEMENT_SEPARATOR}"
    }
    
    private fun addOptionalElement(sb: StringBuilder, elementId: String, value: String) {
        if (value.isNotEmpty()) {
            sb.append(encodeDataElement(elementId, value))
        }
    }
    
    /**
     * Creates the jurisdiction-specific subfile content from the data set.
     * Returns null if no jurisdiction subfile should be created.
     */
    fun createJurisdictionSubfile(dataSet: AAMVADataSet): String? {
        // Only create jurisdiction subfile if jurisdiction code is present
        if (dataSet.addressJurisdictionCode.isBlank()) {
            return null
        }
        
        val sb = StringBuilder()
        
        // Add jurisdiction-specific data elements - optional, skip if empty
        addOptionalElement(sb, "ZVA", "") // Example jurisdiction field
        
        // Segment terminator
        sb.append(AAMVAConstants.SEGMENT_TERMINATOR)
        
        return sb.toString()
    }
}