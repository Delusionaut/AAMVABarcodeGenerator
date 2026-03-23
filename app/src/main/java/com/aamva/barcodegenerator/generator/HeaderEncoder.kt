package com.aamva.barcodegenerator.generator

import com.aamva.barcodegenerator.util.AAMVAConstants

/**
 * Encodes the AAMVA PDF417 barcode header according to Table D.1 of the standard.
 * 
 * The header format is:
 * Field 1: Compliance Indicator (@) - 1 byte
 * Field 2: Data Element Separator (LF) - 1 byte
 * Field 3: Record Separator (RS) - 1 byte
 * Field 4: Segment Terminator (CR) - 1 byte
 * Field 5: File Type (ANSI ) - 5 bytes
 * Field 6: Issuer Identification Number (IIN) - 6 bytes
 * Field 7: AAMVA Version Number - 2 bytes
 * Field 8: Jurisdiction Version Number - 2 bytes
 * Field 9: Number of Entries - 2 bytes
 */
class HeaderEncoder {
    
    data class HeaderData(
        val issuerIdentificationNumber: String,  // 6-digit IIN
        val aamvaVersionNumber: String,          // 2-digit version
        val jurisdictionVersionNumber: String,   // 2-digit version
        val numberOfSubfiles: Int                // Number of subfiles (01-99)
    )
    
    /**
     * Encodes the header with proper formatting and padding.
     */
    fun encodeHeader(headerData: HeaderData): String {
        val sb = StringBuilder()
        
        // Field 1: Compliance Indicator
        sb.append(AAMVAConstants.COMPLIANCE_INDICATOR)
        
        // Field 2: Data Element Separator
        sb.append(AAMVAConstants.DATA_ELEMENT_SEPARATOR)
        
        // Field 3: Record Separator
        sb.append(AAMVAConstants.RECORD_SEPARATOR)
        
        // Field 4: Segment Terminator
        sb.append(AAMVAConstants.SEGMENT_TERMINATOR)
        
        // Field 5: File Type (ANSI with trailing space)
        sb.append(AAMVAConstants.FILE_TYPE)
        
        // Field 6: Issuer Identification Number (6 digits, truncated if longer, zero-padded if shorter)
        val truncatedIIN = if (headerData.issuerIdentificationNumber.length > 6)
            headerData.issuerIdentificationNumber.takeLast(6)
        else
            headerData.issuerIdentificationNumber
        sb.append(truncatedIIN.padStart(6, '0'))
        
        // Field 7: AAMVA Version Number (2 digits)
        sb.append(headerData.aamvaVersionNumber.padStart(2, '0').take(2))
        
        // Field 8: Jurisdiction Version Number (2 digits)
        sb.append(headerData.jurisdictionVersionNumber.padStart(2, '0').take(2))
        
        // Field 9: Number of Entries (2 digits)
        sb.append(headerData.numberOfSubfiles.toString().padStart(2, '0').take(2))
        
        return sb.toString()
    }
    
    /**
     * Validates the IIN format (should be 6 digits).
     */
    fun validateIIN(iin: String): Boolean {
        return iin.all { it.isDigit() } && iin.length == 6
    }
    
    /**
     * Gets the default AAMVA version for 2020 standard.
     */
    fun getDefaultAAMVAVersion(): String {
        return AAMVAConstants.AAMVA_VERSION_2020
    }
}