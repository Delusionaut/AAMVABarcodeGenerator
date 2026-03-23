package com.aamva.barcodegenerator.util

/**
 * Constants for AAMVA PDF417 Barcode Generation
 * Based on AAMVA 2020 DL/ID Card Design Standard
 */
object AAMVAConstants {
    
    // Header Characters (ASCII values)
    const val COMPLIANCE_INDICATOR = "@"          // ASCII 64
    const val DATA_ELEMENT_SEPARATOR = "\n"       // Line Feed, ASCII 10
    const val RECORD_SEPARATOR = "\u001E"         // RS, ASCII 30
    const val SEGMENT_TERMINATOR = "\r"           // Carriage Return, ASCII 13
    const val FILE_TYPE = "ANSI "                 // "ANSI" + space
    
    // Subfile Types
    const val SUBFILE_DL = "DL"
    const val SUBFILE_ID = "ID"
    const val SUBFILE_EN = "EN"
    const val SUBFILE_JURISDICTION_PREFIX = "Z"   // Jurisdiction-specific subfiles
    
    // AAMVA Version Numbers
    const val AAMVA_VERSION_2020 = "10"
    const val AAMVA_VERSION_2016 = "09"
    const val AAMVA_VERSION_2013 = "08"
    
    // Country Codes
    const val COUNTRY_USA = "USA"
    const val COUNTRY_CAN = "CAN"

    // California-Specific Constants
    const val CALIFORNIA_IIN = "636000"  // Issuer Identification Number for California
    const val CALIFORNIA_JURISDICTION_CODE = "CA"
    
    // Sex Codes
    const val SEX_MALE = "1"
    const val SEX_FEMALE = "2"
    const val SEX_NOT_SPECIFIED = "9"
    
    // Truncation Codes
    const val TRUNCATION_YES = "T"
    const val TRUNCATION_NO = "N"
    const val TRUNCATION_UNKNOWN = "U"
    
    // Compliance Type
    const val COMPLIANT = "F"
    const val NON_COMPLIANT = "N"
    
    // Date Formats
    const val DATE_FORMAT_US = "MMDDYYYY"
    const val DATE_FORMAT_CANADA = "CCYYMMDD"
    
    // Element IDs - Mandatory
    object MandatoryElements {
        const val DCA = "DCA"  // Jurisdiction-specific vehicle class (DL)
        const val DCB = "DCB"  // Jurisdiction-specific restriction codes (DL)
        const val DCD = "DCD"  // Jurisdiction-specific endorsement codes (DL)
        const val DBA = "DBA"  // Document Expiration Date (F8N)
        const val DCS = "DCS"  // Customer Family Name (V40ANS)
        const val DAC = "DAC"  // Customer First Name (V40ANS)
        const val DAD = "DAD"  // Customer Middle Name(s) (V40ANS)
        const val DBD = "DBD"  // Document Issue Date (F8N)
        const val DBB = "DBB"  // Date of Birth (F8N)
        const val DBC = "DBC"  // Physical Description – Sex (F1N)
        const val DAY = "DAY"  // Physical Description – Eye Color (F3A)
        const val DAU = "DAU"  // Physical Description – Height (F6ANS)
        const val DAG = "DAG"  // Address – Street 1 (V35ANS)
        const val DAI = "DAI"  // Address – City (V20ANS)
        const val DAJ = "DAJ"  // Address – Jurisdiction Code (F2A)
        const val DAK = "DAK"  // Address – Postal Code (F11ANS)
        const val DAQ = "DAQ"  // Customer ID Number (V25ANS)
        const val DCF = "DCF"  // Document Discriminator (V25ANS)
        const val DCG = "DCG"  // Country Identification (F3A)
        const val DDE = "DDE"  // Family name truncation (F1A)
        const val DDF = "DDF"  // First name truncation (F1A)
        const val DDG = "DDG"  // Middle name truncation (F1A)
    }
    
    object OptionalElements {
        const val DAH = "DAH"  // Address – Street 2 (V35ANS)
        const val DAZ = "DAZ"  // Hair color (V12A)
        const val DCI = "DCI"  // Place of birth (V33A)
        const val DCJ = "DCJ"  // Audit information (V25ANS)
        const val DCK = "DCK"  // Inventory control number (V25ANS)
        const val DBN = "DBN"  // Alias / AKA Family Name (V10ANS)
        const val DBG = "DBG"  // Alias / AKA Given Name (V15ANS)
        const val DBS = "DBS"  // Alias / AKA Suffix Name (V5ANS)
        const val DCU = "DCU"  // Name Suffix (V5ANS)
        const val DCE = "DCE"  // Physical Description – Weight Range (F1N)
        const val DCL = "DCL"  // Race / ethnicity (V3A)
        const val DCM = "DCM"  // Standard vehicle classification (F4AN)
        const val DCN = "DCN"  // Standard endorsement code (F5AN)
        const val DCO = "DCO"  // Standard restriction code (F12AN)
        const val DCP = "DCP"  // Jurisdiction-specific vehicle classification description (V50ANS)
        const val DCQ = "DCQ"  // Jurisdiction-specific endorsement code description (V50ANS)
        const val DCR = "DCR"  // Jurisdiction-specific restriction code description (V50ANS)
        const val DDA = "DDA"  // Compliance Type (F1A)
        const val DDB = "DDB"  // Card Revision Date (F8N)
        const val DDC = "DDC"  // HAZMAT Endorsement Expiration Date (F8N)
        const val DDD = "DDD"  // Limited Duration Document Indicator (F1N)
        const val DAW = "DAW"  // Weight (pounds) (F3N)
        const val DAX = "DAX"  // Weight (kilograms) (F3N)
        const val DDH = "DDH"  // Under 18 Until (F8N)
        const val DDI = "DDI"  // Under 19 Until (F8N)
        const val DDJ = "DDJ"  // Under 21 Until (F8N)
        const val DDK = "DDK"  // Organ Donor Indicator (F1N)
        const val DDL = "DDL"  // Veteran Indicator (F1N)
    }
    
    // Eye Color Codes (ANSI D-20)
    object EyeColorCodes {
        const val BLACK = "BLK"
        const val BLUE = "BLU"
        const val BROWN = "BRO"
        const val GRAY = "GRY"
        const val GREEN = "GRN"
        const val HAZEL = "HAZ"
        const val MAROON = "MAR"
        const val PINK = "PNK"
        const val UNKNOWN = "UNK"
    }
}