package com.aamva.barcodegenerator.util

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.pdf417.PDF417Writer

/**
 * Utility class for rendering PDF417 barcodes.
 * Uses ZXing library for PDF417 barcode generation.
 */
object BarcodeFormatter {
    
    /**
     * Generates a PDF417 barcode bitmap from the encoded data.
     * 
     * @param data The AAMVA-encoded data string
     * @param width The desired width of the barcode
     * @param height The desired height of the barcode
     * @return A Bitmap containing the PDF417 barcode
     */
    fun generatePDF417Bitmap(
        data: String,
        width: Int = 800,
        height: Int = 300
    ): Bitmap? {
        return try {
            val hints: MutableMap<EncodeHintType, Any> = hashMapOf(
                EncodeHintType.PDF417_COMPACT to false,
                EncodeHintType.PDF417_AUTO_ECI to true,
                EncodeHintType.CHARACTER_SET to "ISO-8859-1",
                EncodeHintType.MARGIN to 1
            )
            
            val writer = PDF417Writer()
            val bitMatrix = writer.encode(
                data,
                BarcodeFormat.PDF_417,
                width,
                height,
                hints
            )
            
            createBitmapFromBitMatrix(bitMatrix)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Generates a PDF417 barcode with custom error correction level.
     * 
     * @param data The AAMVA-encoded data string
     * @param width The desired width of the barcode
     * @param height The desired height of the barcode
     * @param errorCorrectionLevel The PDF417 error correction level (0-8)
     * @return A Bitmap containing the PDF417 barcode
     */
    fun generatePDF417BitmapWithECL(
        data: String,
        width: Int = 800,
        height: Int = 300,
        errorCorrectionLevel: Int = 3
    ): Bitmap? {
        return try {
            val hints: MutableMap<EncodeHintType, Any> = hashMapOf(
                EncodeHintType.PDF417_COMPACT to false,
                EncodeHintType.PDF417_AUTO_ECI to true,
                EncodeHintType.ERROR_CORRECTION to errorCorrectionLevel,
                EncodeHintType.CHARACTER_SET to "ISO-8859-1",
                EncodeHintType.MARGIN to 2
            )
            
            val writer = PDF417Writer()
            val bitMatrix = writer.encode(
                data,
                BarcodeFormat.PDF_417,
                width,
                height,
                hints
            )
            
            createBitmapFromBitMatrix(bitMatrix)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    private fun createBitmapFromBitMatrix(bitMatrix: BitMatrix): Bitmap {
        val width = bitMatrix.width
        val height = bitMatrix.height
        val pixels = IntArray(width * height)
        
        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] = if (bitMatrix.get(x, y)) {
                    0xFF000000.toInt() // Black
                } else {
                    0xFFFFFFFF.toInt() // White
                }
            }
        }
        
        return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
            setPixels(pixels, 0, width, 0, 0, width, height)
        }
    }
    
    /**
     * Validates that the data can be encoded as PDF417.
     * 
     * @param data The data string to validate
     * @return True if the data can be encoded
     */
    fun validatePDF417Data(data: String): Boolean {
        // PDF417 has a maximum data capacity
        // Maximum characters: ~1850 alphanumeric, ~1100 binary
        return data.isNotEmpty() && data.length <= 2000
    }
}