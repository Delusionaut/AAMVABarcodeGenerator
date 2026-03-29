/**
 * AAMVA Barcode Parser for Photopea
 * 
 * INSTRUCTIONS:
 * 1. Open your PSD template in Photopea
 * 2. Run this script (File > Scripts > Run)
 * 3. Copy the barcode data from the AAMVA Barcode Generator app
 * 4. Paste when prompted
 * 5. Text layers with matching names will be populated
 * 
 * LAYER NAMING:
 * Name your text layers using these field codes or readable names:
 * 
 * Field Code | Readable Name       | Description
 * -----------|---------------------|--------------------------------
 * DCS        | Surname             | Last Name / Family Name
 * DAC        | FirstName           | First Name / Given Name
 * DAD        | MiddleName          | Middle Name(s)
 * DBA        | ExpirationDate      | Document Expiration Date (MMDDYYYY)
 * DBD        | IssueDate           | Document Issue Date (MMDDYYYY)
 * DBB        | DateOfBirth         | Date of Birth (MMDDYYYY)
 * DBC        | Sex                 | Sex (1=Male, 2=Female)
 * DAY        | EyeColor            | Eye Color (BLK, BLU, BRO, etc.)
 * DAU        | Height              | Height (e.g., "5-09" or "509")
 * DAG        | StreetAddress       | Street Address Line 1
 * DAH        | StreetAddress2      | Street Address Line 2
 * DAI        | City                | City
 * DAJ        | State               | State/Jurisdiction Code (e.g., FL)
 * DAK        | PostalCode          | ZIP/Postal Code
 * DAQ        | IDNumber            | Driver's License Number
 * DCF        | DocumentDiscriminator| Document Discriminator
 * DCG        | Country             | Country (USA, CAN)
 * DAW        | Weight              | Weight in pounds
 * DAX        | WeightKg            | Weight in kilograms
 * DDE        | SurnameTruncated    | Truncation indicator for surname
 * DDF        | FirstNameTruncated  | Truncation indicator for first name
 * DDG        | MiddleNameTruncated | Truncation indicator for middle name
 * DAZ        | HairColor           | Hair Color
 * DDN        | Under18Date         | Under 18 until date
 * DDK        | OrganDonor          | Organ Donor (1=Yes)
 * DDL        | Veteran             | Veteran Indicator
 * DCA        | VehicleClass        | Vehicle Class
 * DCB        | Restrictions        | Restriction Codes
 * DCD        | Endorsements        | Endorsement Codes
 */

(function() {
    // ==================== FIELD CODE MAPPING ====================
    const FIELD_MAPPING = {
        // Primary identifiers
        'DCS': ['Surname', 'LastName', 'FamilyName', 'LName', 'Last_Name', 'CustomerFamilyName'],
        'DAC': ['FirstName', 'GivenName', 'FName', 'First_Name', 'CustomerFirstName'],
        'DAD': ['MiddleName', 'MiddleNames', 'MName', 'Middle_Name'],
        
        // Dates
        'DBA': ['ExpirationDate', 'ExpiryDate', 'ExpDate', 'DocumentExpirationDate', 'LicExpDate'],
        'DBD': ['IssueDate', 'DateIssued', 'IssuedDate', 'DocumentIssueDate', 'LicIssueDate', 'Issue_Date'],
        'DBB': ['DateOfBirth', 'DOB', 'BirthDate', 'Birthday', 'DOB_Date'],
        
        // Physical description
        'DBC': ['Sex', 'Gender', 'SexCode'],
        'DAY': ['EyeColor', 'Eyes', 'Eye_Color'],
        'DAU': ['Height', 'HeightInches', 'PersonHeight'],
        'DAW': ['Weight', 'WeightLbs', 'Weight_Pounds'],
        'DAX': ['WeightKg', 'WeightKilograms'],
        'DAZ': ['HairColor', 'Hair', 'Hair_Color'],
        
        // Address
        'DAG': ['StreetAddress', 'Address', 'Street', 'Address1', 'StreetAddress1', 'Addr1', 'Street_1'],
        'DAH': ['StreetAddress2', 'Address2', 'StreetAddressLine2', 'Addr2', 'Street_2', 'Apt', 'Unit'],
        'DAI': ['City', 'AddressCity', 'Addr_City'],
        'DAJ': ['State', 'StateCode', 'Jurisdiction', 'AddressState', 'Addr_State', 'State_Abbrev'],
        'DAK': ['PostalCode', 'ZIP', 'ZipCode', 'ZIPCode', 'Postal_Code', 'AddressZIP', 'Addr_ZIP', 'AddressPostalCode'],
        
        // ID numbers
        'DAQ': ['IDNumber', 'LicenseNumber', 'DLNumber', 'DriversLicense', 'DriversLicenseNumber', 'LicNumber', 'ID_Number', 'License_ID'],
        'DCF': ['DocumentDiscriminator', 'DocDiscriminator', 'DocumentDiscrim', 'DocDiscrim'],
        'DCG': ['Country', 'CountryCode', 'CountryIdentification', 'IssuingCountry'],
        
        // Truncation flags
        'DDE': ['SurnameTruncated', 'LastNameTruncated', 'LNameTrunc'],
        'DDF': ['FirstNameTruncated', 'GivenNameTruncated', 'FNameTrunc'],
        'DDG': ['MiddleNameTruncated', 'MNameTrunc'],
        
        // Additional fields
        'DDK': ['OrganDonor', 'OrganDonorStatus', 'Donor', 'DonorStatus', 'Donor_Indicator'],
        'DDL': ['Veteran', 'VeteranIndicator', 'Veteran_Status'],
        'DDC': ['HAZMATExpiry', 'HazmatExpiration', 'HazmatExp'],
        'DCE': ['WeightRange', 'WeightRangeCode'],
        'DDA': ['ComplianceType', 'Compliance'],
        'DDB': ['CardRevisionDate', 'RevisionDate'],
        'DDD': ['LimitedDuration', 'LimitedDurationDoc'],
        'DCI': ['PlaceOfBirth', 'BirthPlace', 'POB'],
        'DCJ': ['AuditInfo', 'AuditInformation'],
        'DCK': ['InventoryControl', 'InventoryControlNumber'],
        
        // Alias fields
        'DBN': ['AliasSurname', 'AliasLastName', 'AKALastName'],
        'DBG': ['AliasFirstName', 'AKAFirstName', 'AKAGivenName'],
        'DBS': ['AliasSuffix', 'AKASuffix'],
        'DCU': ['NameSuffix', 'Suffix', 'Suffix_Name'],
        
        // Vehicle fields
        'DCA': ['VehicleClass', 'VehicleClassCode', 'Class', 'DL_Class', 'LicenseClass'],
        'DCB': ['Restrictions', 'RestrictionCodes', 'DL_Restrictions'],
        'DCD': ['Endorsements', 'EndorsementCodes', 'DL_Endorsements'],
        'DCM': ['StandardVehicleClass', 'StandardClass'],
        'DCN': ['StandardEndorsements', 'StandardEndorse'],
        'DCO': ['StandardRestrictions', 'StandardRestrict'],
        'DCP': ['JurisdictionVehicleClass', 'JurisVehicleClass'],
        'DCQ': ['JurisdictionEndorsements', 'JurisEndorse'],
        'DCR': ['JurisdictionRestrictions', 'JurisRestrict'],
        
        // Dates - Additional
        'DDH': ['Under18Until', 'Under18_Date', 'Under18Date'],
        'DDI': ['Under19Until', 'Under19_Date'],
        'DDJ': ['Under21Until', 'Under21_Date'],
        
        // Other
        'DCL': ['RaceEthnicity', 'Race', 'Ethnicity'],
        'DCN': ['StandardEndorsementCode'],
        'DCU': ['SuffixName'],
    };
    
    // ==================== PARSER ====================
    function parseAAMVABarcode(rawData) {
        const result = {};
        
        // Clean the input
        let data = rawData.trim();
        
        // Remove header if present (everything before first proper field)
        // The barcode starts with @ANSI 636000... format
        if (data.startsWith('@')) {
            // Find the first real field (after header)
            const firstNewline = data.indexOf('\n');
            if (firstNewline > 0 && firstNewline < 30) {
                data = data.substring(firstNewline + 1);
            }
        }
        
        // Split by newlines (field separator)
        const lines = data.split(/\r?\n/);
        
        for (const line of lines) {
            // Skip empty lines and segment terminators
            if (!line || line === '\r' || line === '$') continue;
            
            // Extract field code (first 3 characters) and value (rest)
            if (line.length >= 4) {
                const fieldCode = line.substring(0, 3);
                let value = line.substring(3).trim();
                
                // Handle truncation indicators (T, N, U)
                if (['DDE', 'DDF', 'DDG'].includes(fieldCode)) {
                    value = expandTruncationCode(value);
                }
                
                // Handle date formatting
                if (['DBA', 'DBD', 'DBB', 'DDC', 'DDB', 'DDH', 'DDI', 'DDJ'].includes(fieldCode)) {
                    value = formatDate(value);
                }
                
                // Handle sex codes
                if (fieldCode === 'DBC') {
                    value = expandSexCode(value);
                }
                
                // Handle country codes
                if (fieldCode === 'DCG') {
                    value = expandCountryCode(value);
                }
                
                // Handle organ donor
                if (fieldCode === 'DDK') {
                    value = expandDonorStatus(value);
                }
                
                // Handle eye color
                if (fieldCode === 'DAY') {
                    value = expandEyeColor(value);
                }
                
                // Handle veteran
                if (fieldCode === 'DDL') {
                    value = expandVeteranStatus(value);
                }
                
                result[fieldCode] = value;
            }
        }
        
        return result;
    }
    
    function formatDate(dateStr) {
        // AAMVA dates are MMDDYYYY format
        if (!dateStr || dateStr.length !== 8) return dateStr;
        
        const month = dateStr.substring(0, 2);
        const day = dateStr.substring(2, 4);
        const year = dateStr.substring(4, 8);
        
        // Return as MM/DD/YYYY for readability
        return `${month}/${day}/${year}`;
    }
    
    function expandSexCode(code) {
        const codes = {
            '1': 'Male',
            '2': 'Female',
            '9': 'Not Specified'
        };
        return codes[code] || code;
    }
    
    function expandCountryCode(code) {
        const codes = {
            'USA': 'United States',
            'CAN': 'Canada',
            'MEX': 'Mexico'
        };
        return codes[code] || code;
    }
    
    function expandDonorStatus(code) {
        if (code === '1') return 'Yes';
        if (code === '2') return 'No';
        return code;
    }
    
    function expandVeteranStatus(code) {
        if (code === '1') return 'Veteran';
        return 'Not a Veteran';
    }
    
    function expandTruncationCode(code) {
        const codes = {
            'T': 'Truncated',
            'N': 'Not Truncated',
            'U': 'Unknown'
        };
        return codes[code] || code;
    }
    
    function expandEyeColor(code) {
        const colors = {
            'BLK': 'Black',
            'BLU': 'Blue',
            'BRO': 'Brown',
            'GRY': 'Gray',
            'GRN': 'Green',
            'HAZ': 'Hazel',
            'MAR': 'Maroon',
            'PNK': 'Pink',
            'UNK': 'Unknown'
        };
        return colors[code] || code;
    }
    
    // ==================== LAYER UPDATER ====================
    function updateTextLayers(doc, parsedData) {
        let updateCount = 0;
        let skippedCount = 0;
        const notFoundFields = [];
        const matchedLayers = [];
        
        // Get all layers
        const layers = doc.layers;
        
        // Build reverse mapping (layer name -> field code)
        const nameToFieldCode = {};
        for (const [fieldCode, names] of Object.entries(FIELD_MAPPING)) {
            for (const name of names) {
                nameToFieldCode[name.toLowerCase()] = fieldCode;
                nameToFieldCode[name] = fieldCode;
            }
        }
        
        // Update each layer
        for (let i = 0; i < layers.length; i++) {
            const layer = layers[i];
            
            // Skip non-text layers
            if (!layer.text || !layer.text.itemLink) continue;
            
            // Get layer name (handle layer sets)
            const layerName = layer.name;
            
            // Try to find a matching field code
            let fieldCode = null;
            
            // Direct field code match
            if (parsedData.hasOwnProperty(layerName)) {
                fieldCode = layerName;
            }
            // Check against mapping
            else {
                const lowerName = layerName.toLowerCase();
                for (const [code, names] of Object.entries(FIELD_MAPPING)) {
                    if (names.some(n => n.toLowerCase() === lowerName || n === layerName)) {
                        fieldCode = code;
                        break;
                    }
                }
            }
            
            // Update if we have a match
            if (fieldCode && parsedData.hasOwnProperty(fieldCode)) {
                try {
                    // Get the text item
                    const textItem = layer.text.itemLink;
                    
                    // Preserve all existing properties
                    const oldText = textItem.contents;
                    
                    // Only update the text content
                    textItem.contents = parsedData[fieldCode];
                    
                    updateCount++;
                    matchedLayers.push({
                        layer: layerName,
                        field: fieldCode,
                        oldValue: oldText,
                        newValue: parsedData[fieldCode]
                    });
                } catch (e) {
                    skippedCount++;
                    console.log('Error updating layer ' + layerName + ': ' + e);
                }
            } else if (layerName.length <= 4 || 
                       Object.keys(FIELD_MAPPING).includes(layerName) ||
                       Object.values(FIELD_MAPPING).flat().some(n => n.toLowerCase() === layerName.toLowerCase())) {
                // Track fields that exist in mapping but not in barcode data
                notFoundFields.push(layerName);
            }
        }
        
        return {
            updateCount,
            skippedCount,
            notFoundFields,
            matchedLayers
        };
    }
    
    // ==================== MAIN EXECUTION ====================
    function main() {
        // Check if document is open
        if (!app.activeDocument) {
            alert('No document is open. Please open a PSD file first.');
            return;
        }
        
        // Prompt user to paste barcode data
        const rawData = prompt(
            'AAMVA Barcode Parser\n\n' +
            'Paste the raw barcode data from the AAMVA Barcode Generator app.\n\n' +
            'You can copy the barcode data from the app and paste it here.',
            ''
        );
        
        // User cancelled
        if (rawData === null || rawData.trim() === '') {
            alert('No data provided. Operation cancelled.');
            return;
        }
        
        // Parse the barcode
        const parsedData = parseAAMVABarcode(rawData);
        
        // Check if we got any data
        if (Object.keys(parsedData).length === 0) {
            alert('Error: Could not parse any valid data from the barcode.\n\n' +
                  'Please make sure you copied the complete barcode data.');
            return;
        }
        
        // Log parsed data for debugging
        console.log('=== PARSED DATA ===');
        for (const [key, value] of Object.entries(parsedData)) {
            console.log(key + ': ' + value);
        }
        
        // Update layers
        const result = updateTextLayers(app.activeDocument, parsedData);
        
        // Show results
        let message = 'Processing Complete!\n\n';
        message += 'Updated: ' + result.updateCount + ' layers\n';
        message += 'Skipped: ' + result.skippedCount + ' layers\n';
        
        if (result.matchedLayers.length > 0) {
            message += '\n=== UPDATED LAYERS ===\n';
            for (const match of result.matchedLayers) {
                message += match.layer + ' (' + match.field + '): ' + match.newValue + '\n';
            }
        }
        
        if (result.notFoundFields.length > 0) {
            message += '\n=== LAYERS WITH NO DATA ===\n';
            message += 'These layer names exist but no matching data was found:\n';
            for (const field of result.notFoundFields) {
                message += '- ' + field + '\n';
            }
        }
        
        alert(message);
        
        // Also log to console for detailed view
        console.log('=== RESULT ===');
        console.log('Updated: ' + result.updateCount + ' layers');
        console.log('Skipped: ' + result.skippedCount + ' layers');
    }
    
    // Run the script
    main();
})();
