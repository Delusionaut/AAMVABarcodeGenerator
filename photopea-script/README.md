# PDF417 to TextEdit Script

This script parses AAMVA PDF417 barcode data and populates text layers in a PSD template within Photopea.

## Usage

1. Open your PSD template in Photopea
2. Run this script: **File > Scripts > Run**
3. Copy the barcode data from the AAMVA Barcode Generator app
4. Paste the barcode data when prompted
5. Text layers will be automatically populated

## Layer Name Mapping

### Standard Layers

| Layer Name | Description | Example Output |
|------------|-------------|----------------|
| `WGT` | Weight (pounds) | `180` |
| `ISS` | Issue Date | `01/15/2023` |
| `DD` | Document Discriminator | `XYZ123` |
| `EYES` | Eye Color (translated) | `Brown` |
| `DLN` | Driver's License Number | `D1234567` |
| `EXP` | Expiry Date | `01/15/2028` |
| `CLASS` | Motor Vehicle Classification | `C` |
| `LN` | Last Name | `Anderson` |
| `ADD` | Street Address | `123 Main St` |
| `FN` | First Name | `William` |
| `HAIR` | Hair Color (translated) | `Brown` |
| `END` | Endorsements | `B` |
| `RSTR` | Restrictions | `B` |
| `SEX` | Gender (translated) | `M` or `F` |
| `DOB01` | Date of Birth | `05/15/1992` |

### Special Computed Layers

These layers contain computed values from multiple data fields:

| Layer Name | Computation | Example |
|------------|-------------|---------|
| `FLDOB03` | First Initial + Last Initial + Last 2 digits of DOB | `WA92` |
| `FLDOB02` | Same as FLDOB03 | `WA92` |
| `FLDOB` | Same as FLDOB03 | `WA92` |
| `DOB02` | Date of Birth in MMDDYYYY format | `05151992` |
| `DOB03` | Date of Birth in MMDDYY format | `0592` |
| `DOB04` | Date of Birth in MMDDYY format | `0592` |
| `LOC` | City + ", " + State + " " + ZIP | `Los Angeles, CA 90019` |

### Code Translations

#### Eye Colors
| Code | Display |
|------|---------|
| `BLK` | Black |
| `BLU` | Blue |
| `BRO` | Brown |
| `GRY` | Gray |
| `GRN` | Green |
| `HAZ` | Hazel |
| `MAR` | Maroon |
| `PNK` | Pink |
| `UNK` | Unknown |

#### Hair Colors
| Code | Display |
|------|---------|
| `BAL` | Bald |
| `BLK` | Black |
| `BLN` | Blond |
| `BRO` | Brown |
| `GRY` | Gray |
| `RED` | Red |
| `WHI` | White |
| `UNK` | Unknown |

#### Gender (AAMVA Standard)
| Code | Display |
|------|---------|
| `1` | M |
| `2` | F |

## PSD Template Setup

Name your text layers in Photopea using the layer names listed above. The script will automatically match and populate them with the corresponding data.

## Troubleshooting

- **Layer not updating**: Check that the layer name exactly matches one in the mapping
- **Empty value**: The corresponding data field may not be populated in the barcode data
- **Wrong format**: Ensure the PSD template uses text layers (not shape layers)
