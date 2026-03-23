package com.aamva.barcodegenerator.model

/**
 * Represents the complete AAMVA data set for a driver license or ID card.
 * This includes all mandatory and optional data elements organized by subfiles.
 */
data class AAMVADataSet(
    // Header Information
    val issuerIdentificationNumber: String,  // 6-digit IIN
    val aamvaVersionNumber: String = "10",   // Version 10 for 2020 standard
    val jurisdictionVersionNumber: String = "00",
    
    // Subfile Type (DL, ID, or EN)
    val subfileType: CardType = CardType.DL,
    
    // Customer Information - Mandatory
    val customerFamilyName: String,
    val customerFirstName: String,
    val customerMiddleName: String = "",
    val dateOfBirth: String,          // MMDDYYYY (US) or CCYYMMDD (Canada)
    val dateOfIssue: String,          // MMDDYYYY (US) or CCYYMMDD (Canada)
    val dateOfExpiry: String,         // MMDDYYYY (US) or CCYYMMDD (Canada)
    val customerIdNumber: String,
    val documentDiscriminator: String,
    val countryIdentification: String = "USA",  // USA or CAN
    
    // Physical Description - Mandatory
    val sex: String,                  // 1=male, 2=female, 9=not specified
    val eyeColor: String,             // 3-letter ANSI code
    val height: String,               // "073 in" or "181 cm"
    
    // Optional Physical Description
    val hairColor: String = "",       // DAZ V12A
    val weightRange: String = "",     // DCE F1N
    
    // Address - Mandatory
    val addressStreet1: String,
    val addressCity: String,
    val addressJurisdictionCode: String,  // 2-letter state code
    val addressPostalCode: String,
    val addressStreet2: String = "",  // DAH Optional
    
    // Vehicle Information - Mandatory for DL
    val vehicleClass: String = "",        // DCA
    val restrictionCodes: String = "",    // DCB
    val endorsementCodes: String = "",    // DCD
    
    // Name Truncation - Mandatory
    val familyNameTruncation: String = "N",   // DDE F1A
    val firstNameTruncation: String = "N",    // DDF F1A
    val middleNameTruncation: String = "N",   // DDG F1A
    
    // Optional
    val nameSuffix: String = "",      // DCU V5ANS
    val placeOfBirth: String = "",    // DCI V33A
    val auditInfo: String = "",       // DCJ V25ANS
    val inventoryControl: String = "", // DCK V25ANS
    val complianceType: String = "",  // DDA F1A
    val cardRevisionDate: String = "", // DDB F8N
    val hazmatExpiry: String = "",    // DDC F8N
    val limitedDuration: String = "", // DDD F1N
)