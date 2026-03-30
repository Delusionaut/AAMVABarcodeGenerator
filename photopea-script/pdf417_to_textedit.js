/**
 * PDF417 to TextEdit Script for AAMVA Barcode Data
 * 
 * INSTRUCTIONS:
 * 1. Open your PSD template in Photopea
 * 2. Run this script (File > Scripts > Run)
 * 3. Copy the barcode data from the AAMVA Barcode Generator app
 * 4. Paste when prompted
 * 5. Text layers with matching names will be populated
 * 
 * LAYER NAME MAPPING:
 * ===================
 * Standard Layers:
 * - WGT     → Weight (pounds)
 * - ISS     → Issue Date (MMDDYYYY)
 * - DD      → Document Discriminator
 * - EYES    → Eye Color
 * - DLN     → Driver's License Number / Unique Document Number
 * - EXP     → Expiry Date
 * - CLASS   → Motor Vehicle Classification
 * - LN      → Last Name
 * - ADD     → Street Address
 * - FN      → First Name
 * - HAIR    → Hair Color
 * - END     → Endorsements
 * - RSTR    → Restrictions
 * - SEX     → Gender (AAMVA: 1=M, 2=F)
 * - DOB01   → Date of Birth (MM/DD/YYYY format)
 * 
 * Special Computed Layers:
 * ========================
 * FLDOB03  → First letter of First Name + First letter of Last Name + Last 2 digits of DOB
 *             Example: WA92 (W from "William" + A from "Anderson" + 92 from "1992-05-15")
 * 
 * FLDOB02  → Same as FLDOB03
 * 
 * FLDOB    → Same as FLDOB02 and FLDOB03
 * 
 * DOB02    → Date of Birth in MMDDYYYY format (no slashes)
 *             Example: 05151992
 * 
 * DOB03    → Date of Birth in MMDDYY format
 *             Example: 0592
 * 
 * DOB04    → Date of Birth in MMDDYY format
 *             Example: 0592
 * 
 * LOC      → City + State + ZIP Code combined
 *             Example: "Los Angeles, CA 90019"
 *             (Combines: City field + ", " + State + " " + ZIP)
 */

(function() {
    // ==================== FIELD CODE MAPPING ====================
    // Maps layer names to their data source
    const FIELD_MAPPING = {
        // Standard AAMVA Field Codes
        'DCS': ['LN', 'LastName', 'Last_Name', 'Surname', 'FamilyName'],
        'DAC': ['FN', 'FirstName', 'First_Name', 'GivenName'],
        'DBA': ['EXP', 'Expiry', 'ExpiryDate', 'ExpirationDate', 'ExpDate', 'Expiration'],
        'DBD': ['ISS', 'IssueDate', 'Issue_Date', 'DateIssued'],
        'DBB': ['DOB01', 'DateOfBirth', 'DOB', 'BirthDate', 'Birthday'],
        'DBC': ['SEX', 'Gender', 'Sex'],
        'DAY': ['EYES', 'EyeColor', 'Eye_Color'],
        'DAW': ['WGT', 'Weight', 'WeightLbs'],
        'DCF': ['DD', 'DocDisc', 'DocumentDiscriminator'],
        'DAG': ['ADD', 'Address', 'StreetAddress'],
        'DAZ': ['HAIR', 'HairColor', 'Hair_Color'],
        'DCA': ['CLASS', 'VehicleClass', 'DL_Class', 'LicenseClass'],
        'DCB': ['RSTR', 'Restrictions', 'DL_Restrictions'],
        'DCD': ['END', 'Endorsements', 'DL_Endorsements'],
        'DAQ': ['DLN', 'LicenseNumber', 'DLNumber', 'DriversLicense', 'IDNumber'],
        
        // Address components
        'DAI': ['City'],
        'DAJ': ['State'],
        'DAK': ['ZIP', 'ZipCode', 'PostalCode'],
    };

    // Eye color codes (AAMVA standard)
    const EYE_COLORS = {
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

    // Hair color codes (AAMVA standard)
    const HAIR_COLORS = {
        'BAL': 'Bald',
        'BLK': 'Black',
        'BLN': 'Blond',
        'BRO': 'Brown',
        'GRY': 'Gray',
        'RED': 'Red',
        'WHI': 'White',
        'UNK': 'Unknown'
    };

    // Sex/Gender codes
    // AAMVA Standard: 1=Male, 2=Female
    // User Specification: 0=Female, 1=Male
    const SEX_CODES = {
        '1': 'M',
        '2': 'F'
    };

    // ==================== HELPER FUNCTIONS ====================

    /**
     * Format date from MMDDYYYY to MM/DD/YYYY
     */
    function formatDateSlash(dateStr) {
        if (!dateStr || dateStr.length !== 8) return dateStr;
        return dateStr.substring(0, 2) + '/' + dateStr.substring(2, 4) + '/' + dateStr.substring(4, 8);
    }

    /**
     * Format date from MMDDYYYY to MMDDYYYY (no slashes)
     */
    function formatDateMMDDYYYY(dateStr) {
        if (!dateStr || dateStr.length !== 8) return dateStr;
        return dateStr;
    }

    /**
     * Format date from MMDDYYYY to MMDDYY
     */
    function formatDateMMDDYY(dateStr) {
        if (!dateStr || dateStr.length !== 8) return dateStr;
        return dateStr.substring(0, 4) + dateStr.substring(6, 8);
    }

    /**
     * Translate sex code
     * AAMVA Standard: 1=Male, 2=Female
     */
    function translateSex(code) {
        if (code === '1') return 'M';
        if (code === '2') return 'F';
        return code;
    }

    /**
     * Translate eye color code
     */
    function translateEyeColor(code) {
        return EYE_COLORS[code] || code;
    }

    /**
     * Translate hair color code
     * Following AAMVA standard: BAL, BLK, BLN, BRO, GRY, RED, WHI, UNK
     */
    function translateHairColor(code) {
        return HAIR_COLORS[code] || code;
    }

    /**
     * Compute FLDOB value:
     * First letter of First Name + First letter of Last Name + Last 2 digits of DOB
     * Example: William Anderson, DOB 1992-05-15 → WA92
     */
    function computeFLDOB(firstName, lastName, dob) {
        if (!firstName || !lastName || !dob) return '';
        
        const firstInitial = firstName.charAt(0).toUpperCase();
        const lastInitial = lastName.charAt(0).toUpperCase();
        
        // Get last 2 digits of year from DOB (assuming MMDDYYYY format)
        let lastTwoDigits = '';
        if (dob.length === 8) {
            lastTwoDigits = dob.substring(6, 8);
        } else if (dob.length === 4) {
            // If already MMDDYY
            lastTwoDigits = dob.substring(2, 4);
        }
        
        return firstInitial + lastInitial + lastTwoDigits;
    }

    /**
     * Compute [LOC] value:
     * City + ", " + State + " " + ZIP
     * Example: Los Angeles, CA 90019
     */
    function computeLOC(city, state, zip) {
        const parts = [];
        if (city) parts.push(city);
        if (state) parts.push(state);
        if (zip) parts.push(zip);
        
        if (parts.length >= 3) {
            return parts[0] + ', ' + parts[1] + ' ' + parts[2];
        } else if (parts.length === 2) {
            return parts[0] + ', ' + parts[1];
        } else if (parts.length === 1) {
            return parts[0];
        }
        return '';
    }

    // ==================== PARSER ====================
    function parseAAMVABarcode(rawData) {
        const result = {};
        
        // Clean the input
        let data = rawData.trim();
        
        // Remove header if present (@ANSI 636000...)
        if (data.startsWith('@')) {
            const firstNewline = data.indexOf('\n');
            if (firstNewline > 0 && firstNewline < 30) {
                data = data.substring(firstNewline + 1);
            }
        }
        
        // Split by newlines (field separator)
        const lines = data.split(/\r?\n/);
        
        for (const line of lines) {
            // Skip empty lines
            if (!line || line === '\r' || line === '$') continue;
            
            // Extract field code (first 3 characters) and value (rest)
            if (line.length >= 4) {
                const fieldCode = line.substring(0, 3);
                let value = line.substring(3).trim();
                
                // Store raw value for computed fields
                result[fieldCode] = value;
            }
        }
        
        // Compute special fields
        const firstName = result['DAC'] || '';
        const lastName = result['DCS'] || '';
        const dob = result['DBB'] || '';
        const city = result['DAI'] || '';
        const state = result['DAJ'] || '';
        const zip = result['DAK'] || '';
        
        // Computed fields
        result['FLDOB03'] = computeFLDOB(firstName, lastName, dob);
        result['FLDOB02'] = computeFLDOB(firstName, lastName, dob);
        result['FLDOB'] = computeFLDOB(firstName, lastName, dob);
        result['DOB02'] = formatDateMMDDYYYY(dob);
        result['DOB03'] = formatDateMMDDYY(dob);
        result['DOB04'] = formatDateMMDDYY(dob);
        result['LOC'] = computeLOC(city, state, zip);
        
        // Format dates for standard layers
        result['DOB01'] = formatDateSlash(dob);
        result['ISS_FORMATTED'] = formatDateSlash(result['DBD'] || '');
        result['EXP_FORMATTED'] = formatDateSlash(result['DBA'] || '');
        
        // Translate codes
        result['SEX_TRANSLATED'] = translateSex(result['DBC'] || '');
        result['EYES_TRANSLATED'] = translateEyeColor(result['DAY'] || '');
        result['HAIR_TRANSLATED'] = translateHairColor(result['DAZ'] || '');
        
        return result;
    }

    // ==================== LAYER UPDATER ====================
    function updateTextLayers(doc, parsedData) {
        let updateCount = 0;
        let skippedCount = 0;
        const notFoundFields = [];
        const matchedLayers = [];
        
        // Build reverse mapping: layer name -> field key in parsedData
        const nameToFieldKey = {};
        
        // Map standard field codes and aliases
        for (const [fieldCode, aliases] of Object.entries(FIELD_MAPPING)) {
            // Direct field code mapping
            nameToFieldKey[fieldCode] = fieldCode;
            nameToFieldKey[fieldCode.toLowerCase()] = fieldCode;
            
            // Alias mappings
            for (const alias of aliases) {
                nameToFieldKey[alias] = fieldCode;
                nameToFieldKey[alias.toLowerCase()] = fieldCode;
            }
        }
        
        // Special computed field layers
        nameToFieldKey['FLDOB03'] = 'FLDOB03';
        nameToFieldKey['FLDOB02'] = 'FLDOB02';
        nameToFieldKey['FLDOB'] = 'FLDOB';
        nameToFieldKey['DOB02'] = 'DOB02';
        nameToFieldKey['DOB03'] = 'DOB03';
        nameToFieldKey['DOB04'] = 'DOB04';
        nameToFieldKey['LOC'] = 'LOC';
        
        // Standard field layers
        nameToFieldKey['WGT'] = 'DAW';
        nameToFieldKey['ISS'] = 'ISS_FORMATTED';
        nameToFieldKey['DD'] = 'DCF';
        nameToFieldKey['EYES'] = 'EYES_TRANSLATED';
        nameToFieldKey['DLN'] = 'DAQ';
        nameToFieldKey['EXP'] = 'EXP_FORMATTED';
        nameToFieldKey['CLASS'] = 'DCA';
        nameToFieldKey['LN'] = 'DCS';
        nameToFieldKey['ADD'] = 'DAG';
        nameToFieldKey['FN'] = 'DAC';
        nameToFieldKey['HAIR'] = 'HAIR_TRANSLATED';
        nameToFieldKey['END'] = 'DCD';
        nameToFieldKey['RSTR'] = 'DCB';
        nameToFieldKey['SEX'] = 'SEX_TRANSLATED';
        nameToFieldKey['DOB01'] = 'DOB01';
        
        // Get all layers
        const layers = doc.layers;
        
        // Update each layer
        for (let i = 0; i < layers.length; i++) {
            const layer = layers[i];
            
            // Skip non-text layers
            if (!layer.text || !layer.text.itemLink) continue;
            
            const layerName = layer.name;
            const fieldKey = nameToFieldKey[layerName];
            
            if (fieldKey && parsedData.hasOwnProperty(fieldKey)) {
                try {
                    const textItem = layer.text.itemLink;
                    const newValue = parsedData[fieldKey];
                    
                    if (newValue !== undefined && newValue !== '') {
                        textItem.contents = newValue;
                        updateCount++;
                        matchedLayers.push({
                            layer: layerName,
                            field: fieldKey,
                            value: newValue
                        });
                    } else {
                        skippedCount++;
                        notFoundFields.push(layerName + ' (empty data)');
                    }
                } catch (e) {
                    skippedCount++;
                    console.log('Error updating layer ' + layerName + ': ' + e);
                }
            } else {
                // Track layers that might need data but don't have it
                if (nameToFieldKey.hasOwnProperty(layerName) || nameToFieldKey.hasOwnProperty(layerName.toLowerCase())) {
                    notFoundFields.push(layerName);
                }
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
            'PDF417 to TextEdit\n\n' +
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
                message += match.layer + ' = ' + match.value + '\n';
            }
        }
        
        if (result.notFoundFields.length > 0) {
            message += '\n=== LAYERS WITH NO DATA ===\n';
            for (const field of result.notFoundFields) {
                message += '- ' + field + '\n';
            }
        }
        
        alert(message);
        
        // Log to console
        console.log('=== RESULT ===');
        console.log('Updated: ' + result.updateCount + ' layers');
        console.log('Skipped: ' + result.skippedCount + ' layers');
    }
    
    // Run the script
    main();
})();
